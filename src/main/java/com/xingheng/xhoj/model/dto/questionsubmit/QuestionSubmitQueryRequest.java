package com.xingheng.xhoj.model.dto.questionsubmit;

import com.xingheng.xhoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/lixingheng">程序员鱼皮</a>
 * @from <a href="https://xingheng.icu">编程导航知识星球</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 使用语言
     */
    private String language;

    /**
     * 用户状态
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户 id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}