package com.ruoyi.web.controller.colorfulfog.scheme;

import com.github.pagehelper.PageHelper;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.dto.BillResultFlashDto;
import com.ruoyi.colorfulfog.model.dto.CodeDto;
import com.ruoyi.colorfulfog.model.dto.SchemeMainDto;
import com.ruoyi.colorfulfog.model.vo.PagedListVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.model.vo.SchemeMainDetailVO;
import com.ruoyi.colorfulfog.model.vo.TestBillResultVO;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeMainService;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 计划任务主表
 *
 */
@RestController
@RequestMapping("/schemeMain")
public class SchemeMainController {
    @Resource
    SchemeMainService schemeMainService;

    @Resource
    CodeService codeService;
    /**
     * 测试创建账单记录
     * @param codeList
     * @return
     */
    @PostMapping("/create")
    public ResultVO<Void> create(@RequestBody CodeDto codeList) {
        schemeMainService.startCreateBill(Collections.singletonList(codeList.getCode()),false);
        return ResultVOUtils.success();
    }

    @PostMapping("/testScheme")
    public ResultVO<TestBillResultVO> testScheme(@RequestBody CodeDto code) {
        return ResultVOUtils.success(schemeMainService.testScheme(code.getCode()));
    }

    /**
     * 根据id查询出这个计划的所有详细信息
     * @param id
     * @return
     */

    @PostMapping("selectOneById")
    public ResultVO<SchemeMainDetailVO> selectOneById(@RequestParam Long id){
        return ResultVOUtils.success(schemeMainService.selectOneById(id));
    }

    /**
     * 按菜单的形式查询所有的计划
     * @return
     */

    @PostMapping("selectMenu")
    public ResultVO<Map<String,List<SchemeMain>>> selectMenu(){
        return ResultVOUtils.success(schemeMainService.selectMenu());
    }

    @PostMapping("/list")
    public ResultVO<PagedListVO> list(@RequestBody SchemeMainDto schemeMainDto, @RequestParam Integer currentPage, @RequestParam Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        return ResultVOUtils.success(new PagedListVO<>(schemeMainService.list(schemeMainDto)));
    }




    //新增计划内容主表
    @PostMapping("/addSchemeMain")
    public ResultVO<SchemeMain> addSchemeMain(@RequestBody SchemeMain schemeMain){
        return ResultVOUtils.success(schemeMainService.addSchemeMain(schemeMain));
    }

    //删除计划内容主表
    @PostMapping("/deleteSchemeMain")
    public ResultVO<String> deleteSchemeMain(@RequestBody List<Integer> ids){
        schemeMainService.removeBatchByIds(ids);
        return ResultVOUtils.success();
    }

    //修改计划内容主表
    @PostMapping("/updateSchemeMain")
    public ResultVO<String> updateSchemeMain(@RequestBody SchemeMain schemeMain){
        schemeMainService.updateById(schemeMain);
        return ResultVOUtils.success();
    }
    /**
     * 刷新账单接口，对计算失败的数据进行覆盖，暂定传入方案和批次号修改
     */
    @PostMapping("flash")
    public ResultVO<String> flash(@RequestBody BillResultFlashDto billResultDto) {
        schemeMainService.flash(billResultDto);
        return ResultVOUtils.success();
    }

}
