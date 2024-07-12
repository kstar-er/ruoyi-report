package com.ruoyi.colorfulfog.model.mongodb;

import com.ruoyi.colorfulfog.constant.enums.AuditStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "collectBillData")
public class CollectBillData extends BaseData{

    AuditStatusEnum auditStatus;
    // 拒绝的话需要填写原因
    String auditComment;
    // 审核人，填入账号名称
    String auditUser;
    Long auditTime;
    Long autoAuditTime;

}
