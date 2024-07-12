package com.ruoyi.colorfulfog.controller.depend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.colorfulfog.model.DependRule;
import com.ruoyi.colorfulfog.model.DependRule;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.DependRuleService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

// todo 依赖表相关的增删改查，计划表的增删改查，表单字段关联表的查找，计划用户使用表的查询，订单以及数据库表关联表的增删改查

/**
 * 依赖表规则表
 * 记录依赖表的规则，是取哪个表的哪个字段的数值，然后传入作为依赖表的key
 */
@RestController
@RequestMapping("/colorful-fog/dependRule")
public class DependRuleController {
    @Resource
    DependRuleService dependRuleService;

    //查询依赖规则表
    //@DataScopeAuth(ruleTable = "cwu_depend_rule")
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody DependRule dependRule, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<DependRule> pageInfo = new PageInfo<>(dependRuleService.list(new LambdaQueryWrapper<>(dependRule)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    //新增依赖规则表
    @PostMapping("/addDependRule")
    public ResultVO<String> addDependRule(@RequestBody List<DependRule> dependRule){
        dependRuleService.saveBatch(dependRule);
        return ResultVOUtils.success();
    }

    //删除依赖规则表
    @PostMapping("/deleteDependRule")
    public ResultVO<String> deleteDependRule(@RequestBody List<Integer> ids){
        dependRuleService.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    //修改依赖规则表
    @PostMapping("/updateDependRule")
    public ResultVO<String> updateDependRule(@RequestBody List<DependRule> dependRule){
        dependRuleService.updateBatchById(dependRule);
        return ResultVOUtils.success();
    }
}
