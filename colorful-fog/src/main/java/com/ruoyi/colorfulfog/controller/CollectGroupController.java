package com.ruoyi.colorfulfog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.CollectGroup;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.CollectGroupServiceImpl;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 汇总方案分组管理表控制层
 *
 * @author kstar
 * @data Fri Aug 16 11:09:52 CST 2024
 */
@RestController
@RequestMapping("/CollectGroup")
public class CollectGroupController {
    /**
     * 服务对象
     */
    @Autowired
    private CollectGroupServiceImpl collectGroupServiceImpl;


    /**
     * 查询
     *
     * @param entity
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody CollectGroup entity, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<CollectGroup> pageInfo = new PageInfo<>(collectGroupServiceImpl.list(new LambdaQueryWrapper<>(entity)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("/add")
    public ResultVO<String> add(@RequestBody CollectGroup entity) {
        collectGroupServiceImpl.save(entity);
        return ResultVOUtils.success();
    }

    /**
     * 批量新增
     *
     * @param entity
     * @return
     */
    @PostMapping("/addBatch")
    public ResultVO<String> add(@RequestBody List<CollectGroup> entity) {
        collectGroupServiceImpl.saveBatch(entity);
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
        collectGroupServiceImpl.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    /**
     * 批量修改
     * @param entity
     * @return
     */
    @PostMapping("/update")
    public ResultVO<String> update(@RequestBody List<CollectGroup> entity) {
        collectGroupServiceImpl.updateBatchById(entity);
        return ResultVOUtils.success();
    }
}
