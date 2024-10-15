package com.ruoyi.colorfulfog.service;

import com.ruoyi.colorfulfog.model.dto.repo.DataSourceDTO;
import com.ruoyi.colorfulfog.model.dto.repo.FilterCriteria;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.BillMainService;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectResultMainService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class repoServiceImpl implements RepoService {

    @Resource
    private CollectResultMainService collectResultMainService;

    @Resource
    BillMainService billMainService;

    @Override
    public Map<String, Map<String, Double>> getData(  DataSourceDTO dataSourceDTO){
        //根据接口名判断调取哪个接口
        String sourceInterfaceName = dataSourceDTO.getSourceInterfaceName();
        if ("collectResult".equals(sourceInterfaceName)){
            return  collectResultMainService.list(dataSourceDTO,dataSourceDTO.getFilterCriteria());
        }else if ("billMain".equals(sourceInterfaceName)){
            return billMainService.list(dataSourceDTO,dataSourceDTO.getFilterCriteria());

        }

        return null;
    }

}
