package com.xingheng.xhoj.judge;

import cn.hutool.json.JSONUtil;
import com.xingheng.xhoj.common.ErrorCode;
import com.xingheng.xhoj.exception.BusinessException;
import com.xingheng.xhoj.judge.codesandbox.CodeSandbox;
import com.xingheng.xhoj.judge.codesandbox.CodeSandboxFactory;
import com.xingheng.xhoj.judge.codesandbox.CodeSandboxProxy;
import com.xingheng.xhoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xingheng.xhoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xingheng.xhoj.judge.strategy.JudgeContext;
import com.xingheng.xhoj.model.dto.question.JudgeCase;
import com.xingheng.xhoj.model.dto.questionsubmit.JudgeInfo;
import com.xingheng.xhoj.model.entity.Question;
import com.xingheng.xhoj.model.entity.QuestionSubmit;
import com.xingheng.xhoj.model.enums.QuestionSubmitStatusEnum;
import com.xingheng.xhoj.service.QuestionService;
import com.xingheng.xhoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    public String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 获取到题目提交id：获取到对应的题目，提交信息（包含代码，编程语言等等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);

        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }

        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 2. 如果不为等待状态
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "正在判题中");
        }

        // 3. 更改判题（题目提交）的状态为 “判题中”，防止重复执行，同时也能让用户看到状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        // 4. 调用沙箱获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);

        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取调用接口
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        List<String> outputList = executeCodeResponse.getOutputList();

        // 5.据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);



        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 6. 修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionId);
        return questionSubmitResult;
    }
}
