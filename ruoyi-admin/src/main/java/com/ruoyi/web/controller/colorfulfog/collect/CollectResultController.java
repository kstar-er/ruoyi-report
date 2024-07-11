package com.ruoyi.web.controller.colorfulfog.collect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.CollectResult;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.CollectResultServiceImpl;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectSchemeMainService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 汇总数据明细表表控制层
 *
 * @author kstar
 * @data Wed Feb 21 16:24:17 CST 2024
 */
@RestController
@RequestMapping("/CollectResult")
public class CollectResultController {
    /**
     * 服务对象
     */
    @Autowired
    private CollectResultServiceImpl collectResultServiceImpl;

    @Autowired
    CollectSchemeMainService collectSchemeMainService;


    /**
     * 查询
     *
     * @param entity
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody CollectResult entity, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<CollectResult> pageInfo = new PageInfo<>(collectResultServiceImpl.list(new LambdaQueryWrapper<>(entity)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("/add")
    public ResultVO<String> add(@RequestBody CollectResult entity) {
        collectResultServiceImpl.save(entity);
        return ResultVOUtils.success();
    }

    /**
     * 批量新增
     *
     * @param entity
     * @return
     */
    @PostMapping("/addBatch")
    public ResultVO<String> addBatch(@RequestBody List<CollectResult> entity) {
        collectResultServiceImpl.saveBatch(entity);
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
        collectResultServiceImpl.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    /**
     * 批量修改
     *
     * @param entity
     * @return
     */
    @PostMapping("/update")
    public ResultVO<String> update(@RequestBody List<CollectResult> entity) {
        collectResultServiceImpl.updateBatchById(entity);
        return ResultVOUtils.success();
    }

}
