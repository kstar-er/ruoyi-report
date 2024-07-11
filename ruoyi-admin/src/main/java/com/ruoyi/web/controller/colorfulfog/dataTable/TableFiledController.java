package com.ruoyi.web.controller.colorfulfog.dataTable;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.TableFieldRelation;
import com.ruoyi.colorfulfog.model.dto.AddTableFieldDto;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.TableFieldRelationService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 单据结构明细表
 * 记录数据库和表之间的字段关系，根据表名更新数据库字段
 *
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tableFiled")
public class TableFiledController {
    @Resource
    TableFieldRelationService tableFieldRelationService;


    /**
     * 查询
     * @param tableFieldRelation
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody TableFieldRelation tableFieldRelation, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<TableFieldRelation> pageInfo = new PageInfo<>(tableFieldRelationService.list(new LambdaQueryWrapper<>(tableFieldRelation)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }
    /**
     * 根据表名导入
     * @param tableField
     */
    @PostMapping("/addTableField")
    public ResultVO<String> addTableField(@RequestBody AddTableFieldDto tableField){
        tableFieldRelationService.saveTableFile(tableField);
        return ResultVOUtils.success();
    }

    /**
     * 批量导入
     * @param tableNames
     */
    @PostMapping("/addTableFieldList")
    public ResultVO<String> addTableFieldList(@RequestBody AddTableFieldDto tableNames){
        tableFieldRelationService.saveTableFileList(tableNames);
        return  ResultVOUtils.success();
    }

    /**
     * 根据表名更新表关系字段
     * @param addTableFieldDto
     */
    @PostMapping("/updateTableField")
    public ResultVO<String> updateTableField(@RequestBody AddTableFieldDto addTableFieldDto){
        tableFieldRelationService.updateTableField(addTableFieldDto);
        return ResultVOUtils.success();
    }

}
