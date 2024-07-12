package com.ruoyi.mybatis.aspect;


import com.esotericsoftware.minlog.Log;

import com.ruoyi.common.utils.LoginHelper;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import com.ruoyi.mybatis.contextholder.AuthInfoContextHolder;
import com.ruoyi.mybatis.entity.AuthInfoEntity;
import com.ruoyi.mybatis.helper.ExpressionHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主要作用是获取权限判断需要的一些信息，放置于AuthInfoEntity
 */
@Component
@Aspect
public class DataScopeAuthAspect {

    @Autowired
    ExpressionHelper expressionHelper;
//    @Autowired
//    DataRuleServiceImpl dataRuleService;
    @Value("${aspect.data-scope-auth.enable:true}")
    private boolean enable;

    @Pointcut("@annotation(com.ruoyi.mybatis.annotation.DataScopeAuth)")
    private void cutPoint() {
    }

    @Around("cutPoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!this.enable) {
            return joinPoint.proceed();
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        DataScopeAuth annotation = method.getAnnotation(DataScopeAuth.class);

        AuthInfoEntity authInfoEntity = AuthInfoEntity.builder()
                .isAuth(true)
                .params(getParams(annotation))
                .build();

        AuthInfoContextHolder.setEntity(authInfoEntity);
        Object result = joinPoint.proceed(joinPoint.getArgs());

        //当被注解的方法完成后，要消除。防止影响到其他方法。
        AuthInfoContextHolder.clear();

        return result;
    }


    /**
     * 参数含义：
     * 1.ruleTable：要进行权限过滤的表名
     * 2.sqlPart：用于权限拼接的sql
     *
     *
     * @param dataScopeAuth
     * @return
     */
    private Map<String, Object> getParams(DataScopeAuth dataScopeAuth) {
        Map<String, Object> map = new HashMap<>();
        map.put("ruleTable", dataScopeAuth.ruleTable());

        // 总部的话，直接返回全部
        if(LoginHelper.isAdmin()){
            Log.info("当前用户是总部用户，无需进行数据权限过滤");
            map.put("sqlPart", "1=1 ");
            return map;
        }

//        List<DataRule> dataRule = dataRuleService.getDataRule(dataScopeAuth.ruleTable(), LoginHelper.getLoginUser().getRoles().get(0).getRoleId());

//        List<DataRule> dataRule = null;
//        map.put("sqlPart", expressionHelper.parseSql(dataRule));

        return map;
    }
}
