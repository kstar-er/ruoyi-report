package com.ruoyi.colorfulfog.controller.dataTable;

import com.ruoyi.colorfulfog.model.ForeignKey;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.ForeignKeyServiceImpl;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 外键关联表表控制层
 *
 * @author kstar
 * @data Thu May 09 13:36:09 CST 2024
 */
@RestController
@RequestMapping("/colorful-fog/ForeignKey")
public class ForeignKeyController {
    /**
     * 服务对象
     */
    @Autowired
    private ForeignKeyServiceImpl foreignKeyServiceImpl;


    /**
     * 查询
     *
     * @param entity
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PostMapping("/select")
    public ResultVO<PagedListVO> select(@RequestBody ForeignKey entity, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<ForeignKey> pageInfo = new PageInfo<>(foreignKeyServiceImpl.list(new LambdaQueryWrapper<>(entity)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("/add")
    public ResultVO<String> add(@RequestBody ForeignKey entity) {
        foreignKeyServiceImpl.save(entity);
        return ResultVOUtils.success();
    }

    /**
     * 批量新增
     *
     * @param entity
     * @return
     */
    @PostMapping("/addBatch")
    public ResultVO<String> addBatch(@RequestBody List<ForeignKey> entity) {
        foreignKeyServiceImpl.saveBatch2(entity);
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
        foreignKeyServiceImpl.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    /**
     * 批量修改
     *
     * @param entity
     * @return
     */
    @PostMapping("/update")
    public ResultVO<String> update(@RequestBody List<ForeignKey> entity) {
        foreignKeyServiceImpl.updateBatchById(entity);
        return ResultVOUtils.success();
    }
}