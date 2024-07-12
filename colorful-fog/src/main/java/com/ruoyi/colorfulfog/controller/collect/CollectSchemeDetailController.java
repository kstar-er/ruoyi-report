package com.ruoyi.colorfulfog.controller.collect;

import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;
import com.ruoyi.colorfulfog.model.CollectSchemeDetail;
import com.ruoyi.colorfulfog.model.CollectSchemeMain;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.CollectSchemeDetailServiceImpl;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.DependDataService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 汇总计划明细表表控制层
 *
 * @author kstar
 * @data Wed Feb 21 16:24:17 CST 2024
 */
@RestController
@RequestMapping("/colorful-fog/CollectSchemeDetail")
public class CollectSchemeDetailController {
    /**
     * 服务对象
     */
    @Autowired
    private CollectSchemeDetailServiceImpl collectSchemeDetailServiceImpl;

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
    public ResultVO<PagedListVO> select(@RequestBody CollectSchemeDetail entity, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<CollectSchemeDetail> pageInfo = new PageInfo<>(collectSchemeDetailServiceImpl.list(new LambdaQueryWrapper<>(entity)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("/add")
    public ResultVO<String> add(@RequestBody CollectSchemeDetail entity) {
        if (entity.getCollectResultCode()==null){
            String code = codeService.getCode(IdTypeEnum.COLLECT_RESULT_CODE);
            entity.setCollectResultCode(code);
        }
        collectSchemeDetailServiceImpl.save(entity);
        collectSchemeDetailServiceImpl.calculateOrder(entity.getCollectSchemeCode());
        return ResultVOUtils.success();
    }

    /**
     * 批量新增
     * @param entity
     * @return
     */
    @PostMapping("/addBatch")
    public ResultVO<String> addBatch(@RequestBody List<CollectSchemeDetail> entity) {
        List<String> codeList = codeService.getCode(IdTypeEnum.COLLECT_RESULT_CODE,entity.size());
        int i =0;
        for (CollectSchemeDetail collectSchemeMain : entity) {
            if (collectSchemeMain.getCollectResultCode()!= null){
                continue;
            }
            collectSchemeMain.setCollectResultCode(codeList.get(i));
            i++;
        }
        collectSchemeDetailServiceImpl.saveBatch(entity);
        collectSchemeDetailServiceImpl.calculateOrder(entity.get(0).getCollectSchemeCode());
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
        collectSchemeDetailServiceImpl.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    /**
     * 批量修改
     *
     * @param entity
     * @return
     */
    @PostMapping("/update")
    public ResultVO<String> update(@RequestBody List<CollectSchemeDetail> entity) {
        collectSchemeDetailServiceImpl.updateBatchById(entity);
        collectSchemeDetailServiceImpl.calculateOrder(entity.get(0).getCollectSchemeCode());
        return ResultVOUtils.success();
    }
}
