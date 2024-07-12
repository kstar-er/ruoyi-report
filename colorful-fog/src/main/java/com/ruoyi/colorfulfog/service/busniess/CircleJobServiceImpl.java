package com.ruoyi.colorfulfog.service.busniess;

import com.ruoyi.colorfulfog.constant.enums.ExecutionTimeUnit;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeMainService;
import com.ruoyi.colorfulfog.utils.TimeUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class CircleJobServiceImpl {

    /**
     * 定时计算当天仓储费用
     */
    @Autowired
    SchemeMainService schemeMainService;


    @XxlJob("checkDayScheme")
    public void checkDayScheme(){
        // 获取当前系统的小时数
        int hour = TimeUtils.getHour();
        List<String> schemeMains =   schemeMainService.getNeedSchemeMainList(ExecutionTimeUnit.DAILY,hour);
        if (schemeMains.isEmpty()){
            return;
        }
        schemeMainService.startCreateBill(schemeMains,0);
    }
    @XxlJob("checkMonScheme")
    public void checkMonScheme(){
        int day = TimeUtils.getDay();
        List<String> schemeMains =   schemeMainService.getNeedSchemeMainList(ExecutionTimeUnit.MONTHLY,day);
        if (schemeMains.isEmpty()){
            return;
        }
        schemeMainService.startCreateBill(schemeMains,0);
        //        schemeMainService.startCreateBill(Collections.singletonList(param),false);
    }
}
