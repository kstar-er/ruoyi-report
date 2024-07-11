
package com.ruoyi.colorfulfog.modules.datasource.dao.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.colorfulfog.model.BaseClass;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



/**
* @description 数据源 entity
* @author Raod
* @date 2021-03-18 12:09:57.728203200
**/
@Data
@TableName(keepGlobalPrefix=true, value="cwu_data_source")
public class DataSource extends BaseClass {

    @ApiModelProperty(value = "数据源编码")
    @TableField(value = "source_code")
    private String sourceCode;

    @TableField(value = "source_name")
    @ApiModelProperty(value = "数据源名称")
    private String sourceName;

    @ApiModelProperty(value = "数据源描述")
    @TableField(value = "source_desc")
    private String sourceDesc;

    @ApiModelProperty(value = "数据源类型 DIC_NAME=SOURCE_TYPE; mysql，orace，sqlserver，elasticsearch，接口，javaBean，数据源类型字典中item-extend动态生成表单")
    @TableField(value = "source_type")
    private String sourceType;

    @ApiModelProperty(value = "数据源连接配置json：关系库{ jdbcUrl:'', username:'', password:'','driverName':''}ES-sql{ apiUrl:'http://127.0.0.1:9092/_xpack/sql?format=json','method':'POST','body':'{\"query\":\"select 1\"}' }  接口{ apiUrl:'http://ip:port/url', method:'' } javaBean{ beanNamw:'xxx' }")
    @TableField(value = "source_config")
    private String sourceConfig;

    @TableField(value = "enable_flag")
    @ApiModelProperty(value = "0--已禁用 1--已启用  DIC_NAME=ENABLE_FLAG")
    private Integer enableFlag;




}
