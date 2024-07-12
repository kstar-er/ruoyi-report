package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.model.CollectResult;
import com.ruoyi.colorfulfog.model.CollectResultMain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CollectBillResultDataVO {
    Map<String,String> keyMap;
    public CollectBillResultDataVO(CollectResultMain billMain, List<CollectResult> billResultList){
        Map<String,String> keyMap1 = new java.util.HashMap<>();
        keyMap1.put("belongArchiveName",billMain.getBelongArchiveName());
        keyMap1.put("belongArchiveCode",billMain.getBelongArchiveCode());
        keyMap1.put("belongUserId", String.valueOf(billMain.getBelongUserId()));
        keyMap1.put("billType",billMain.getBillType());
        keyMap1.put("billCode",billMain.getBillCode());
        keyMap1.put("costTerm",billMain.getCostTerm());
        keyMap1.put("status",billMain.getStatus());
        for (CollectResult billResult : billResultList){
            keyMap1.put(billResult.getResultCode(),billResult.getValue());
        }
        this.keyMap = keyMap1;
    }
}
