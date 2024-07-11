/**/
package com.ruoyi.colorfulfog.modules.dataset.controller.param;

import com.ruoyi.colorfulfog.modules.datasetparam.controller.dto.DataSetParamDto;
import com.ruoyi.colorfulfog.modules.datasettransform.controller.dto.DataSetTransformDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
* @desc DataSet 数据集查询输入类
* @author Raod
* @date 2021-03-18 12:11:31.150755900
**/
@Data
public class DataSetTestTransformParam implements Serializable{

    /** 数据源编码 */
    private String sourceCode;

    /** 动态查询sql或者接口中的请求体 */
    private String dynSentence;

    /** 数据集类型 */
    private String setType;

    /** 请求参数集合 */
    private List<DataSetParamDto> dataSetParamDtoList;

    /** 数据转换集合 */
    private List<DataSetTransformDto> dataSetTransformDtoList;

}
