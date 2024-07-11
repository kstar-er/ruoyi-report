package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_bill_result")
public class BillResult {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 绑定的字段的id
     */
    @TableField(value = "result_code")
    private String resultCode;


    /**
     * 结果值
     */
    @TableField(value = "value")
    private String value;

    /**
     * 所属哪条记录
     */
    @TableField(value = "bill_code")
    private String billCode;

    /**
     * 绑定字段的名称
     */
    @TableField(value = "result_name")
    private String resultName;

    /**
     * 所属计划的id
     */
    @TableField(value = "scheme_code")
    private String schemeCode;
    /**
     * 字段类型
     */
    @TableField(value = "result_type")
    private String resultType;

    /**
     * 字段的类型
     */
    @TableField(value = "type")
    private String type;
    /**
     * 计算状态：
     *  计算失败：0
     *  计算成功：1
     *  手动添加：2
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    Integer displayOrder;
    @TableField(value = "batch_code")
    String batchCode;
    public enum CalculateStatusEnum{
        FAIL(0,"计算失败"),
        SUCCESS(1, "计算成功"),
        MANUAL(2, "手动添加");
        @Getter
        final Integer status;
        final String msg;
        CalculateStatusEnum(Integer status, String msg) {
            this.status = status;
            this.msg = msg;
        }

    }
}
