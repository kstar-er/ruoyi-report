package com.ruoyi.colorfulfog.controller.collect;

import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.CollectResultMain;
import com.ruoyi.colorfulfog.model.dto.*;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import com.ruoyi.colorfulfog.service.table.CollectResultMainServiceImpl;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectResultMainService;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.DependDataService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 汇总数据主表表控制层
 *
 * @author kstar
 * @data Wed Feb 21 16:24:17 CST 2024
 */
@RestController
@RequestMapping("/colorful-fog/CollectResultMain")
public class CollectResultMainController {
    /**
     * 服务对象
     */
    @Autowired
    private CollectResultMainServiceImpl collectResultMainServiceImpl;

    @Autowired
    private CollectResultMainService collectResultMainService;
    /**
     * 格式化查询汇总账单的结果
     * @param billResultDto
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PostMapping("/list")
    public ResultVO<BillResultVO> list(@RequestBody CollectResultMainDto billResultDto, @RequestParam Integer currentPage,
                                       @RequestParam Integer pageSize) {
        return ResultVOUtils.success(collectResultMainService
                .list(billResultDto, currentPage, pageSize)) ;

    }

    /**
     * 批量修改
     *
     * @param entity
     * @return
     */
//    @PostMapping("/update")
    public ResultVO<String> update(@RequestBody CollectBillData entity) {
        collectResultMainServiceImpl.updateBillData(entity);
        return ResultVOUtils.success();
    }

    /**
     * 导出excel接口
     */
    @PostMapping("/exportExcel")
    public ResultVO<List<ExportExcelVO>> exportExcel(@RequestBody ExportExcelDto exportExcelDto) {
        return ResultVOUtils.success( collectResultMainService.exportExcel(exportExcelDto, SelectTypeEnum.EXPORT));
    }
    @PostMapping("/exportExcelBatch")
    public ResultVO<List<ExportExcelVO>> exportExcelBatch(@RequestBody List<String>  idList) {
        return ResultVOUtils.success( collectResultMainService.exportExcelBatch(idList, SelectTypeEnum.EXPORT));
    }
    /**
     * 手动更新数据接口
     */
    @PostMapping("/manualUpdate")
    public ResultVO<String> manualUpdate(@RequestBody List<ManualUpdateDto> manualUpdateDto) {

        return ResultVOUtils.success(collectResultMainService.manualUpdate(manualUpdateDto));
    }

    @PostMapping("audit")
    public ResultVO<String> audit(@RequestBody AuditDto auditDto) {
        return ResultVOUtils.success(collectResultMainService.audit(auditDto));
    }

    @PostMapping("getCostTerm")
    public ResultVO<List<CollectBillData>> getCostTerm(@RequestBody GetCostTermDto getCostTermDto) {
        return ResultVOUtils.success(collectResultMainService.getCostTerm(getCostTermDto));
    }
    @PostMapping("delete")
    public ResultVO<String> delete(@RequestBody List<String> billCode) {
        collectResultMainService.deleteByIds(billCode);
        return ResultVOUtils.success();
    }
}
