package com.ruoyi.colorfulfog.controller.dataTable;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esotericsoftware.minlog.Log;
import com.ruoyi.colorfulfog.model.OrderTableRelation;
import com.ruoyi.colorfulfog.model.vo.OrderTableRelationVO;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.ForeignKeyService;
import com.ruoyi.colorfulfog.service.table.interfaces.OrderTableRelationService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 单据结构主表
 * 用户订单和数据库表单之间的关系控制
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/colorful-fog/orderTable")
public class OrderTableController {

    @Autowired
    OrderTableRelationService orderTableRelationService;
    @Autowired
    ForeignKeyService foreignKeyService;
    /**
     * 查询
     * @param orderTableRelation
     * @param currentPage
     * @param pageSize
     * @return
     */

    //@DataScopeAuth(ruleTable = "cwu_order_table_relation")
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody OrderTableRelation orderTableRelation, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        Log.info("查询");
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<OrderTableRelationVO> pageInfo = orderTableRelationService.list(orderTableRelation);
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    /**
     * 新增
     * @param orderTableRelation
     * @return
     */
    @PostMapping("/add")
    public ResultVO<String> add(@RequestBody OrderTableRelation orderTableRelation){
        orderTableRelationService.saveAndAddField(Collections.singletonList(orderTableRelation));
        return ResultVOUtils.success();
    }

    /**
     * 批量新增
     * @param orderTableRelations
     * @return
     */
    @PostMapping("/addBatch")
    public ResultVO<String> addBatch(@RequestBody List<OrderTableRelation> orderTableRelations){
        orderTableRelationService.saveAndAddField(orderTableRelations);
        return ResultVOUtils.success();
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    public ResultVO<String> delete(@RequestBody List<Integer> ids){
        List<OrderTableRelation> orderTableRelations = orderTableRelationService.listByIds(ids);
        foreignKeyService.deleteByOrderTableId(orderTableRelations);
        orderTableRelationService.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    /**
     * 批量修改
     * @param orderTableRelations
     * @return
     */
    @PostMapping("/update")
    public ResultVO<String> update(@RequestBody List<OrderTableRelation> orderTableRelations){
        orderTableRelationService.updateBatchById(orderTableRelations);
        return ResultVOUtils.success();
    }
    /**
     * 传入数据库id，可以选择该数据库id中存在的表， 返回该数据库中所有表
     */
    @PostMapping("/getTableNameByDataSourceId")
    public ResultVO<List<OrderTableRelation>> getTableNameByDataSourceId(@RequestBody Integer dataSourceIds){
        return ResultVOUtils.success(orderTableRelationService.getTableNameByDataSourceId(dataSourceIds));
    }
}
