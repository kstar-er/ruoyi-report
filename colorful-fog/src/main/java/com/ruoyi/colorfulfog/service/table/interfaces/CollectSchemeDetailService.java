package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.CollectSchemeDetail;

import java.util.List;

public interface CollectSchemeDetailService extends IService<CollectSchemeDetail>{

    List<CollectSchemeDetail> listByCode(String code);


}
