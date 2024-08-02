package com.ruoyi.colorfulfog.controller.bill;


import com.ruoyi.colorfulfog.model.dto.AddDataManualDto;
import com.ruoyi.colorfulfog.model.dto.BillResultDto;

import com.ruoyi.colorfulfog.model.dto.ExportTemplateDto;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ExportTemplateVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.BillMainService;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeDetailService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * 账单结果控制器
 */
@RestController
@RequestMapping("/colorful-fog/billResult")
public class BillResultController {


    @Autowired
    BillMainService billMainService;
    @Autowired
    SchemeDetailService schemeDetailService;

    /**
     * 查询账单数据
     */
    //@DataScopeAuth(ruleTable = "cwu_bill_main")
    @PostMapping("/list")
    public ResultVO<BillResultVO> list(@RequestBody BillResultDto billResultDto, @RequestParam Integer currentPage,
                                       @RequestParam Integer pageSize) {
        return ResultVOUtils.success(billMainService
                .list(billResultDto, currentPage, pageSize)) ;

    }

    /**
     * 账单数据设为无效
     */
    @PostMapping("/setInValid")
    public ResultVO<String> setInValid(@RequestBody List<String> idList) {
        String ans =  billMainService.setInValid(idList);
        return ResultVOUtils.success(ans);

    }
    /**
     * 手动上传账单数据
     */
    @PostMapping("/addDataManual")
    public ResultVO<String> addDataManual(@RequestBody List<AddDataManualDto> billDataList) {

        billMainService.addDataManual(billDataList);
        return ResultVOUtils.success();
    }
    /**
     * 导出手动导入数据的数据模板
     */
    @PostMapping("/exportTemplate")
    public ResultVO<List<ExportTemplateVO>> exportTemplate(@RequestBody ExportTemplateDto exportTemplateDto) {
        return ResultVOUtils.success(schemeDetailService.exportTemplate(exportTemplateDto.getSchemeCode()));

    }


}
