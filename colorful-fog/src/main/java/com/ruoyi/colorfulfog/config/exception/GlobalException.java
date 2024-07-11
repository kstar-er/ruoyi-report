package com.ruoyi.colorfulfog.config.exception;


import com.ruoyi.colorfulfog.constant.enums.ErrorCodeEnum;

public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;
    public GlobalException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
    }

    public GlobalException(String message) {
        this.message = message;
    }
    public GlobalException(String message,String  detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public GlobalException(ErrorCodeEnum errorCodeEnum, String detailMessage) {
        this.message = errorCodeEnum.getMsg() ;
        this.detailMessage = detailMessage;
    }

    @Override
    public String getMessage() {
        return message+detailMessage;
    }

    public GlobalException setMessage(String message) {
        this.message = message;
        return this;
    }
}
