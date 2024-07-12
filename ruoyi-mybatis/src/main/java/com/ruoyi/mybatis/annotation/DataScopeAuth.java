package com.ruoyi.mybatis.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScopeAuth {
    //当前线程，要进行权限检验的表名。
    String ruleTable();
}
