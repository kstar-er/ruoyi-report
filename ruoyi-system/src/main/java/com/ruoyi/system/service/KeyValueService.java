package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.KeyValue;

public interface KeyValueService extends IService<KeyValue> {

    String get(String key);

    boolean put(String key, String value);

    KeyValue getKeyValueEntity(String key);

    KeyValue getKeyValueEntityForUpdate(String key);
}


