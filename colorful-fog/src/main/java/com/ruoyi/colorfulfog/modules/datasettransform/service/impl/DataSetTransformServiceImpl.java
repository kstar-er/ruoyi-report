
package com.ruoyi.colorfulfog.modules.datasettransform.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.modules.datasettransform.controller.dto.DataSetTransformDto;
import com.ruoyi.colorfulfog.modules.datasettransform.dao.DataSetTransformMapper;
import com.ruoyi.colorfulfog.modules.datasettransform.dao.entity.DataSetTransform;
import com.ruoyi.colorfulfog.modules.datasettransform.service.DataSetTransformService;
import com.ruoyi.colorfulfog.modules.datasettransform.service.TransformStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @desc DataSetTransform 数据集数据转换服务实现
* @author Raod
* @date 2021-03-18 12:13:15.591309400
**/
@Service
//@RequiredArgsConstructor
public class DataSetTransformServiceImpl extends ServiceImpl<DataSetTransformMapper,DataSetTransform> implements DataSetTransformService {

    private final Map<String, TransformStrategy> queryServiceImplMap = new HashMap<>();
    private ApplicationContext applicationContext;



    public TransformStrategy getTarget(String type) {
        return queryServiceImplMap.get(type);
    }


    @Override
    public List<JSONObject> transform(List<DataSetTransformDto> dataSetTransformDtoList, List<JSONObject> data) {
        if (dataSetTransformDtoList == null || dataSetTransformDtoList.size() <= 0) {
            return data;
        }

        for (DataSetTransformDto dataSetTransformDto : dataSetTransformDtoList) {
            data = getTarget(dataSetTransformDto.getTransformType()).transform(dataSetTransformDto, data);
        }
        return data;
    }
}
