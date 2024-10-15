package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.*;
import com.ruoyi.colorfulfog.mapper.SchemeDetailMapper;
import com.ruoyi.colorfulfog.model.CollectSchemeDetail;
import com.ruoyi.colorfulfog.model.DependRule;
import com.ruoyi.colorfulfog.model.SchemeDetail;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.dto.CopyFieldDto;
import com.ruoyi.colorfulfog.model.vo.ExportTemplateVO;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectSchemeDetailService;
import com.ruoyi.colorfulfog.service.table.interfaces.DependRuleService;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeDetailService;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeMainService;
import com.ruoyi.colorfulfog.utils.GraphUtils;
import com.ruoyi.colorfulfog.utils.JEPUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ruoyi.colorfulfog.constant.enums.SchemeDetailParamEnum.ORDER_DATA;

@Service
public class SchemeDetailServiceImpl extends ServiceImpl<SchemeDetailMapper, SchemeDetail> implements SchemeDetailService {

    @Resource
    CodeService codeService;

    @Resource
    DependRuleService dependRuleService;

    @Resource
    SchemeMainService schemeMainService;

    @Resource
    CollectSchemeDetailService collectSchemeDetailService;
    @Override
    public Map<String, List<SchemeDetail>> getSchemeDetailBySchemeCode(List<String> schemeCodeList) {
        return list(new LambdaQueryWrapper<SchemeDetail>().in(SchemeDetail::getSchemeCode, schemeCodeList))
                .stream().collect(Collectors.groupingBy(SchemeDetail::getSchemeCode));
    }

    @Override
    public List<SchemeDetail> listSchemeDetailBySchemeCode(List<String> schemeCodeList, SelectTypeEnum selectTypeEnum) {
        if (CollectionUtils.isEmpty(schemeCodeList)){
            return new ArrayList<>();
        }
        schemeCodeList = schemeCodeList.stream().distinct().collect(Collectors.toList());;
        if (selectTypeEnum.equals(SelectTypeEnum.CALC)){
            return list(new LambdaQueryWrapper<SchemeDetail>()
                    .in(SchemeDetail::getSchemeCode, schemeCodeList));
        }else if (selectTypeEnum.equals(SelectTypeEnum.PUSH)){ // 推送的模式获得的数据不包括需要隐藏的这部分
            return list(new LambdaQueryWrapper<SchemeDetail>()
                    .in(SchemeDetail::getSchemeCode, schemeCodeList)
                    .eq(SchemeDetail::getHideWhenPush, 0));
        }else if (selectTypeEnum.equals(SelectTypeEnum.EXPORT)){
            return list(new LambdaQueryWrapper<SchemeDetail>()
                    .in(SchemeDetail::getSchemeCode, schemeCodeList)
                    .eq(SchemeDetail::getHideWhenExport, 0));
        }
        throw new RuntimeException("未知的查询模式");
    }

    @Override
    public List<SchemeDetail> listSchemeDetailBySchemeCode(String schemeCode,SelectTypeEnum selectTypeEnum) {
        if (selectTypeEnum.equals(SelectTypeEnum.CALC)){
            return list(new LambdaQueryWrapper<SchemeDetail>()
                    .eq(SchemeDetail::getSchemeCode, schemeCode));
        }else if (selectTypeEnum.equals(SelectTypeEnum.PUSH)){ // 推送的模式获得的数据不包括需要隐藏的这部分
            return list(new LambdaQueryWrapper<SchemeDetail>()
                    .eq(SchemeDetail::getSchemeCode, schemeCode)
                    .ne(SchemeDetail::getHideWhenPush, 1));
        }else if (selectTypeEnum.equals(SelectTypeEnum.EXPORT)){
            return list(new LambdaQueryWrapper<SchemeDetail>()
                    .eq(SchemeDetail::getSchemeCode, schemeCode)
                    .eq(SchemeDetail::getHideWhenExport, 1));
        }
        else if(selectTypeEnum.equals(SelectTypeEnum.TEMPLATE)){
            return list(new LambdaQueryWrapper<SchemeDetail>()
                    .eq(SchemeDetail::getSchemeCode, schemeCode)
                    .eq(SchemeDetail::getType, ORDER_DATA));
        }
        else {
            throw new RuntimeException("未知的查询模式");
        }

    }

    @Override
    public  void addSchemeDetailBatch(List<SchemeDetail> schemeDetailList){

        List<String> codeList = codeService.getCode(IdTypeEnum.RESULT_FIELD,schemeDetailList.size());
        for (int i = 0; i < schemeDetailList.size(); i++) {
            schemeDetailList.get(i).setResultCode(codeList.get(i));
            schemeDetailList.get(i).setCalculateOrder(0); // 没进入到的默认为0
        }
        if (schemeDetailList.stream().allMatch(s->s.getType().equals(ORDER_DATA))){
            saveBatch(schemeDetailList);
            return;
        }

        List<SchemeDetail> schemeDetailList1 = listSchemeDetailBySchemeCode(schemeDetailList.get(0).getSchemeCode(),SelectTypeEnum.CALC);
        if (!CollectionUtils.isEmpty(schemeDetailList1)){
            schemeDetailList.addAll(schemeDetailList1);
        }
        if (!schemeDetailList.stream().allMatch(s->s.getType().equals(ORDER_DATA))){
            calculateOrder(schemeDetailList);
        }
        saveOrUpdateBatch(schemeDetailList);
    }

    @Override
    public void updateSchemeDetailBatch(List<SchemeDetail> schemeDetail){
        List<SchemeDetail> schemeDetailList1 = listSchemeDetailBySchemeCode(schemeDetail.get(0).getSchemeCode(),SelectTypeEnum.CALC);
        for (SchemeDetail detail : schemeDetailList1) {
            if (detail.getResultCode().equals(schemeDetail.get(0).getResultCode())){
               // 移除当前detail
                schemeDetailList1.remove(detail);
                break;
            }
        }
        schemeDetailList1.add(schemeDetail.get(0));
        calculateOrder(schemeDetailList1);
        updateBatchById(schemeDetailList1);
    }

    /**
     * 重新排列计算顺序，依赖表有拿取方案中的数据的，排在所需字段后面
     * 公式中有需要字段的，排在最后面，其他的可以随便排序。
     * 使用拓扑排序，
     * 1. 创建邻接表存一个有向图
     * 2. 将有向图中入度为0的节点放入队列，删除该节点引出的边以及关联节点入度-1
     * 3. 重复步骤2，直到邻接表为空
     * 4. 如果队列无法清空，则有向图中存在环，则无法计算，抛出异常
     * 5. 将队列中的节点按照顺序取出，即为拓扑排序的结果
     * @param schemeDetailList
     */
    @Override
    public List<SchemeDetail>  calculateOrder(List<SchemeDetail> schemeDetailList){

        // 筛选出依赖类型的字段
        List<String> dependCode
                = schemeDetailList.stream().filter(schemeDetail -> StringUtils.isNotBlank(schemeDetail.getDependCode()))
                .map(SchemeDetail::getDependCode).collect(Collectors.toList());
        List<DependRule> dependRuleList = dependRuleService.getDependRuleByCode(dependCode);
        Map<String,List<DependRule>> dependRuleMap=null;
        if(!CollectionUtils.isEmpty(dependRuleList)){
            dependRuleMap = dependRuleList.stream()
                    .filter(dependRule -> dependRule.getGetDataFrom().equals(GetDataFromType.SCHEME))
                    .collect(Collectors.groupingBy(DependRule::getDependCode));
        }
        // 批量获取依赖数据
        Map<String,SchemeDetail> schemeDetailMap = schemeDetailList.stream().collect(Collectors.toMap(SchemeDetail::getResultCode,s->s));

        // 创建邻接表节点
        Map<String,GraphUtils.Node> nodeMap = new HashMap<>();
        for (SchemeDetail schemeDetail : schemeDetailList) {
            GraphUtils.Node node = new GraphUtils.Node(schemeDetail.getResultCode());
            nodeMap.put(schemeDetail.getResultCode(),node);
        }
        // 创建邻接表的边
        GraphUtils.Graph graph = new GraphUtils.Graph();
        for (SchemeDetail schemeDetail : schemeDetailList) {
            if (schemeDetail.getType().equals(SchemeDetailParamEnum.EQUATION)){
                if (schemeDetail.getExpression()==null||!schemeDetail.getExpression().contains("$")){
                    throw new GlobalException("公式：["+schemeDetail.getResultName()+"]中没有设置表达式，请先设置表达式后再添加","");
                }
                List<String> variableList = JEPUtils.getVariables(schemeDetail.getExpression());
                for (String variable : variableList) {
                    graph.addNode(nodeMap.get(variable),nodeMap.get(schemeDetail.getResultCode()));
                    schemeDetailMap.get(variable).setCalculatedTag(true);
                }
            }
            if (schemeDetail.getType().equals(SchemeDetailParamEnum.DEPEND)){
                if (dependRuleMap==null){
                    continue;
                }
                List<DependRule> dependRuleList1 = dependRuleMap.get(schemeDetail.getDependCode());
                if (dependRuleList1==null){
                    continue;
                }
                Map<String,List<DependRule>> schemeDependRuleMap = dependRuleList1.stream()
                        .collect(Collectors.groupingBy(DependRule::getSchemeCode));
                if (schemeDependRuleMap.get(schemeDetail.getSchemeCode())==null){
                    throw new GlobalException("分组管理表："+schemeDetail.getDependCode()+"中没有绑定当前方案的字段，请先绑定字段后再添加");
                }
                // 根据方案做分组，只拿当前方案的规则
                for (DependRule dependRule : schemeDependRuleMap.get(schemeDetail.getSchemeCode())){
                    graph.addNode(nodeMap.get(dependRule.getOrderField()),nodeMap.get(schemeDetail.getResultCode()));
                    schemeDetailMap.get(dependRule.getOrderField()).setCalculatedTag(true);
                }
            }
            if ((schemeDetail.getType().equals(SchemeDetailParamEnum.SINGLE_TAG)||
            schemeDetail.getType().equals(SchemeDetailParamEnum.FOR_MUL_TAG)||
                    schemeDetail.getType().equals(SchemeDetailParamEnum.SUM))||
                    schemeDetail.getType().equals(SchemeDetailParamEnum.COUNT)||
                    schemeDetail.getType().equals(SchemeDetailParamEnum.MAX)||
                    schemeDetail.getType().equals(SchemeDetailParamEnum.MIN)
                    &&schemeDetail.getGetDataFrom().equals(GetDataFromType.SCHEME)){
                graph.addNode(nodeMap.get(schemeDetail.getOrderField()),nodeMap.get(schemeDetail.getResultCode()));
                schemeDetailMap.get(schemeDetail.getOrderField()).setCalculatedTag(true);
                graph.addNode(nodeMap.get(schemeDetail.getGroupByField()),nodeMap.get(schemeDetail.getResultCode()));
                schemeDetailMap.get(schemeDetail.getGroupByField()).setCalculatedTag(true);
            }
        }
        GraphUtils.KahnTopo topo = new GraphUtils.KahnTopo(graph);
        topo.process();
        Map<String,SchemeDetail> resultCodeSchemeDetailMap = schemeDetailList.stream().collect(Collectors.toMap(SchemeDetail::getResultCode, s->s));
        int i = 1;
        for(GraphUtils.Node temp : topo.getResult()){
            resultCodeSchemeDetailMap.get(temp.val.toString()).setCalculateOrder(i);
            i++;
        }
        return schemeDetailList;
    }
    @Override
    public void deleteSchemeDetail(List<Integer> ids){
        List<SchemeDetail> schemeDetailList = listByIds(ids);
        removeBatchByIds(ids);
        List<SchemeDetail> schemeDetailList1 = listSchemeDetailBySchemeCode(schemeDetailList.get(0).getSchemeCode(), SelectTypeEnum.CALC);
        calculateOrder(schemeDetailList1);
        updateBatchById(schemeDetailList1);
    }
    @Override
    public  void copyField(CopyFieldDto copyFieldDto){
        List<SchemeDetail> schemeDetails = listSchemeDetailBySchemeCode(copyFieldDto.getSourceSchemeCode(),SelectTypeEnum.CALC);

        schemeDetails =  schemeDetails.stream().filter(s->s.getType().equals(ORDER_DATA))
                .collect(Collectors.toList());
        List<String> newCode = codeService.getCode(IdTypeEnum.RESULT_FIELD,schemeDetails.size());
        List<SchemeDetail> newSchemeDetailList = new ArrayList<>();
        int i =0;
        for (SchemeDetail schemeDetail : schemeDetails) {
            if (i<2){//0,1开头的两个字段去掉
                i++;
                continue;
            }
            SchemeDetail schemeDetail1 = new SchemeDetail();
            BeanUtils.copyProperties(schemeDetail,schemeDetail1);
            schemeDetail1.setId(null);
            schemeDetail1.setSchemeCode(copyFieldDto.getTargetSchemeCode());
            schemeDetail1.setResultCode(newCode.get(i++));
            newSchemeDetailList.add(schemeDetail1);
        }
        saveBatch(newSchemeDetailList);
    }
    @Override
    public List<ExportTemplateVO> exportTemplate(String schemeCode){
        List<SchemeDetail> schemeDetailList = listSchemeDetailBySchemeCode(schemeCode,SelectTypeEnum.TEMPLATE);
        List<CollectSchemeDetail> collectSchemeDetailList = collectSchemeDetailService.listBySchemeCode(schemeCode);
        List<String> requiredCode = requiredCode(collectSchemeDetailList);
        SchemeMain schemeMain = schemeMainService.getSchemeMainByCode(schemeCode);
        List<ExportTemplateVO> exportTemplateVOS = new ArrayList<>();
        exportTemplateVOS.add(ExportTemplateVO.builder().resultCode("schemeCode")
                .resultName("方案编码").resultType(ResutlTypeEnum.STRING).build());
        exportTemplateVOS.add(ExportTemplateVO.builder().resultCode("belongArchiveCode")
                .resultName("所属用户编码").resultType(ResutlTypeEnum.STRING).build());
        exportTemplateVOS.add(ExportTemplateVO.builder().resultCode("belongArchiveName")
                .resultName("所属用户名称").resultType(ResutlTypeEnum.STRING).build());
        exportTemplateVOS.add(ExportTemplateVO.builder().resultCode("collectResultCode")
                .resultName("汇总账单编码（填写后刷新汇总账单）").resultType(ResutlTypeEnum.STRING).build());
        for (SchemeDetail schemeDetail : schemeDetailList) {
            Boolean isRequred = false;
            if(schemeDetail.getResultCode().equals(schemeMain.getTimeFieldResultCode())){
                isRequred = true;
            }
            /**
             * 如果在当前字段需要被计算，计算的顺序不为零。在添加字段时已经进行过判断了。
             */
            if (schemeDetail.getCalculateOrder()!=0){
                isRequred = true;
            }
            if (requiredCode.contains(schemeDetail.getResultCode())){
                isRequred = true;
            }
            exportTemplateVOS.add(
                    ExportTemplateVO.builder()
                            .resultCode(schemeDetail.getResultCode())
                            .resultName(schemeDetail.getResultName())
                            .resultType(schemeDetail.getResultType())
                            .isRequired(isRequred)
                            .build()
            );
        }
        return exportTemplateVOS;

    }
    private List<String> requiredCode(List<CollectSchemeDetail> collectSchemeDetailList){
        List<String> requiredCode = new ArrayList<>();

        for (CollectSchemeDetail collectSchemeDetail : collectSchemeDetailList) {
                if(collectSchemeDetail.getGroupByField()!=null){
                    requiredCode.add(collectSchemeDetail.getGroupByField());
                }
                requiredCode.add(collectSchemeDetail.getSchemeResultCode());
        }
        return requiredCode;
    }
    @Override
    public     List<String> getOrderTableName(List<SchemeDetail> schemeDetails){
        return schemeDetails.stream()
                .filter(schemeDetail -> schemeDetail.getType().equals(ORDER_DATA))
                .distinct()
                .map(SchemeDetail::getOrderTable).collect(Collectors.toList());
    }
}

