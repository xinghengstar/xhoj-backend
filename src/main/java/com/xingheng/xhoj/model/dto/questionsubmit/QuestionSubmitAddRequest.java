package com.xingheng.xhoj.model.dto.questionsubmit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目提交请求
 *
 * @author <a href="https://github.com/lixingheng">程序员鱼皮</a>
 * @from <a href="https://xingheng.icu">编程导航知识星球</a>
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 使用语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目 id
     */
    private Long questionId;


    private static final long serialVersionUID = 1L;
}