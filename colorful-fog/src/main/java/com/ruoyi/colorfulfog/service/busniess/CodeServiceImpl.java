package com.ruoyi.colorfulfog.service.busniess;

import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;
import com.ruoyi.colorfulfog.model.table.KeyValue;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.KeyValueService;
import com.ruoyi.colorfulfog.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CodeServiceImpl implements CodeService {
    @Autowired
    KeyValueService keyValueService;


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public String getCode(IdTypeEnum idTypeEnum) {
        String todayDate = new SimpleDateFormat("yyMMdd").format(new Date());
        String prefix = idTypeEnum.getPrefix().toString();
        String num = getNum(idTypeEnum.getKey(), 1);

        return formatCode(prefix, todayDate, num);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public List<String> getCode(IdTypeEnum idTypeEnum, int createNum) {
        String todayDate = new SimpleDateFormat("yyMMdd").format(new Date());
        String prefix = idTypeEnum.getPrefix().toString();
        String num = getNum(idTypeEnum.getKey(), createNum);

        List<String> result = new ArrayList<>();
        for (int i = Integer.parseInt(num); i < Integer.parseInt(num) + createNum; i++) {
            result.add(formatCode(prefix, todayDate, i + ""));
        }

        return result;
    }

    //num的一致性由mysql的锁来保证，因此不需要在方法粒度加synchronized。加了反而会容易死锁。
    //步长为1，即每次num只增加1。
    //返回第一个可用的num
    private String getNum(String key, int step) {
        KeyValue keyValueEntity = keyValueService.getKeyValueEntityForUpdate(key);
        if (keyValueEntity == null) {
            try {
                keyValueService.save(KeyValue.builder().key(key).value("0").build());
            } catch (DuplicateKeyException e) {
                //如果key索引重复，说明别的线程已经添加过。索引重复直接忽略即可
            }
            keyValueEntity = keyValueService.getKeyValueEntityForUpdate(key);
        }

        //今天零点零分零秒零毫秒的毫秒数
        long zero = TimeUtils.initDateByDay();

        String num = null;
        //如果是昨天更新过的,则直接将value的值设置为1
        if (zero > keyValueEntity.getUpdateTime()) {
            num = "0";
            keyValueEntity.setValue("" + step);
            keyValueService.updateById(keyValueEntity);
        } else {//如果是今天刚更新的
            num = keyValueEntity.getValue();
            //将num的值+1，给下一次生成code时用
            keyValueEntity.setValue("" + (Integer.parseInt(num)+ step));
            keyValueService.updateById(keyValueEntity);
        }

        return num;
    }

    //将编码格式化成13位。
    private String formatCode(String prefix,
                              String todayDate,
                              String num) {
        int len = prefix.length() + todayDate.length() + num.length();
        if (len >= 13) {
            return prefix + todayDate + num;
        }

        //在数字前面补“0”
        for (int i = len; i < 13; i++) {
            num = "0" + num;
        }

        return prefix + todayDate + num;
    }
}
