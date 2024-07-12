package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 实时生成数据的DTO类
 */
@Data
public class CreateAndCollectDto {
    String collectCode;
    List<String> belongArchiveCode;
    TimeDto timeDto;
    List<String> schemeCode;
}
