package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.constant.enums.CollectDataTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.ExecutionTimeUnit;
import com.ruoyi.colorfulfog.model.*;
import com.ruoyi.colorfulfog.model.dto.*;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.vo.SchemeMainDetailVO;
import com.ruoyi.colorfulfog.model.vo.TestBillResultOriginVO;
import com.ruoyi.colorfulfog.model.vo.TestBillResultVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SchemeMainService extends IService<SchemeMain> {

    /**
     * 传入测试标志，
     * 0是正常运行，生成数据并保存
     * 1是测试，数据，直接返回2行数据,不保存数据库
     * 2是导出实时数据，生成全部数据，不保存数据库。
     *
     * @param schemeMainList
     * @param testFlag
     * @param userCodeList
     */
    TestBillResultOriginVO startCreateBill(List<String> schemeMainList, Integer testFlag, TimeDto timeDto,List<String> userCodeList);
    TestBillResultOriginVO startCreateBill(List<String> schemeMainList, Integer testFlag);
    List<SchemeUserRelation> checkUserData(List<SchemeUserRelation> schemeUserRelationList, TimeDto timeDto,
                                           String schemeCode, CollectDataTypeEnum collectDataTypeEnum,Integer testFlag);

    Map<String, SchemeMain> getSchemeMainMap(List<String> schemeMainList);

    Map<String, List<Map<String, Object>>> getAllOrderData(List<SchemeDetail> schemeDetailList,
                                                           Map<String, List<OrderTableRelation>> orderTableRelationMap,
                                                           Map<String,Map<String, List<ForeignKey>>> foreignKeyMap,
                                                           List<DependRule> dependRuleList,
                                                           Map<String, Condition> conditionMap,List<SchemeUserRelation> schemeUserRelations,
                                                           SchemeMain schemeMain,Integer testFlag,TimeDto timeDto,Map<String,String> deleteFiledMap
                                                           );

    PageInfo<SchemeMain> list(SchemeMainDto schemeMainDto);
    Map<String,List<SchemeMain>>  selectMenu();
    SchemeMainDetailVO selectOneById(Long id);
    TestBillResultVO testScheme(String code);
    List<String> getNeedSchemeMainList(ExecutionTimeUnit executionUnit, int time);
    SchemeMain addSchemeMain(SchemeMain schemeMain);

    List<SchemeMain> listByCode(List<String> codeList);
    List<SchemeMain> listByCode(Set<String> codeList);
    SchemeMain getSchemeMainByCode(String code);
    List<BillData> getFlashData(BillResultFlashDto billResultDto);
    void flash(BillResultFlashDto billResultDto);
    void flashByCollectCode(BillResultFlashDto billResultDto);

    List<BillData> createAndCollect(CreateAndCollectDto billResultDto);
    void deleteSchemeMain(List<Integer> idList);

    /**
     * 对方案中存在的商家进行单独的账单数据重新生成，
     * 删除掉原有的账单数据，生成新的账单数据
     * 仅删除最新的批次号的账单，不能全删了。
     * @param reCreateByUserCode
     */
    void reCreateByUserCode(ReCreateByUserCode reCreateByUserCode);

}


