package com.ruoyi.colorfulfog.controller;

import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.modules.datasource.controller.param.ConnectionParam;
import com.ruoyi.colorfulfog.modules.datasource.dao.entity.DataSource;
import com.ruoyi.colorfulfog.service.table.interfaces.DataSourceService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 注册数据库
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/colorful-fog/dataSource")
public class DataSourceController {
    @Autowired
    DataSourceService dataSourceService;

    @DataScopeAuth(ruleTable = "cwu_data_source")
    @PostMapping("/list")
    public ResultVO<List<DataSource>> queryDataSourceList() {
        return ResultVOUtils.success(dataSourceService.listAllDataSource());
    }
    @PostMapping("/testConnection")
    public ResultVO<Boolean> testConnection(@RequestBody ConnectionParam connectionParam) {
        return  ResultVOUtils.success(dataSourceService.testConnection(connectionParam));
    }

    //新增依赖数据表
    @PostMapping("/add")
    public ResultVO<String> addDependData(@RequestBody DataSource dataSource){
        dataSourceService.save(dataSource);
        return ResultVOUtils.success();
    }

    //删除依赖数据表
    @PostMapping("/delete")
    public ResultVO<String> deleteDependData(@RequestBody List<Integer> ids){
        dataSourceService.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    //修改依赖数据表
    @PostMapping("/update")
    public ResultVO<String> updateDependData(@RequestBody DataSource dataSource){
        dataSourceService.updateById(dataSource);
        return ResultVOUtils.success();
    }
}
