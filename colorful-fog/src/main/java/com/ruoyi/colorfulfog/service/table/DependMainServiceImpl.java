package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.colorfulfog.constant.SysConstant;
import com.ruoyi.colorfulfog.model.dto.DependMainDto;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.DependMainMapper;
import com.ruoyi.colorfulfog.model.DependMain;
import com.ruoyi.colorfulfog.service.table.interfaces.DependMainService;

@Service
public class DependMainServiceImpl extends ServiceImpl<DependMainMapper, DependMain> implements DependMainService {

    @Override
    public Map<String, DependMain> getDependMainMap(List<String> codeList) {
        if (CollectionUtils.isEmpty(codeList)){
            return null;
        }
        return list(new LambdaQueryWrapper<DependMain>().in(
                DependMain::getDependCode, codeList
        )).stream().collect(Collectors.toMap(DependMain::getDependCode, e -> e));
    }

    @Override
    public PageInfo<DependMain> list(DependMainDto dependMainDto){
        long createTimeStart = 0,createTimeEnd = 0,updateTimeStart = 0,updateTimeEnd = 0;
        if(dependMainDto.getCreateTime()!= null){
            createTimeStart = dependMainDto.getCreateTime();
            createTimeEnd = createTimeStart + SysConstant.Time.DAY_TIME_STAMP;
            dependMainDto.setCreateTime(null);
        }
        if(dependMainDto.getUpdateTime()!= null){
            updateTimeStart = dependMainDto.getUpdateTime();
            updateTimeEnd = updateTimeStart + SysConstant.Time.DAY_TIME_STAMP;
            dependMainDto.setUpdateTime(null);
        }
        LambdaQueryWrapper<DependMain> queryWrapper = new LambdaQueryWrapper<>(dependMainDto);
        if(createTimeStart!= 0){
            queryWrapper
                    .ge(DependMain::getCreateTime,createTimeStart)
                    .le(DependMain::getCreateTime,createTimeEnd);
        }
        if(updateTimeStart!= 0){
            queryWrapper
                    .ge(DependMain::getUpdateTime,updateTimeStart)
                    .le(DependMain::getUpdateTime,updateTimeEnd);
        }

        if (dependMainDto.getCreateStartTime() != null && dependMainDto.getCreateEndTime() != null) {
            queryWrapper.ge(DependMain::getCreateTime, dependMainDto.getCreateStartTime())
                    .le(DependMain::getCreateTime, dependMainDto.getCreateEndTime() + SysConstant.Time.DAY_TIME_STAMP);
        }
        if (dependMainDto.getUpdateStartTime() != null && dependMainDto.getUpdateEndTime() != null) {
            queryWrapper.ge(DependMain::getUpdateTime, dependMainDto.getUpdateStartTime())
                    .le(DependMain::getUpdateTime, dependMainDto.getUpdateEndTime() + SysConstant.Time.DAY_TIME_STAMP);
        }

        queryWrapper.orderByDesc(DependMain::getId);
        List<DependMain> dependMainList = list(queryWrapper);
        return new PageInfo<>(dependMainList);
    }

}

