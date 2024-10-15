package com.ruoyi.colorfulfog.controller;

import com.ruoyi.colorfulfog.model.dto.repo.DataSourceDTO;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.RepoService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import oracle.jdbc.proxy.annotation.Post;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 数据报表接口
 */
@RestController
@RequestMapping("/colorful-fog/RepoController")
public class RepoController {
    @Resource
    RepoService repoService;

    @Resource
    RedissonClient redissonClient;

    /**
     * 获取数据
     */
    @PostMapping("/getData")
    public  ResultVO<Map<String, Map<String, Double>>> getData(@RequestBody DataSourceDTO dataSourceDTO) {
        return ResultVOUtils.success(repoService.getData(dataSourceDTO));
    }

    /**
     * 存放json数据
     */
    @PostMapping("/saveRepoJson")
    public ResultVO<String> saveData(@RequestBody Object repoJson) {
        //存放到redis中
        redissonClient.getBucket("json").set(repoJson);
        return ResultVOUtils.success();
    }

    /**
     * 获取json数据
     * @return
     */
    @PostMapping("/getRepoJson")
    public ResultVO<String> getRepoJson() {
        Object object = redissonClient.getBucket("json").get();
        //转为map类型
        Map<String,String> map = (Map<String, String>) object;
        return ResultVOUtils.success(map.get("chartJSON"));
    }



}
