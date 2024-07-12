package com.ruoyi.colorfulfog.modules.datasettransform.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author: Raod
 * @since: 2022-02-23
 */
public interface IGroovyHandler {

    List<JSONObject> transform(List<JSONObject> data);
}
