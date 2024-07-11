package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.DependTypeEnum;
import com.ruoyi.colorfulfog.mapper.ErrReasonMapper;
import com.ruoyi.colorfulfog.model.DependData;
import com.ruoyi.colorfulfog.model.DependMain;
import com.ruoyi.colorfulfog.model.ErrReason;
import com.ruoyi.colorfulfog.service.table.interfaces.DependDataService;
import com.ruoyi.colorfulfog.service.table.interfaces.DependMainService;
import com.ruoyi.colorfulfog.service.table.interfaces.ErrReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ErrReasonServiceImpl extends ServiceImpl<ErrReasonMapper, ErrReason> implements ErrReasonService{

    @Autowired
    DependDataService dependDataService;

    @Autowired
    DependMainService dependMainService;

    @Transactional
    @Override
    public boolean dealErrReason(List<ErrReason> errReason){
        // 处理errReason数据
        List<ErrReason> haveDealErr = new ArrayList<>();
        // 将errReason数据转换为DependData数据
        List<DependData> dependDataList = new ArrayList<>();
        Map<String, DependMain> dependMainMap = dependMainService.getDependMainMap(errReason.stream().map(ErrReason::getDependCode)
                .collect(Collectors.toList()));
        for (ErrReason reason : errReason) {
            if (dependMainMap.get(reason.getDependCode())==null){
                throw new GlobalException(reason.getDependCode()+"依赖主表已经不存在","");
            }
            // 只有分类依赖的情况可以导入数据
            if (DependTypeEnum.SORT.equals(dependMainMap.get(reason.getDependCode()).getDependType())){
                DependData dependData = DependData.builder()
                        .dependCode(reason.getDependCode())
                        .key(reason.getKey())
                        .value(reason.getValue())
                        .build();
                haveDealErr.add(reason);
                dependDataList.add(dependData);
            }
        }
        updateErrReason(haveDealErr);
        // 处理依赖表的数据
        dependDataService.saveBatch(dependDataList);
        if (haveDealErr.size()==errReason.size()){
            return true;
        }{
            return false;
        }
    }

    /**
     * 根据dependCode和Key去重保存新的errReason
     * @param errReason
     */
    @Transactional
    @Override
    public void saveErrReasonBatch(List<ErrReason> errReason){
        List<ErrReason> errReasons = list(new LambdaQueryWrapper<ErrReason>()
                .eq(ErrReason::getDealFlag,0)
                .in(ErrReason::getDependCode, errReason.stream().map(ErrReason::getDependCode).collect(Collectors.toList()))
                .in(ErrReason::getKey, errReason.stream().map(ErrReason::getKey).collect(Collectors.toList()))
        );
        // 去重errReason
        errReason = errReason.stream().distinct()
                .collect(Collectors.toList());
        Map<String, ErrReason> errReasonMap = errReasons.stream().collect(Collectors.toMap(
                arr -> String.format("%s[-]%s", arr.getDependCode(), arr.getKey()), errReason1 -> errReason1
        ));
        errReason.removeIf(reason -> errReasonMap.get(String.format("%s[-]%s", reason.getDependCode(), reason.getKey())) != null);
        saveBatch(errReason);
    }

    private void updateErrReason(List<ErrReason> errReason){
        for (ErrReason reason : errReason) {
            reason.setDealFlag(1);
        }
        this.updateBatchById(errReason);
    }
}
