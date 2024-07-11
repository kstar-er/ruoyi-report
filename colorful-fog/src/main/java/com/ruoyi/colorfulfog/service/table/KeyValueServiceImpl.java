package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.KeyValueMapper;
import com.ruoyi.colorfulfog.model.table.KeyValue;
import com.ruoyi.colorfulfog.service.table.interfaces.KeyValueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeyValueServiceImpl extends ServiceImpl<KeyValueMapper, KeyValue> implements KeyValueService {
    @Override
    public String get(String key) {
        if (key == null) {
            return null;
        }
        LambdaQueryWrapper<KeyValue> eq = new QueryWrapper<KeyValue>().lambda().eq(KeyValue::getKey, key);
        KeyValue one = this.getOne(eq);

        return one.getValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean put(String key, String value) {
        if (key == null || value == null) {
            return false;
        }

        KeyValue keyValue = getKeyValueEntity(key);

        if (keyValue != null) {
            keyValue.setValue(value);
            updateById(keyValue);
        } else {
            keyValue = new KeyValue().setKey(key).setValue(value);
            save(keyValue);
        }
        return true;
    }

    @Override
    public KeyValue getKeyValueEntity(String key) {
        return getOne(new LambdaQueryWrapper<KeyValue>().eq(KeyValue::getKey, key));
    }

    @Override
    public KeyValue getKeyValueEntityForUpdate(String key) {
        return getOne(new LambdaQueryWrapper<KeyValue>().eq(KeyValue::getKey, key).last("for update"));
    }
}


