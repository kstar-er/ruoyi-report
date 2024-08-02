package com.ruoyi.colorfulfog.controller.dataTable;

import com.ruoyi.colorfulfog.model.ForeignKey;
import com.ruoyi.colorfulfog.model.dto.ChartJsonDto;
import com.ruoyi.colorfulfog.model.dto.RemoveKeyDto;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import com.ruoyi.colorfulfog.model.mongodb.KeyValue;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.ForeignKeyServiceImpl;
import com.ruoyi.colorfulfog.service.table.KeyValueServiceImpl;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    @Autowired
    private KeyValueServiceImpl keyValueServiceImpl;
    @Autowired
    MongoTemplate mongoTemplate;


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
    public ResultVO<String> delete(@RequestBody List<Integer> ids) {
        foreignKeyServiceImpl.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    /**
     * 成对删除外键对
     * @param removeKeyDto
     * @return
     */
    @PostMapping("/removeKeyPair")
    public ResultVO<String> removeKeyPair(@RequestBody RemoveKeyDto removeKeyDto) {
        foreignKeyServiceImpl.removeKeyPair(removeKeyDto);
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
    /**
     * 获取存储的图表样式的json数据
     */
    @PostMapping("getChartJson")
    public ResultVO<String> getChartJson() {
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is("chartJson"));
        KeyValue keyValue = mongoTemplate.findOne(query, KeyValue.class);
        if (keyValue == null){
            return ResultVOUtils.success();

        }
        return ResultVOUtils.success(keyValue.getValue());
    }
    /**
     * 保存存储的图表样式的json数据
     */
    @PostMapping("setChartJson")
    public ResultVO<String> setChartJson(@RequestBody ChartJsonDto chartJsonDto) {
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is("chartJson"));
        KeyValue keyValue = mongoTemplate.findOne(query, KeyValue.class);
        if (keyValue == null){
           keyValue = new KeyValue("chartJson", chartJsonDto.getJson(), "外键ER图表样式的json数据");
           mongoTemplate.save(keyValue);
        }
        Update update = new Update();
        update.set("value", chartJsonDto.getJson());
        mongoTemplate.updateFirst(new Query(Criteria.where("key").is("chartJson")), update, KeyValue.class);
        return ResultVOUtils.success();
    }
}
