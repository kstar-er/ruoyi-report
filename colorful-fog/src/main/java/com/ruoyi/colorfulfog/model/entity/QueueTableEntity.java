package com.ruoyi.colorfulfog.model.entity;

import com.ruoyi.colorfulfog.model.ForeignKey;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 有序的表单实例
 * 存储当前表单的表名、上级关联表单的表名
 * @author kstar
 */
@Data
@Builder
public class QueueTableEntity {

    String lastTable;
    String thisTable;

    static public List<QueueTableEntity> queueTableEntityList(String granularityTable, Map<String, Map<String, ForeignKey>> foreignKeyMap,
                                                              Set<String> keySet){
        List<QueueTableEntity> ans = new ArrayList<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(granularityTable);
        while (!queue.isEmpty()){
            String lastTable  = queue.poll();
            Map<String, ForeignKey> foreignKeyMap1 = foreignKeyMap.get(lastTable);
            for (Map.Entry<String, ForeignKey> entry : foreignKeyMap1.entrySet()) {
                if (!keySet.contains(entry.getKey())){
                    continue;
                }
                String thisTable1 = entry.getKey();
                if (thisTable1.equals(granularityTable)){
                    // 等于粒度的这个表直接跳过
                    continue;
                }
                if (!ans.stream().map(QueueTableEntity::getThisTable).collect(Collectors.toList()).contains(thisTable1)){
                    ans.add(QueueTableEntity.builder()
                                    .lastTable(lastTable)
                                    .thisTable(thisTable1)
                                    .build());
                    queue.add(entry.getKey());
                }
            }
        }
        return ans;
    }


}
