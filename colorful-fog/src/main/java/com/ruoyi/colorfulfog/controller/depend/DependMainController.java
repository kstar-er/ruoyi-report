package com.ruoyi.colorfulfog.controller.depend;

import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;
import com.ruoyi.colorfulfog.model.DependMain;
import com.ruoyi.colorfulfog.model.dto.DependMainDto;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.DependMainService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 依赖表主表
 * 记录依赖表的基本类型
 */
@RestController
@RequestMapping("/colorful-fog/dependMain")
public class DependMainController {
    @Resource
    DependMainService dependMainService;
    @Resource
    CodeService codeService;

    //查询依赖主表
    @DataScopeAuth(ruleTable = "cwu_depend_main")
    @PostMapping("/list")
    public ResultVO<PagedListVO> list(@RequestBody DependMainDto dependMainDto, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        return ResultVOUtils.success(new PagedListVO<>(dependMainService.list(dependMainDto)));
    }

    //新增依赖主表
    @PostMapping("/addDependMain")
    public ResultVO<String> addDependMain(@RequestBody DependMain dependMain){
        dependMain.setDependCode(codeService.getCode(IdTypeEnum.DEPEND_CODE));
        dependMainService.save(dependMain);
        return ResultVOUtils.success();
    }

    //删除依赖主表
    @PostMapping("/deleteDependMain")
    public ResultVO<String> deleteDependMain(@RequestBody List<Integer> Ids){
        List<DependMain> dependMainList = dependMainService.listByIds(Ids);
        if (!dependMainList.isEmpty()){
            dependMainList.forEach(arr->arr.setIsLoseEfficacy(true));
            dependMainService.updateBatchById(dependMainList);
        }
        return ResultVOUtils.success();
    }

    //修改依赖主表
    @PostMapping("/updateDependMain")
    public ResultVO<String> updateDependMain(@RequestBody DependMain dependMain){
        dependMainService.updateById(dependMain);
        return ResultVOUtils.success();
    }
}
