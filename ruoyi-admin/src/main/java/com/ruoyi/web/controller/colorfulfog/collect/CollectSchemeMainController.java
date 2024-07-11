package com.ruoyi.web.controller.colorfulfog.collect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;
import com.ruoyi.colorfulfog.model.CollectSchemeMain;
import com.ruoyi.colorfulfog.model.dto.CodeDto;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.CollectSchemeMainServiceImpl;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectSchemeMainService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 汇总计划主表表控制层
 *
 * @author kstar
 * @data Wed Feb 21 16:24:17 CST 2024
 */
@RestController
@RequestMapping("/collectSchemeMain")
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
        collectSchemeMainService.createCollect(codeDto.getCode());
        return ResultVOUtils.success();
    }

}
