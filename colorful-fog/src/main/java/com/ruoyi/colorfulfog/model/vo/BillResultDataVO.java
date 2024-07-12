package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.BillResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BillResultDataVO {

//    BillMain billMain;
//    List<BillResult> billResultList;
    Map<String,String> keyMap;
    public BillResultDataVO(BillMain billMain,List<BillResult> billResultList){
        Map<String,String> keyMap1 = new java.util.HashMap<>();
        keyMap1.put("belongArchiveName",billMain.getBelongArchiveName());
        keyMap1.put("belongArchiveCode",billMain.getBelongArchiveCode());
        keyMap1.put("belongUserId", String.valueOf(billMain.getBelongUserId()));
        keyMap1.put("billType",billMain.getBillType());
        keyMap1.put("billCode",billMain.getBillCode());
        keyMap1.put("costTerm",billMain.getCostTerm());
        keyMap1.put("status",billMain.getStatus().getMsg());
        keyMap1.put("batchCode",billMain.getBatchCode());
        for (BillResult billResult : billResultList){
            keyMap1.put(billResult.getResultCode(),billResult.getValue());
        }
        this.keyMap = keyMap1;
    }

}
