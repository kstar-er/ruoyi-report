package com.ruoyi.colorfulfog.modules.datasettransform.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.ruoyi.colorfulfog.code.ResponseCode;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.modules.datasettransform.controller.dto.DataSetTransformDto;
import com.ruoyi.colorfulfog.modules.datasettransform.service.IGroovyHandler;
import com.ruoyi.colorfulfog.modules.datasettransform.service.TransformStrategy;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by raodeming on 2021/3/23.
 */
@Component
@Slf4j
public class GroovyTransformServiceImpl implements TransformStrategy {

    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    /**
     * 数据清洗转换 类型
     *
     * @return
     */
    @Override
    public String type() {
        return "javaBean";
    }

    /***
     * 清洗转换算法接口
     * @param def
     * @param data
     * @return
     */
    @Override
    public List<JSONObject> transform(DataSetTransformDto def, List<JSONObject> data) {
        String transformScript = def.getTransformScript();
        Class<?> clazz = groovyClassLoader.parseClass(transformScript);
        if (clazz != null) {
            try {
                Object instance = clazz.newInstance();
                if (instance!=null) {
                    if (instance instanceof IGroovyHandler) {
                        IGroovyHandler handler = (IGroovyHandler) instance;
                        return handler.transform(data);
                    } else {
                        System.err.println("转换失败！");
                    }
                }
            } catch (Exception e) {
                log.info("执行javaBean异常", e);
                throw new GlobalException( e.getMessage());
            }
        }
        return data;
    }
}
