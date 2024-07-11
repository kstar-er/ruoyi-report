package com.ruoyi.web.controller.colorfulfog.dataTable;

import com.esotericsoftware.minlog.Log;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.OrderTableRelation;
import com.ruoyi.colorfulfog.model.vo.OrderTableRelationVO;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.ForeignKeyService;
import com.ruoyi.colorfulfog.service.table.interfaces.OrderTableRelationService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单据结构主表
 * 用户订单和数据库表单之间的关系控制
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderTable")
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

//    @DataScopeAuth(ruleTable = "cwu_order_table_relation")
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
        orderTableRelationService.save(orderTableRelation);
        return ResultVOUtils.success();
    }

    /**
     * 批量新增
     * @param orderTableRelations
     * @return
     */
    @PostMapping("/addBatch")
    public ResultVO<String> addBatch(@RequestBody List<OrderTableRelation> orderTableRelations){
        orderTableRelationService.saveBatch(orderTableRelations);
        return ResultVOUtils.success();
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    public ResultVO<String> delete(@RequestBody List<Integer> ids){
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
}
