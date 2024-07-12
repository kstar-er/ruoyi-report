package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.model.DependMain;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.DependMainDto;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface DependMainService extends IService<DependMain> {

    Map<String, DependMain> getDependMainMap(List<String> codeList);

    PageInfo<DependMain> list(DependMainDto dependMainDto);
}

