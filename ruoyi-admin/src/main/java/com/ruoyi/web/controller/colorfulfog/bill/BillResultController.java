package com.ruoyi.web.controller.colorfulfog.bill;

import com.ruoyi.colorfulfog.model.dto.BillResultDto;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.BillMainService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 账单结果控制器
 */
@RestController
@RequestMapping("/billResult")
public class BillResultController {


    @Autowired
    BillMainService billMainService;

    /**
     * 查询账单数据
     */
//    @DataScopeAuth(ruleTable = "cwu_bill_main")
    @PostMapping("/list")
    public ResultVO<BillResultVO> list(@RequestBody BillResultDto billResultDto, @RequestParam Integer currentPage,
                                       @RequestParam Integer pageSize) {
        return ResultVOUtils.success(billMainService
                .list(billResultDto, currentPage, pageSize)) ;

    }

}
