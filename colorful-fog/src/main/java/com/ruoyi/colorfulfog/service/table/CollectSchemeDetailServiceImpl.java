package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.CollectSchemeDetailMapper;
import com.ruoyi.colorfulfog.model.CollectSchemeDetail;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectSchemeDetailService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CollectSchemeDetailServiceImpl extends ServiceImpl<CollectSchemeDetailMapper, CollectSchemeDetail> implements CollectSchemeDetailService{

    @Override
    public  List<CollectSchemeDetail> listByCode(String code){
        return list(new LambdaQueryWrapper<CollectSchemeDetail>().
                eq(CollectSchemeDetail::getCollectSchemeCode,code));
    }
}
