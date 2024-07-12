package com.ruoyi.colorfulfog.controller.collect;

import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;
import com.ruoyi.colorfulfog.model.CollectSchemeMain;
import com.ruoyi.colorfulfog.model.dto.CodeDto;
import com.ruoyi.colorfulfog.model.dto.CreateAndCollectDto;
import com.ruoyi.colorfulfog.model.dto.CreateBatchUser;
import com.ruoyi.colorfulfog.model.dto.CreateOneUser;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.CollectSchemeMainServiceImpl;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectSchemeMainService;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;

import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 汇总计划主表表控制层
 *
 * @author kstar
 * @data Wed Feb 21 16:24:17 CST 2024
 */
@RestController
@RequestMapping("/colorful-fog/collectSchemeMain")
public class CollectSchemeMainController {
    /**
     * 服务对象
     */
    @Autowired
    private CollectSchemeMainServiceImpl collectSchemeMainServiceImpl;

    @Autowired
    CollectSchemeMainService collectSchemeMainService;

    @Autowired
    CodeService codeService;


    /**
     * 查询
     *
     * @param entity
     * @param currentPage
     * @param pageSize
     * @return
     */
    @DataScopeAuth(ruleTable = "cwu_collect_scheme_main")
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody CollectSchemeMain entity, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<CollectSchemeMain> pageInfo = new PageInfo<>(collectSchemeMainServiceImpl.list(new LambdaQueryWrapper<>(entity)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("/add")
    public ResultVO<String> add(@RequestBody CollectSchemeMain entity) {
        String code = codeService.getCode(IdTypeEnum.COLLECT_SCHEME_CODE);
        entity.setCollectSchemeCode(code);
        collectSchemeMainServiceImpl.save(entity);
        return ResultVOUtils.success();
    }

    /**
     * 批量新增
     *
     * @param entity
     * @return
     */
    @PostMapping("/addBatch")
    public ResultVO<String> addBatch(@RequestBody List<CollectSchemeMain> entity) {
        List<String> codeList = codeService.getCode(IdTypeEnum.COLLECT_SCHEME_CODE,entity.size());
        int i =0;
        for (CollectSchemeMain collectSchemeMain : entity) {
            collectSchemeMain.setCollectSchemeCode(codeList.get(i));
        }
        collectSchemeMainServiceImpl.saveBatch(entity);
        return ResultVOUtils.success();
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    public ResultVO<String> deleteDependData(@RequestBody List<Integer> ids) {
        collectSchemeMainServiceImpl.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    /**
     * 批量修改
     *
     * @param entity
     * @return
     */
    @PostMapping("/update")
    public ResultVO<String> update(@RequestBody List<CollectSchemeMain> entity) {
        collectSchemeMainServiceImpl.updateBatchById(entity);
        return ResultVOUtils.success();
    }

    /**
     * 创建方案数据，查看生成的汇总数据
     */
    @PostMapping("create")
    public ResultVO<String> create(@RequestBody CodeDto codeDto) {
        collectSchemeMainService.createCollect(codeDto.getCode(),null);
        return ResultVOUtils.success();
    }
    @PostMapping("createOneUser")
    public ResultVO<String> createOneUser(@RequestBody CreateOneUser createOneUser) {
        collectSchemeMainService.createCollect(createOneUser.getCollectSchemeCode(),createOneUser.getBelongArchiveCode());
        return ResultVOUtils.success();
    }
    @PostMapping("createBatchUser")
    public ResultVO<String> createBatchUser(@RequestBody CreateBatchUser createBatchUser) {
        for (String code : createBatchUser.getBelongArchiveCode()) {
            collectSchemeMainService.createCollect(createBatchUser.getCollectSchemeCode(),code);
        }
        return ResultVOUtils.success();
    }
    /**
     * 直接拿到当前的账单数据去生成汇总数据并导出
     */
    @PostMapping("createAndExport")
    public ResultVO<List<ExportExcelVO>> createAndExport(@RequestBody CreateAndCollectDto createAndCollectDto) {
        return ResultVOUtils.success( collectSchemeMainService.createAndCollect(createAndCollectDto));
    }



}
