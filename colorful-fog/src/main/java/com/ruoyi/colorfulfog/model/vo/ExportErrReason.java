package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.ErrReason;
import lombok.Data;

import java.util.List;

@Data
public class ExportErrReason {
    List<ErrReason> errReasonList;
    List<ExportExcelVO> exportExcelVOList;

}
