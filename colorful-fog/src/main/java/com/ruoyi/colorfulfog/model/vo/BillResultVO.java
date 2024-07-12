package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.BillResult;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class BillResultVO {
    List<ResultNameCodeVO> resultNameList;
    PageInfo resultDataList;

}
