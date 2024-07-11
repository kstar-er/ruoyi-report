package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.table.KeyValue;

public interface KeyValueService extends IService<KeyValue> {

    String get(String key);

    boolean put(String key, String value);

    KeyValue getKeyValueEntity(String key);

    KeyValue getKeyValueEntityForUpdate(String key);

}


