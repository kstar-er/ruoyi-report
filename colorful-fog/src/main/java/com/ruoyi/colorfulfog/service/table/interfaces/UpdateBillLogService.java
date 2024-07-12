package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.model.UpdateBillLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.SelectOneFieldLogDto;
import com.ruoyi.colorfulfog.model.mongodb.UpdateRecord;

import java.util.List;
import java.util.Map;

public interface UpdateBillLogService extends IService<UpdateBillLog>{

    Map<String, List<UpdateRecord>> selectOneFieldLog(SelectOneFieldLogDto entity);
}
