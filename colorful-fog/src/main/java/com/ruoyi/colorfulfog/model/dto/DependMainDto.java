package com.ruoyi.colorfulfog.model.dto;

import com.ruoyi.colorfulfog.model.DependMain;
import lombok.Data;

@Data
public class DependMainDto extends DependMain {
    Long createStartTime;
    Long createEndTime;
    Long updateStartTime;
    Long updateEndTime;
}
