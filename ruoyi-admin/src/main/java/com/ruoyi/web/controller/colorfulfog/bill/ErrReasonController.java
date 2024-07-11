package com.ruoyi.web.controller.colorfulfog.bill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.ErrReason;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.ErrReasonService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 错误原因控制器
 */
@RestController
@RequestMapping("/errReason")
public class ErrReasonController {

    @Autowired
    ErrReasonService errReasonService;

//    @DataScopeAuth(ruleTable = "cwu_err_reason")
    @PostMapping("/list")
    public ResultVO<PagedListVO> select(@RequestBody ErrReason errReason,
                                        @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        PageInfo<ErrReason> pageInfo = new PageInfo<>(errReasonService.list(new LambdaQueryWrapper<>(errReason)));
        return ResultVOUtils.success(new PagedListVO<>(pageInfo));
    }
    /**
     * 处理错误原因，直接导入excel
     */
    @PostMapping("/dealErr")
    public ResultVO<String> dealErr(@RequestBody List<ErrReason> errReasons){
        if (errReasonService.dealErrReason(errReasons)){
            return ResultVOUtils.success("已全部处理成功");
        };
        return ResultVOUtils.success("依赖分类数据已处理成功，按范围分类数据请手动添加对应的范围");
    }
}
