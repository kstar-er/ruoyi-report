package com.ruoyi.colorfulfog.model.dto;

import com.ruoyi.colorfulfog.model.SchemeMain;
import lombok.Data;

@Data
public class SchemeMainDto extends SchemeMain {
    Long createStartTime;
    Long createEndTime;
    Long updateStartTime;
    Long updateEndTime;
}
