package com.ruoyi.colorfulfog.controller.depend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.colorfulfog.model.Condition;
import com.ruoyi.colorfulfog.model.SchemeUserRelation;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.ConditionService;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeUserRelationService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账单生效条件判断
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/colorful-fog/condition")
public class ConditionController {

    @Autowired
    ConditionService mainService;
    /**
     * 查询
     * @param entity
     * @param currentPage
     * @param pageSize
     * @return
     */
    @DataScopeAuth(ruleTable = "cwu_condition")
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody Condition entity, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<Condition> pageInfo = new PageInfo<>(mainService.list(new LambdaQueryWrapper<>(entity)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    /**
     * 新增
     * @param entity
     * @return
     */
    @PostMapping("/add")
    public ResultVO<Condition> add(@RequestBody Condition entity){
        mainService.save(entity);
        return ResultVOUtils.success(entity);
    }

    /**
     * 批量新增
     * @param entityList
     * @return
     */
    @PostMapping("/addBatch")
    public ResultVO<String> addBatch(@RequestBody List<Condition> entityList){
        mainService.saveBatch(entityList);
        return ResultVOUtils.success();
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    public ResultVO<String> delete(@RequestBody List<Integer> ids){
        mainService.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    /**
     * 批量修改
     * @param entityList
     * @return
     */
    @PostMapping("/update")
    public ResultVO<String> update(@RequestBody List<Condition> entityList){
        mainService.updateBatchById(entityList);
        return ResultVOUtils.success();
    }
}
