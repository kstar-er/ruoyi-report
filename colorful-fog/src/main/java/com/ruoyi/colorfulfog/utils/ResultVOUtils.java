package com.ruoyi.colorfulfog.utils;

import com.ruoyi.colorfulfog.constant.enums.ErrorCodeEnum;
import com.ruoyi.colorfulfog.model.vo.ResultVO;


public class ResultVOUtils {
    public static <T> ResultVO<T> success() {
        return success(null);
    }
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(ErrorCodeEnum.SUCCESS.getCode(),
                ErrorCodeEnum.SUCCESS.getMsg(),
                data);
    }

    public static <T> ResultVO<T> error(ErrorCodeEnum errorCodeEnum) {
        return new ResultVO<>(errorCodeEnum.getCode(),
                errorCodeEnum.getMsg(),
                null);
    }
    public static <T> ResultVO<T> error(ErrorCodeEnum errorCodeEnum,T data) {
        return new ResultVO<>(errorCodeEnum.getCode(),
                errorCodeEnum.getMsg(),
                data);
    }

    public static <T> ResultVO<T> commonResult(Integer code, String msg, T data) {
        return new ResultVO<>(code, msg, data);
    }

    public static <T> ResultVO<T> commonResult(Integer code, String msg) {
        return commonResult(code, msg, null);
    }
}
