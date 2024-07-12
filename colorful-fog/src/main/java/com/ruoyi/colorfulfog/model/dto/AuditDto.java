package com.ruoyi.colorfulfog.model.dto;

import com.ruoyi.colorfulfog.constant.enums.AuditStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditDto {
    // 审核结果，通过或拒绝
    AuditStatusEnum auditStatus;
    // 拒绝的话需要填写原因
    String auditComment;
    // 审核人，填入账号名称
    String auditUser;
    // 需要审核的是哪个ID
    String id;
    Long autoAuditTime;



}
