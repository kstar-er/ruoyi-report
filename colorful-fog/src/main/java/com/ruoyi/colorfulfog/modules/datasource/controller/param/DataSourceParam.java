/**/
package com.ruoyi.colorfulfog.modules.datasource.controller.param;

import com.ruoyi.colorfulfog.modules.datasettransform.controller.param.PageParam;
import lombok.Data;

import java.io.Serializable;


/**
* @desc DataSource 数据集查询输入类
* @author Raod
* @date 2021-03-18 12:09:57.728203200
**/
@Data
public class DataSourceParam extends PageParam implements Serializable{

    /** 数据源名称 */
    private String sourceName;

    /** 数据源编码 */
    private String sourceCode;

    /** 数据源类型 DIC_NAME=SOURCE_TYPE; mysql，orace，sqlserver，elasticsearch，接口，javaBean，数据源类型字典中item-extend动态生成表单 */
    private String sourceType;
}
