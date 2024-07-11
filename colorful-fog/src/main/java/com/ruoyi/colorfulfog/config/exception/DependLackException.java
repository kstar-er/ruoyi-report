package com.ruoyi.colorfulfog.config.exception;

import lombok.Data;

/**
 * 依赖表数据缺失错误
 */
@Data
public class DependLackException extends RuntimeException{
    String dependCode;
    String key;
    String reason;
    public DependLackException(String dependCode, String key,String reason) {
        this.dependCode = dependCode;
        this.key = key;
        this.reason = reason;
    }
}
