package com.ruoyi.mybatis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author zwh
 * @date 2023/2/8 10:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthInfoEntity {
    //是否开启权限校验
    private Boolean isAuth;

    //校验需要用到的其他参数。扩展项，目前没用
    private Map<String, Object> params;

    //是否为写权限校验。扩展项，目前没用
    private boolean isWrite;
}
