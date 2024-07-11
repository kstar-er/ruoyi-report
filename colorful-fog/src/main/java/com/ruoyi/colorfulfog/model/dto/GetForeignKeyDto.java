package com.ruoyi.colorfulfog.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetForeignKeyDto {
    String foreignKey;
    String useDataTableName;
}
