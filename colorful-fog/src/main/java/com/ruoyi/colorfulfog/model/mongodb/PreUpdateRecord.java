package com.ruoyi.colorfulfog.model.mongodb;

import com.ruoyi.colorfulfog.constant.enums.AuditStatusEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PreUpdateRecord {
    String id;
    String updateUser;
    String updateTime;
    String originValue;
    String afterValue;
    String fieldName;
    AuditStatusEnum auditStatus;
    // 需要修改的原因
    String auditComment;
    // 审核人，填入账号名称
    String auditUser;
    String auditTime;
    // 拒绝原因
    String refuseComment;
}
