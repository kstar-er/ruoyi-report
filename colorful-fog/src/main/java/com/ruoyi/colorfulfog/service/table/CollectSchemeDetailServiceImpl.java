package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.mapper.CollectSchemeDetailMapper;
import com.ruoyi.colorfulfog.model.CollectSchemeDetail;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectSchemeDetailService;
import com.ruoyi.colorfulfog.utils.GraphUtils;
import com.ruoyi.colorfulfog.utils.JEPUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollectSchemeDetailServiceImpl extends ServiceImpl<CollectSchemeDetailMapper, CollectSchemeDetail> implements CollectSchemeDetailService{

    @Override
    public  List<CollectSchemeDetail> listByCode(String code){
        return list(new LambdaQueryWrapper<CollectSchemeDetail>().
                eq(CollectSchemeDetail::getCollectSchemeCode,code));
    }

    @Override
    public List<CollectSchemeDetail> listSchemeDetailBySchemeCode(List<String> schemeCodeList, SelectTypeEnum selectTypeEnum) {
        if (CollectionUtils.isEmpty(schemeCodeList)){
            return new ArrayList<>();
        }

        schemeCodeList = schemeCodeList.stream().distinct().collect(Collectors.toList());;
        if (selectTypeEnum.equals(SelectTypeEnum.CALC)){
            return list(new LambdaQueryWrapper<CollectSchemeDetail>()
                    .in(CollectSchemeDetail::getCollectSchemeCode, schemeCodeList));
        }else if (selectTypeEnum.equals(SelectTypeEnum.PUSH)){ // 推送的模式获得的数据不包括需要隐藏的这部分
            return list(new LambdaQueryWrapper<CollectSchemeDetail>()
                    .in(CollectSchemeDetail::getCollectSchemeCode, schemeCodeList)
                    .eq(CollectSchemeDetail::getHideWhenPush, 0));
        }else if (selectTypeEnum.equals(SelectTypeEnum.EXPORT)){
            return list(new LambdaQueryWrapper<CollectSchemeDetail>()
                    .in(CollectSchemeDetail::getCollectSchemeCode, schemeCodeList));
        }
        throw new RuntimeException("未知的查询模式");
    }
    @Override
    public  List<CollectSchemeDetail> listBySchemeCode(String code){
        return list(new LambdaQueryWrapper<CollectSchemeDetail>().
                eq(CollectSchemeDetail::getSchemeCode,code));
    }
    @Override
    public void calculateOrder(String collectSchemeCode){
        List<CollectSchemeDetail> collectSchemeDetails = listByCode(collectSchemeCode);
        Map<String, GraphUtils.Node> nodeMap = new HashMap<>();
        for (CollectSchemeDetail schemeDetail : collectSchemeDetails) {
            GraphUtils.Node node = new GraphUtils.Node(schemeDetail.getCollectResultCode());
            nodeMap.put(schemeDetail.getCollectResultCode(),node);
        }
        GraphUtils.Graph graph = new GraphUtils.Graph();
        for (CollectSchemeDetail schemeDetail : collectSchemeDetails) {
            if (schemeDetail.getCollectType().equals(CollectSchemeDetail.CollectTypeEnum.EQUATION)) {
                if (schemeDetail.getExpression()==null||!schemeDetail.getExpression().contains("$")){
                    throw new GlobalException("公式：["+schemeDetail.getCollectResultName()+"]中没有设置表达式，请先设置表达式后再添加");
                }
                List<String> variableList = JEPUtils.getVariables(schemeDetail.getExpression());
                for (String variable : variableList) {
                    graph.addNode(nodeMap.get(variable), nodeMap.get(schemeDetail.getCollectResultCode()));
                }
            }
        }
        GraphUtils.KahnTopo topo = new GraphUtils.KahnTopo(graph);
        topo.process();
        Map<String,List<CollectSchemeDetail>> resultCodeSchemeDetailMap = collectSchemeDetails.stream().collect(Collectors.groupingBy(CollectSchemeDetail::getCollectResultCode));
        int i = 1;
        List<CollectSchemeDetail> needUpdateList = new ArrayList<>();
        for(GraphUtils.Node temp : topo.getResult()){

            for (CollectSchemeDetail collectSchemeDetail : resultCodeSchemeDetailMap.get(temp.val.toString())) {
                collectSchemeDetail.setCalculateOrder(i);
            }
            needUpdateList.addAll(resultCodeSchemeDetailMap.get(temp.val.toString()));
            i++;
        }
        updateBatchById(needUpdateList);
    }
}
