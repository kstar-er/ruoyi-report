package com.ruoyi.web.controller.colorfulfog.scheme;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.SchemeDetail;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeDetailService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 计划任务详情
 * 每个字段一条记录，记录详情的类型，计算公式
 */
@RestController
@RequestMapping("/schemeDetail")
public class SchemeDetailController {
    @Resource
    SchemeDetailService schemeDetailService;


    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody SchemeDetail schemeDetail, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<SchemeDetail> pageInfo = new PageInfo<>(schemeDetailService.list(new LambdaQueryWrapper<>(schemeDetail)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }
    //新增计划内容主表
    @PostMapping("/addSchemeDetailBatch")
    public ResultVO<String> addSchemeDetailBatch(@RequestBody List<SchemeDetail> schemeDetail){

        schemeDetailService.addSchemeDetailBatch(schemeDetail);

        return ResultVOUtils.success();
    }

    //删除计划内容主表
    @PostMapping("/deleteSchemeDetail")
    public ResultVO<String> deleteSchemeDetail(@RequestBody List<Integer> ids){
        schemeDetailService.deleteSchemeDetail(ids);
        return ResultVOUtils.success();
    }

    //修改计划内容主表
    @PostMapping("/updateSchemeDetailBatch")
    public ResultVO<String> updateSchemeDetailBatch(@RequestBody List<SchemeDetail> schemeDetail){

        schemeDetailService.updateSchemeDetailBatch(schemeDetail);
        return ResultVOUtils.success();
    }
}
