package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

@Data
public class RegisterBodyDto {
    /**
     * 验证码
     */
    String smsCode;
    String phoneNumber;
    String nickName;
    String passWord;
    
}
