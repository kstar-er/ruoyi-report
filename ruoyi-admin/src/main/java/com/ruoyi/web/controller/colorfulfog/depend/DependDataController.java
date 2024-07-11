package com.ruoyi.web.controller.colorfulfog.depend;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.DependData;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.DependDataService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 依赖表数据
 * 记录依赖表的key-value,根据传入的key在这个表中替换对应的value
 */
@RestController
@RequestMapping("/dependData")
public class DependDataController {
    @Resource
    DependDataService dependDataService;

    /**
     * 查询
     * @param dependData
     * @param currentPage
     * @param pageSize
     * @return
     */
//    @DataScopeAuth(ruleTable = "cwu_depend_data")
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody DependData dependData, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<DependData> pageInfo = new PageInfo<>(dependDataService.list(new LambdaQueryWrapper<>(dependData)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    /**
     * 新增
     * @param dependData
     * @return
     */
    @PostMapping("/addDependData")
    public ResultVO<String> addDependData(@RequestBody DependData dependData){
        dependDataService.save(dependData);
        return ResultVOUtils.success();
    }

    /**
     * 批量新增
     * @param dependData
     * @return
     */
    @PostMapping("/addDependDataBatch")
    public ResultVO<String> addDependDataBatch(@RequestBody List<DependData> dependData){
        dependDataService.saveBatch(dependData);
        return ResultVOUtils.success();
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @PostMapping("/deleteDependData")
    public ResultVO<String> deleteDependData(@RequestBody List<Integer> ids){
        dependDataService.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    /**
     * 批量修改
     * @param dependData
     * @return
     */
    @PostMapping("/updateDependData")
    public ResultVO<String> updateDependData(@RequestBody List<DependData> dependData){
        dependDataService.updateBatchById(dependData);
        return ResultVOUtils.success();
    }
}
