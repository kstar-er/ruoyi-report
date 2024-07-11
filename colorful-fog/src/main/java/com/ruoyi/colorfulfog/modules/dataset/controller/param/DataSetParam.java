/**/
package com.ruoyi.colorfulfog.modules.dataset.controller.param;

import lombok.Data;

import java.io.Serializable;


/**
* @desc DataSet 数据集查询输入类
* @author Raod
* @date 2021-03-18 12:11:31.150755900
**/
@Data
public class DataSetParam  implements Serializable{
    /** 数据集编码 */

    private String setCode;

    /** 数据集名称 */

    private String setName;

    /** 数据源编码 */
    private String sourceCode;

    /** 数据集类型 */
    private String setType;
}
