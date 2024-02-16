package com.xingheng.xhoj.judge;

import com.xingheng.xhoj.judge.strategy.DefaultJudgeStrategy;
import com.xingheng.xhoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.xingheng.xhoj.judge.strategy.JudgeContext;
import com.xingheng.xhoj.judge.strategy.JudgeStrategy;
import com.xingheng.xhoj.model.dto.questionsubmit.JudgeInfo;
import com.xingheng.xhoj.model.entity.Question;
import com.xingheng.xhoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理：简化调用
 */
@Service
public class JudgeManager {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }

        return judgeStrategy.doJudge(judgeContext);
    }
}
