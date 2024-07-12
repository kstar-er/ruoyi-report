package com.ruoyi.colorfulfog.controller.scheme;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;
import com.ruoyi.colorfulfog.model.SchemeDetail;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.dto.*;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.model.vo.SchemeMainDetailVO;
import com.ruoyi.colorfulfog.model.vo.TestBillResultVO;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeMainService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.Cacheable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 计划任务主表
 *
 */
@RestController
@RequestMapping("/colorful-fog/schemeMain")
public class SchemeMainController {
    @Resource
    SchemeMainService schemeMainService;

    /**
     * 测试创建账单记录
     * @param codeList
     * @return
     */
    @PostMapping("/create")
    public ResultVO<Void> create(@RequestBody CodeDto codeList) {
        schemeMainService.startCreateBill(Collections.singletonList(codeList.getCode()),0);
        return ResultVOUtils.success();
    }

    @PostMapping("/testScheme")
    public ResultVO<TestBillResultVO> testScheme(@RequestBody CodeDto code) {
        return ResultVOUtils.success(schemeMainService.testScheme(code.getCode()));
    }

    /**
     * 根据id查询出这个计划的所有详细信息
     * @param id
     * @return
     */

    @PostMapping("selectOneById")
    public ResultVO<SchemeMainDetailVO> selectOneById(@RequestParam Long id){
        return ResultVOUtils.success(schemeMainService.selectOneById(id));
    }

    /**
     * 按菜单的形式查询所有的计划
     * @return
     */
    @DataScopeAuth(ruleTable = "cwu_scheme_main")
    @PostMapping("selectMenu")
    public ResultVO<Map<String,List<SchemeMain>>> selectMenu(){
        return ResultVOUtils.success(schemeMainService.selectMenu());
    }

    @DataScopeAuth(ruleTable = "cwu_scheme_main")
    @PostMapping("/list")
    public ResultVO<PagedListVO> list(@RequestBody SchemeMainDto schemeMainDto, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        return ResultVOUtils.success(new PagedListVO<>(schemeMainService.list(schemeMainDto)));
    }




    //新增计划内容主表
    @PostMapping("/addSchemeMain")
    public ResultVO<SchemeMain> addSchemeMain(@RequestBody SchemeMain schemeMain){
        return ResultVOUtils.success(schemeMainService.addSchemeMain(schemeMain));
    }

    //删除计划内容主表
    @PostMapping("/deleteSchemeMain")
    public ResultVO<String> deleteSchemeMain(@RequestBody List<Integer> ids){
        schemeMainService.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    //修改计划内容主表
    @PostMapping("/updateSchemeMain")
    public ResultVO<String> updateSchemeMain(@RequestBody SchemeMain schemeMain){
        schemeMainService.updateById(schemeMain);
        return ResultVOUtils.success();
    }
    @PostMapping("/updateSchemeMainBatch")
    public ResultVO<String> updateSchemeMainBatch(@RequestBody List<SchemeMain> schemeMain){
        schemeMainService.updateBatchById(schemeMain);
        return ResultVOUtils.success();
    }
    /**
     * 刷新账单接口，对计算失败的数据进行覆盖，暂定传入方案和批次号修改
     */
    @PostMapping("flash")
    public ResultVO<String> flash(@RequestBody BillResultFlashDto billResultDto) {
        schemeMainService.flash(billResultDto);
        return ResultVOUtils.success();
    }
    @PostMapping("flashByCollectCode")
    public ResultVO<String> flashByCollectCode(@RequestBody BillResultFlashDto billResultDto) {
        schemeMainService.flashByCollectCode(billResultDto);
        return ResultVOUtils.success();
    }
    @PostMapping("getGroupType")
    public ResultVO<List<String>> getGroupTyp(){
        return  ResultVOUtils.success(schemeMainService.list(new LambdaQueryWrapper<SchemeMain>()
                .select(SchemeMain::getSort)).stream().map(SchemeMain::getSort).distinct()
                .collect(Collectors.toList()));
    }

    @PostMapping("getBelongTable")
    public ResultVO<List<SchemeMain>> getBelongTable(){
        return  ResultVOUtils.success();
    }

    /**
     * 复制字段，传入源字段和目标字段，会将基础的字段直接复制到新
     */


}
