package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateBatchUser {
    String collectSchemeCode;
    List<String> belongArchiveCode;
}
