package com.ruoyi.colorfulfog.model.mongodb;

import com.ruoyi.colorfulfog.constant.enums.BillCheckStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

/**
 * 汇总数据和账单数据的父类
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseData {
    @Id
    private String  id;
    private String billCode;
    /**
     * 对账状态
     */
    private BillCheckStatusEnum status;
    /**
     * 所属计划id
     */
    private String schemeCode;
    /**
     * 账期
     */
    private String costTerm;
    /**
     * 应付还是应收账单
     */
    private String billType;

    /*
     * 对应的账号的id，直接控制权限
     */
    private Integer belongUserId;

    /**
     * 对应的档案code,司机code或者商家code，做上下级权限控制
     */
    private String belongArchiveCode;

    private String belongArchiveName;

    private String collectResultCode;

    private String batchCode;
    /**
     * 标记时间，用于汇总数据
     */
    private Long tagTime;

    @Field("detail")
    private Map<String,Object> data;
    /**
     * 更新之前的数据
     */
    @Field("lastDetail")
    private Map<String, List<UpdateRecord>> lastData;

}
