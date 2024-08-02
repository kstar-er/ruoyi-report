package com.ruoyi.colorfulfog.model.mongodb;

import com.ruoyi.colorfulfog.constant.enums.BillCheckStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "billData")
public class BillData extends BaseData{

    // 手动上传数据的标识，0：自动生成，1：手动上传
    private Integer manualFlag;
}
