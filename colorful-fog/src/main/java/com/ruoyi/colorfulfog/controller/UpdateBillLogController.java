package com.ruoyi.colorfulfog.controller;
import com.ruoyi.colorfulfog.model.UpdateBillLog;
import com.ruoyi.colorfulfog.model.dto.SelectOneFieldLogDto;
import com.ruoyi.colorfulfog.model.mongodb.UpdateRecord;
import com.ruoyi.colorfulfog.service.table.UpdateBillLogServiceImpl;
import com.ruoyi.colorfulfog.service.table.interfaces.UpdateBillLogService;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;

import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
* 手动更新账单的日志表控制层
*
* @data Wed Mar 27 17:22:21 CST 2024
* @author kstar
*/
@RestController
@RequestMapping("/colorful-fog/UpdateBillLog")
public class UpdateBillLogController {
    @Autowired
    private UpdateBillLogServiceImpl updateBillLogServiceImpl;

    @Resource
    UpdateBillLogService updateBillLogService;

    /**
    * 查询
    * @param entity
    * @param currentPage
    * @param pageSize
    * @return
    */
    //@DataScopeAuth(ruleTable = "cwu_update_bill_log")
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody UpdateBillLog  entity, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<UpdateBillLog > pageInfo = new PageInfo<>(updateBillLogServiceImpl.list(new LambdaQueryWrapper<>(entity)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }

    /**
     * git
     */
    @PostMapping("selectOneFieldLog")
    public ResultVO<Map<String, List<UpdateRecord>>> selectOneFieldLog(@RequestBody SelectOneFieldLogDto entity){
        return ResultVOUtils.success(updateBillLogService.selectOneFieldLog(entity));
    }
}
