package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 汇总数据主表
 */
@ApiModel(description = "汇总数据主表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_collect_result_main")
public class CollectResultMain extends BaseClass{
    /**
     * 账单编码
     */
    @TableField(value = "bill_code")
    @ApiModelProperty(value = "账单编码")
    private String billCode;

    /**
     * 对账状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "对账状态")
    private String status;

    /**
     * 所属计划id
     */
    @TableField(value = "scheme_code")
    @ApiModelProperty(value = "所属计划id")
    private String schemeCode;

    /**
     * 账期
     */
    @TableField(value = "cost_term")
    @ApiModelProperty(value = "账期,0是当月，-1是上月")
    private String costTerm;
    /**
     * 应付还是应收账单
     */
    @TableField(value = "bill_type")
    @ApiModelProperty(value = "应付还是应收账单")
    private String billType;

    /**
     * 对应的账号的id，直接控制权限
     */
    @TableField(value = "belong_user_id")
    @ApiModelProperty(value = "对应的账号的id，直接控制权限")
    private Integer belongUserId;

    /**
     * 对应的档案code,司机code或者商家code，做上下级权限控制
     */
    @TableField(value = "belong_archive_code")
    @ApiModelProperty(value = "对应的档案code,司机code或者商家code，做上下级权限控制")
    private String belongArchiveCode;

    /**
     * 对应档案中的名称，司机、车队、商家
     */
    @TableField(value = "belong_archive_name")
    @ApiModelProperty(value = "对应档案中的名称，司机、车队、商家")
    private String belongArchiveName;

    @TableField(value = "collect_result_code")
    @ApiModelProperty(value = "被汇聚时的code")
    private String collectResultCode;


}
