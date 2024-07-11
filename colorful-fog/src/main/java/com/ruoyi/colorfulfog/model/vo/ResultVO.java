package com.ruoyi.colorfulfog.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用返回类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> {

    /** 错误码 **/
    private Integer code;

    /** 提示信息 **/
    private String message;

    /** 返回内容 **/
    private T data;
}
