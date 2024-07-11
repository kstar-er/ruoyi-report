package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_bill_main")
public class  BillMain extends BaseClass{
    /**
     * 账单编码
     */
    @TableField(value = "bill_code")
    private String billCode;

    /**
     * 对账状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 所属计划id
     */
    @TableField(value = "scheme_code")
    private String schemeCode;

    /**
     * 账期
     */
    @TableField(value = "cost_term")
    private String costTerm;

    /**
     * 应付还是应收账单
     */
    @TableField(value = "bill_type")
    private String billType;

    /**
     * 对应的账号的id，直接控制权限
     */
    @TableField(value = "belong_user_id")
    private Integer belongUserId;

    /**
     * 对应的档案code,司机code或者商家code，做上下级权限控制
     */
    @TableField(value = "belong_archive_code")
    private String belongArchiveCode;

    /**
     * 对应档案中的名称，司机、车队、商家
     */
    @TableField(value = "belong_archive_name")
    private String belongArchiveName;

    @TableField(value = "collect_result_code")
    private String collectResultCode;

    /**
     * 批次号
     */
    @TableField(value = "batch_code")
    private String batchCode;
}
