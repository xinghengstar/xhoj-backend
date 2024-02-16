package com.xingheng.xhoj.judge.codesandbox.impl;

import com.xingheng.xhoj.judge.codesandbox.CodeSandbox;
import com.xingheng.xhoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xingheng.xhoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xingheng.xhoj.model.dto.questionsubmit.JudgeInfo;
import com.xingheng.xhoj.model.enums.JudgeInfoMessageEnum;
import com.xingheng.xhoj.model.enums.QuestionSubmitLanguageEnum;
import com.xingheng.xhoj.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 示例代码沙箱（仅为了跑通业务流程）
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试运行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setTime(100L);
        judgeInfo.setMemory(100L);


        executeCodeResponse.setJudgeInfo(judgeInfo);


        return executeCodeResponse;
    }
}
