package com.ruoyi.web.controller.colorfulfog.collect;

import com.ruoyi.colorfulfog.model.CollectResultMain;
import com.ruoyi.colorfulfog.model.dto.CodeDto;
import com.ruoyi.colorfulfog.model.dto.CollectResultMainDto;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.CollectResultMainServiceImpl;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectResultMainService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 汇总数据主表表控制层
 *
 * @author kstar
 * @data Wed Feb 21 16:24:17 CST 2024
 */
@RestController
@RequestMapping("/CollectResultMain")
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
    @PostMapping("/update")
    public ResultVO<String> update(@RequestBody List<CollectResultMain> entity) {
        collectResultMainServiceImpl.updateBatchById(entity);
        return ResultVOUtils.success();
    }

    /**
     * 导出excel接口
     */
    @PostMapping("/exportExcel")
    public ResultVO<List<ExportExcelVO>> exportExcel(@RequestBody CodeDto codeDto) {
        return ResultVOUtils.success( collectResultMainService.exportExcel(codeDto.getCode()));
    }
    /**
     * 手动更新数据接口
     */
    @PostMapping("/manualUpdate")
    public ResultVO<String> manualUpdate(@RequestBody List<ManualUpdateDto> manualUpdateDto) {

        return ResultVOUtils.success(collectResultMainService.manualUpdate(manualUpdateDto));
    }
}
