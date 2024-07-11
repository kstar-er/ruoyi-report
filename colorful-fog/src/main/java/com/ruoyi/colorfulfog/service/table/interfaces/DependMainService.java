package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.DependMain;
import com.ruoyi.colorfulfog.model.dto.DependMainDto;

import java.util.List;
import java.util.Map;

public interface DependMainService extends IService<DependMain> {

    Map<String, DependMain> getDependMainMap(List<String> codeList);

    PageInfo<DependMain> list(DependMainDto dependMainDto);
}

