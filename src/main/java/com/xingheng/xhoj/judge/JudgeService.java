package com.xingheng.xhoj.judge;

import com.xingheng.xhoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xingheng.xhoj.model.entity.QuestionSubmit;
import com.xingheng.xhoj.model.vo.QuestionSubmitVO;

/**
 * 判题服务
 */
public interface JudgeService {
    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
