package com.xingheng.xhoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingheng.xhoj.annotation.AuthCheck;
import com.xingheng.xhoj.common.BaseResponse;
import com.xingheng.xhoj.common.ErrorCode;
import com.xingheng.xhoj.common.ResultUtils;
import com.xingheng.xhoj.constant.UserConstant;
import com.xingheng.xhoj.exception.BusinessException;
import com.xingheng.xhoj.model.dto.question.QuestionQueryRequest;
import com.xingheng.xhoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xingheng.xhoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xingheng.xhoj.model.entity.Question;
import com.xingheng.xhoj.model.entity.QuestionSubmit;
import com.xingheng.xhoj.model.entity.User;
import com.xingheng.xhoj.model.vo.QuestionSubmitVO;
import com.xingheng.xhoj.service.QuestionSubmitService;
import com.xingheng.xhoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 提交题目
 *
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 提交记录id
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交（除了管理员，普通用户只能看到非答案，提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();

        // 从数据库中查到了原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));

        // 提交脱敏信息
        final User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

}
