package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.model.ErrReason;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.vo.ExportErrReason;

import java.util.List;

public interface ErrReasonService extends IService<ErrReason>{

    /**
     * 处理错误的数据，批量导入excel,将错误数据插入数据库
     * @param errReason
     * @return
     */
    boolean dealErrReason(List<ErrReason> errReason);

    void saveErrReasonBatch(List<ErrReason> errReason);
    ExportErrReason exportErrReasonList(List<Integer> errReason);

}
