package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.SchemeUserRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.BindUserDto;
import com.ruoyi.colorfulfog.model.dto.SelectUserDataDto;
import com.ruoyi.colorfulfog.model.dto.TableNameDto;
import com.ruoyi.colorfulfog.model.vo.BelongTableVO;
import com.ruoyi.colorfulfog.model.vo.UserAndSchemeVO;
import com.ruoyi.colorfulfog.model.vo.UserDataVO;

import java.util.List;
import java.util.Map;

public interface SchemeUserRelationService extends IService<SchemeUserRelation> {

    Map<String,List<SchemeUserRelation>> getMapBySchemeCode(List<String> CodeList);
    List<SchemeUserRelation> listBySchemeCode(List<String> codeList);
    List<SchemeUserRelation> listBySchemeCode(String code);
    List<SchemeUserRelation> listByTableName(String code);
    List<SchemeUserRelation> listByTableDto(TableNameDto code);
    List<UserDataVO> selectUserData(SelectUserDataDto selectUserDataDto);
    String bindUserBatch(BindUserDto entity);

    List<BelongTableVO> geBelongTable();
    List<UserAndSchemeVO> getUserAndScheme(TableNameDto tableName, Integer pageNum, Integer pageSize);
}

