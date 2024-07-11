package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.*;
import com.ruoyi.colorfulfog.model.dto.BillResultFlashDto;
import com.ruoyi.colorfulfog.model.dto.SchemeMainDto;
import com.ruoyi.colorfulfog.model.vo.SchemeMainDetailVO;
import com.ruoyi.colorfulfog.model.vo.TestBillResultOriginVO;
import com.ruoyi.colorfulfog.model.vo.TestBillResultVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SchemeMainService extends IService<SchemeMain> {

    /**
     * 传入测试标志，0是正常运行，1是测试，数据，直接返回2行数据
     * @param schemeMainList
     * @param testFlag
     */
    TestBillResultOriginVO startCreateBill(List<String> schemeMainList, boolean testFlag);

    Map<String, SchemeMain> getSchemeMainMap(List<String> schemeMainList);

    Map<String, List<Map<String, Object>>> getAllOrderData(List<SchemeDetail> schemeDetailList,
                                                           Map<String, List<OrderTableRelation>> orderTableRelationMap,
                                                           Map<String,Map<String,ForeignKey>> foreignKeyMap,
                                                           List<DependRule> dependRuleList,
                                                           Map<String, Condition> conditionMap,List<SchemeUserRelation> schemeUserRelations,
                                                           SchemeMain schemeMain,boolean testFlag
                                                           );

    PageInfo<SchemeMain> list(SchemeMainDto schemeMainDto);
    Map<String,List<SchemeMain>>  selectMenu();
    SchemeMainDetailVO selectOneById(Long id);
    TestBillResultVO testScheme(String code);
    List<String> getNeedSchemeMainList(SchemeMain.ExecutionUnit executionUnit,int time);
    SchemeMain addSchemeMain(SchemeMain schemeMain);

    List<SchemeMain> listByCode(List<String> codeList);
    List<SchemeMain> listByCode(Set<String> codeList);
    void flash(BillResultFlashDto billResultDto);
}


