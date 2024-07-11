package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.constant.enums.GetDataFromType;
import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.SchemeDetailParamEnum;
import com.ruoyi.colorfulfog.mapper.SchemeDetailMapper;
import com.ruoyi.colorfulfog.model.DependRule;
import com.ruoyi.colorfulfog.model.SchemeDetail;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.DependRuleService;
import com.ruoyi.colorfulfog.service.table.interfaces.SchemeDetailService;
import com.ruoyi.colorfulfog.utils.GraphUtils;
import com.ruoyi.colorfulfog.utils.JEPUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SchemeDetailServiceImpl extends ServiceImpl<SchemeDetailMapper, SchemeDetail> implements SchemeDetailService {

    @Resource
    CodeService codeService;

    @Resource
    DependRuleService dependRuleService;

    @Override
    public Map<String, List<SchemeDetail>> getSchemeDetailBySchemeCode(List<String> schemeCodeList) {
        return list(new LambdaQueryWrapper<SchemeDetail>().in(SchemeDetail::getSchemeCode, schemeCodeList))
                .stream().collect(Collectors.groupingBy(SchemeDetail::getSchemeCode));
    }

    @Override
    public List<SchemeDetail> listSchemeDetailBySchemeCode(List<String> schemeCodeList) {
        return list(new LambdaQueryWrapper<SchemeDetail>().in(SchemeDetail::getSchemeCode, schemeCodeList));
    }

    @Override
    public List<SchemeDetail> listSchemeDetailBySchemeCode(String schemeCode) {
        return list(new LambdaQueryWrapper<SchemeDetail>().eq(SchemeDetail::getSchemeCode, schemeCode));

    }
    @Override
    public  void addSchemeDetailBatch(List<SchemeDetail> schemeDetailList){

        List<String> codeList = codeService.getCode(IdTypeEnum.RESULT_FIELD,schemeDetailList.size());
        for (int i = 0; i < schemeDetailList.size(); i++) {
            schemeDetailList.get(i).setResultCode(codeList.get(i));
            schemeDetailList.get(i).setCalculateOrder(0); // 没进入到的默认为0
        }
        if (schemeDetailList.stream().allMatch(s->s.getType().equals(SchemeDetailParamEnum.ORDER_DATA))){
            saveBatch(schemeDetailList);
            return;
        }

        List<SchemeDetail> schemeDetailList1 = listSchemeDetailBySchemeCode(schemeDetailList.get(0).getSchemeCode());
        if (CollectionUtils.isNotEmpty(schemeDetailList1)){
            schemeDetailList.addAll(schemeDetailList1);
        }
        if (!schemeDetailList.stream().allMatch(s->s.getType().equals(SchemeDetailParamEnum.ORDER_DATA))){
            calculateOrder(schemeDetailList);
        }
        saveOrUpdateBatch(schemeDetailList);
    }

    @Override
    public void updateSchemeDetailBatch(List<SchemeDetail> schemeDetail){
        List<SchemeDetail> schemeDetailList1 = listSchemeDetailBySchemeCode(schemeDetail.get(0).getSchemeCode());
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
                List<String> variableList = JEPUtils.getVariables(schemeDetail.getExpression());
                for (String variable : variableList) {
                    graph.addNode(nodeMap.get(variable),nodeMap.get(schemeDetail.getResultCode()));
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
                // 根据方案做分组，只拿当前方案的规则
                for (DependRule dependRule : schemeDependRuleMap.get(schemeDetail.getSchemeCode())){
                    graph.addNode(nodeMap.get(dependRule.getOrderField()),nodeMap.get(schemeDetail.getResultCode()));
                }
            }
            if ((schemeDetail.getType().equals(SchemeDetailParamEnum.SINGLE_TAG)||
            schemeDetail.getType().equals(SchemeDetailParamEnum.FOR_MUL_TAG)||
                    schemeDetail.getType().equals(SchemeDetailParamEnum.SUM))
                    &&schemeDetail.getGetDataFrom().equals(GetDataFromType.SCHEME)){
                graph.addNode(nodeMap.get(schemeDetail.getOrderField()),nodeMap.get(schemeDetail.getResultCode()));
                graph.addNode(nodeMap.get(schemeDetail.getGroupByField()),nodeMap.get(schemeDetail.getResultCode()));
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
        List<SchemeDetail> schemeDetailList1 = listSchemeDetailBySchemeCode(schemeDetailList.get(0).getSchemeCode());
        calculateOrder(schemeDetailList1);
        updateBatchById(schemeDetailList1);
    }
}

