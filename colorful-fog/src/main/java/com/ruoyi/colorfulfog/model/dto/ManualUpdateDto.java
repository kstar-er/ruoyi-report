package com.ruoyi.colorfulfog.model.dto;



import com.ruoyi.colorfulfog.constant.enums.CollectObjectEnum;
import lombok.Data;

/**
 * 手动更新账单数据接口
 */
@Data
public class ManualUpdateDto {
    /**
     * 确认要修改的是账单表还是汇总表的数据
     */
    CollectObjectEnum billType;
    /**
     * 要改哪一条账单的信息
     */
    String billCode;
    /**
     * 要改哪个字段的数据
     */
    String fieldName;
    /**
     * 要修改成什么值
     */
    String updateValue;
}
