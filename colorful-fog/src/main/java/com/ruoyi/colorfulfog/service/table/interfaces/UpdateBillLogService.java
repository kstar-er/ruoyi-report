package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.UpdateBillLog;
import com.ruoyi.colorfulfog.model.dto.SelectOneFieldLogDto;

import java.util.List;

public interface UpdateBillLogService extends IService<UpdateBillLog>{

    List<UpdateBillLog> selectOneFieldLog(SelectOneFieldLogDto entity);
}
