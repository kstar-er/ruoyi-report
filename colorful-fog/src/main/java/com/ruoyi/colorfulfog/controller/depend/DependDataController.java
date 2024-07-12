package com.ruoyi.colorfulfog.controller.depend;


import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.model.DependData;
import com.ruoyi.colorfulfog.model.DependMain;
import com.ruoyi.colorfulfog.model.dto.DependDataDto;
import com.ruoyi.colorfulfog.model.dto.UpdateDependDataDto;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.DependDataService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 依赖表数据
 * 记录依赖表的key-value,根据传入的key在这个表中替换对应的value
 */
@RestController
@RequestMapping("/colorful-fog/dependData")
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
    public ResultVO<PagedListVO> select(@RequestBody DependDataDto dependData, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageInfo<DependData> pageInfo = new PageInfo<>(dependDataService.select(dependData,currentPage,pageSize));
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

        dependDataService.addBatch(dependData);
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

    @PostMapping("/updateDependDataKey")
    public ResultVO<String> dependDataUpdate(@RequestBody UpdateDependDataDto updateDependDataDto){
        List<DependData> dependDataList = dependDataService.list(
                new LambdaQueryWrapper<DependData>()
                        .like(DependData::getKey,updateDependDataDto.getOldKey())
        );
        // 将dependData中的Key中的MERCHANT_WAREHOUSE_ORDER替换为MERCHANT_INTO_WAREHOUSE
        for (DependData dependData : dependDataList) {
            dependData.setKey(dependData.getKey().replace(updateDependDataDto.getOldKey(),updateDependDataDto.getNewKey()));
        }
        dependDataService.updateBatchById(dependDataList);
        return ResultVOUtils.success();
    }
}
