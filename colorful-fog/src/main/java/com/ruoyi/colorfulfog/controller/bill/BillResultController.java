package com.ruoyi.colorfulfog.controller.bill;

import com.ruoyi.colorfulfog.model.dto.BillResultDto;
import com.ruoyi.colorfulfog.model.dto.CodeDto;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.BillMainService;
import com.ruoyi.colorfulfog.service.table.interfaces.BillResultService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.ruoyi.mybatis.annotation.DataScopeAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账单结果控制器
 */
@RestController
@RequestMapping("/colorful-fog/billResult")
public class BillResultController {


    @Autowired
    BillMainService billMainService;

    /**
     * 查询账单数据
     */
    //@DataScopeAuth(ruleTable = "cwu_bill_main")
    @PostMapping("/list")
    public ResultVO<BillResultVO> list(@RequestBody BillResultDto billResultDto, @RequestParam Integer currentPage,
                                       @RequestParam Integer pageSize) {
        return ResultVOUtils.success(billMainService
                .list(billResultDto, currentPage, pageSize)) ;

    }

    /**
     * 账单数据设为无效
     */
    @PostMapping("/setInValid")
    public ResultVO<String> setInValid(@RequestBody List<String> idList) {
        String ans =  billMainService.setInValid(idList);
        return ResultVOUtils.success(ans);

    }


}
