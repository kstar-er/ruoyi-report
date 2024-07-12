package com.ruoyi.mybatis.contextholder;


import com.ruoyi.mybatis.entity.AuthInfoEntity;
import com.ruoyi.mybatis.entity.AuthInfoEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthInfoContextHolder {

    private static final  ThreadLocal<AuthInfoEntity> threadLocal = new ThreadLocal();

    public static AuthInfoEntity getAuthEntity() {
        return threadLocal.get();
    }

    public static void setEntity(AuthInfoEntity entity) {
        threadLocal.set(entity);
    }


    public static void clear() {
        threadLocal.remove();
    }
}
