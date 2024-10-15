package com.ruoyi.colorfulfog.model.mongodb;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRecord {
    String updateUser;
    String updateTime;
    String originValue;
    String afterValue;
    String comment;
}
