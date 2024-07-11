package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.UpdateBillLogMapper;
import com.ruoyi.colorfulfog.model.UpdateBillLog;
import com.ruoyi.colorfulfog.model.dto.SelectOneFieldLogDto;
import com.ruoyi.colorfulfog.service.table.interfaces.UpdateBillLogService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UpdateBillLogServiceImpl extends ServiceImpl<UpdateBillLogMapper, UpdateBillLog> implements UpdateBillLogService{

    @Override
    public  List<UpdateBillLog> selectOneFieldLog(SelectOneFieldLogDto entity){
        return  list(new LambdaQueryWrapper<UpdateBillLog>()
                .eq(UpdateBillLog::getBillCode,entity.getBillCode())
                .eq(UpdateBillLog::getResultName,entity.getResultName()));
    }
}
