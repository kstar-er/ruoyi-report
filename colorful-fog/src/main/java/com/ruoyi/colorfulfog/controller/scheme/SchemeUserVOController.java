package com.ruoyi.colorfulfog.controller.scheme;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.SchemeUserRelation;
import com.ruoyi.colorfulfog.model.dto.*;
import com.ruoyi.colorfulfog.model.vo.*;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeUserRelationService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户和计划绑定的关系表
 * 哪些商户需要执行这个计划
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/colorful-fog/schemeUser")
public class SchemeUserVOController {

    @Autowired
    SchemeUserRelationService mainService;
    /**
     * 查询
     * @param entity
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody SchemeUserRelation entity, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<SchemeUserRelation> pageInfo = new PageInfo<>(mainService.list(new LambdaQueryWrapper<>(entity)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    /**
     * 新增
     * @param entity
     * @return
     */
    @PostMapping("/add")
    public ResultVO<String> add(@RequestBody SchemeUserRelation entity){
        mainService.save(entity);
        return ResultVOUtils.success();
    }

    /**
     * 批量新增
     * @param entityList
     * @return
     */
    @PostMapping("/addBatch")
    public ResultVO<String> addBatch(@RequestBody List<SchemeUserRelation> entityList){
        mainService.saveBatch(entityList);
        return ResultVOUtils.success();
    }


    /**
     * 批量新增或者更新，覆盖之前的数据
     * @param schemeUserRelationSaveDto
     * @return
     */
    @PostMapping("/addOrUpdateBatch")
    public ResultVO<String> addOrUpdateBatch(@RequestBody SchemeUserRelationSaveDto schemeUserRelationSaveDto){
        List<SchemeUserRelation> schemeUserRelationList = mainService.list(new LambdaQueryWrapper<SchemeUserRelation>()
                .eq(SchemeUserRelation::getSchemeCode, schemeUserRelationSaveDto.getSchemeCode()));
        List<Long> idList = schemeUserRelationList.stream().map(SchemeUserRelation::getId).collect(Collectors.toList());
        mainService.removeBatchByIds(idList);

        for (SchemeUserRelation schemeUserRelation : schemeUserRelationSaveDto.getEntityList()) {
            schemeUserRelation.setTableName(schemeUserRelationSaveDto.getTableName());
        }
        mainService.saveBatch(schemeUserRelationSaveDto.getEntityList());
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
    public ResultVO<String> update(@RequestBody List<SchemeUserRelation> entityList){
        mainService.updateBatchById(entityList);
        return ResultVOUtils.success();
    }

    /**
     *  查询用户数据，根据传入的表和字段
     * @param entity
     * @return
     */
    @PostMapping("/selectUserData")
    public ResultVO<List<UserDataVO>> selectUserData(@RequestBody SelectUserDataDto entity){
        return ResultVOUtils.success(mainService.selectUserData(entity));
    }

    /**
     * 批量绑定使用的用户
     * @param entity
     * @return
     */
    @PostMapping("bindUserBatch")
    public ResultVO<String> bindUserBatch(@RequestBody BindUserDto entity){
        return ResultVOUtils.success(mainService.bindUserBatch(entity));
    }

    /**
     * 直接获得当前所有方案种，绑定了哪些商家档案
     * @return
     */
     @PostMapping("geBelongTable")
    public ResultVO<List<BelongTableVO>> geBelongTable(){
        return ResultVOUtils.success(mainService.geBelongTable());
     }
    /**
     * 传入表名获得对应的所有用户和他们选择的方案
     * 以及分页
     */
    @PostMapping("getUserAndScheme")
    public ResultVO<List<UserAndSchemeVO>> getUserAndScheme(@RequestBody TableNameDto tableName, @RequestParam(defaultValue = "1") Integer pageNum,
                                                            @RequestParam(defaultValue = "20") Integer pageSize){
        return ResultVOUtils.success(mainService.getUserAndScheme(tableName,pageNum,pageSize));
    }
}
