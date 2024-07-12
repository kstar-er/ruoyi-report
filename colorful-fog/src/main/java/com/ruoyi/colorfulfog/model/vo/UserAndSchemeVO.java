package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.SchemeMain;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAndSchemeVO {
    private String userName;
    private String userCode;
    List<SchemeMainUserVO> schemeMainList;

}
