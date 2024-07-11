package com.ruoyi.colorfulfog.service.busniess.interfaces;

import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;

import java.util.List;


public interface CodeService {
    /**
     * 拿到业务表的id。
     * id的组成:
     * IdTypeEnum.prefix + yyddmm + num（5位）,
     * 其中prefix为业务的前缀，yyddmm为当前日期，num为今天生成的序号
     * @param idTypeEnum
     * @return
     */
    String getCode(IdTypeEnum idTypeEnum);

    /**
     * 批量生成code
     * @param idTypeEnum id类型
     * @param createNum 要生成的数据
     * @return
     */
    List<String> getCode(IdTypeEnum idTypeEnum, int createNum);
}
