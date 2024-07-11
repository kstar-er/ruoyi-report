package com.ruoyi.colorfulfog.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 测试方案是否成功返回的数据，
 * 返回一个源数据，一个测试结果数据
 */
@Data
public class TestBillResultVO {
    List<ResultNameCodeVO> resultNameList;
    List<BillResultDataVO> resultDataList;
    List<ResultNameCodeVO> originNameList;
    List<BillResultDataVO> originDataList;
//    List<Map<String, Map<String, Object>> > originDataList;



}
