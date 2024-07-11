package com.ruoyi.colorfulfog.service.busniess;

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
//    @Scheduled(cron = "0 0 0 * * ?") //每隔一天
//    @Scheduled(cron = "0/10 * * * * ?") //测试用,5秒
    public void circleJob() {
        log.info("生成一次库存账单");
        List<String> codeList= new ArrayList<>();
        codeList.add("SC002");
        schemeMainService.startCreateBill(codeList,false);
    }

    @XxlJob("checkDayScheme")
    public void checkDayScheme(){
        // 获取当前系统的小时数
        int hour = TimeUtils.getHour();
        List<String> schemeMains =   schemeMainService.getNeedSchemeMainList(SchemeMain.ExecutionUnit.DAILY,hour);
        if (schemeMains.isEmpty()){
            return;
        }
        schemeMainService.startCreateBill(schemeMains,false);
    }
    @XxlJob("checkMonScheme")
    public void checkMonScheme(){
        int day = TimeUtils.getDay();
        List<String> schemeMains =   schemeMainService.getNeedSchemeMainList(SchemeMain.ExecutionUnit.MONTHLY,day);
        if (schemeMains.isEmpty()){
            return;
        }
        schemeMainService.startCreateBill(schemeMains,false);
        //        schemeMainService.startCreateBill(Collections.singletonList(param),false);
    }
}
