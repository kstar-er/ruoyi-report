package com.ruoyi.colorfulfog.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.ErrReason;
import com.ruoyi.colorfulfog.model.SchemeDetail;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.modules.datasource.controller.dto.DataSourceDto;
import com.ruoyi.colorfulfog.modules.datasource.dao.entity.DataSource;
import com.ruoyi.colorfulfog.service.table.interfaces.BillResultService;
import com.ruoyi.colorfulfog.service.table.interfaces.DataSourceService;
import com.ruoyi.colorfulfog.service.table.interfaces.ErrReasonService;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeDetailService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 测试接口
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/colorful-fog/test")
public class TestController {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private ErrReasonService errReasonService;
    @Autowired
    SchemeDetailService schemeDetailService;
    @PostMapping("/testSql")
    public ResultVO<List<Map<String,Object>>> testSql(){
        DataSource dataSource = dataSourceService.getOne(new LambdaQueryWrapper<DataSource>()
                .eq(DataSource::getId, 1));

        // 设置数据源信息
        DataSourceDto dataSourcedto = new DataSourceDto();
        BeanUtils.copyProperties(dataSource, dataSourcedto);
        dataSourcedto.setDynSentence("select * from auth_user where id = 1");
        List<Map<String,Object>> jsonObjects  = dataSourceService.execute(dataSourcedto);
        return ResultVOUtils.success(jsonObjects);
    }

    @PostMapping("/testErr")
    public ResultVO<String> testErr(@RequestBody  List<ErrReason> errReasons){
        errReasonService.saveErrReasonBatch(errReasons);
        return  ResultVOUtils.success();
    }
    @PostMapping("/testCal")
    public ResultVO<String> testCal(@RequestBody  String schemeCode){
        List<SchemeDetail> schemeDetailList = schemeDetailService.listSchemeDetailBySchemeCode(schemeCode, SelectTypeEnum.CALC);
        schemeDetailList = schemeDetailService.calculateOrder(schemeDetailList);
        schemeDetailService.updateBatchById(schemeDetailList);
        return  ResultVOUtils.success();
    }
    @Autowired
    BillResultService billResultService;
    @PostMapping("testMo")
    public ResultVO<String> testMo(){
        billResultService.testSave();
        return ResultVOUtils.success();
    }

}
