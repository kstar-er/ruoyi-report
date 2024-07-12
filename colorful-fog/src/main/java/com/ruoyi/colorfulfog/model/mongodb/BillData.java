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

    //占位字段，无用
    private String tmp;
}
