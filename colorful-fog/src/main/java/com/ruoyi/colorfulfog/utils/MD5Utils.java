package com.ruoyi.colorfulfog.utils;

import org.springframework.util.DigestUtils;

public class MD5Utils {
    //盐，用于混交md5
    private static final String salt = "my md5 salt";
    /**
     * 生成md5,带盐
     *
     * @return
     */
    public static String getMD5WithSalt(String s) {
        String base = s + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    public static String getMD5(String s) {
        return DigestUtils.md5DigestAsHex(s.getBytes());
    }
}
