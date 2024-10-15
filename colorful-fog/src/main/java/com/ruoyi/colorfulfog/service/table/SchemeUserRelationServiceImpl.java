package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.CollectDataTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.ErrorCodeEnum;
import com.ruoyi.colorfulfog.mapper.SchemeUserRelationMapper;
import com.ruoyi.colorfulfog.model.CollectSchemeDetail;
import com.ruoyi.colorfulfog.model.OrderTableRelation;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.SchemeUserRelation;
import com.ruoyi.colorfulfog.model.dto.BindUserDto;
import com.ruoyi.colorfulfog.model.dto.GetUserBySchemeDto;
import com.ruoyi.colorfulfog.model.dto.SelectUserDataDto;
import com.ruoyi.colorfulfog.model.dto.TableNameDto;
import com.ruoyi.colorfulfog.model.vo.BelongTableVO;
import com.ruoyi.colorfulfog.model.vo.SchemeMainUserVO;
import com.ruoyi.colorfulfog.model.vo.UserAndSchemeVO;
import com.ruoyi.colorfulfog.model.vo.UserDataVO;
import com.ruoyi.colorfulfog.service.table.interfaces.*;
import com.ruoyi.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class SchemeUserRelationServiceImpl extends ServiceImpl<SchemeUserRelationMapper, SchemeUserRelation> implements SchemeUserRelationService {

    @Autowired
    DataSourceService dataSourceService;
    @Resource
    SchemeMainService schemeMainService;
    @Resource
    OrderTableRelationService orderTableRelationService;
    @Resource
    TableFieldRelationService tableFieldRelationService;
    @Resource
    CollectSchemeDetailService collectSchemeDetailService;


    @Override
    public Map<String, List<SchemeUserRelation>> getMapBySchemeCode(List<String> codeList){
        return list(new LambdaQueryWrapper<SchemeUserRelation>().in(SchemeUserRelation::getSchemeCode,codeList))
                .stream().collect(Collectors.groupingBy(SchemeUserRelation::getSchemeCode));
    }

    @Override
    public List<SchemeUserRelation> listBySchemeCode(List<String> codeList){
        if (CollectionUtils.isEmpty(codeList)){
            return null;
        }
       return list(new LambdaQueryWrapper<SchemeUserRelation>().in(SchemeUserRelation::getSchemeCode,codeList));
    }
    @Override
    public List<SchemeUserRelation> listBySchemeCodeAndUserCode(List<String> schemeCode, List<String> userCode){
        if (CollectionUtils.isEmpty(userCode)){
            return null;
        }
        return list(new LambdaQueryWrapper<SchemeUserRelation>().in(SchemeUserRelation::getSchemeCode,schemeCode)
                .in(SchemeUserRelation::getArchiveUserCode,userCode));
    }
    @Override
    public     List<SchemeUserRelation> listBySchemeCode(String code){
        return list(new LambdaQueryWrapper<SchemeUserRelation>().eq(SchemeUserRelation::getSchemeCode,code));
    }

    @Override
    public     List<SchemeUserRelation> listByTableName(String table){
        return list(new LambdaQueryWrapper<SchemeUserRelation>().eq(SchemeUserRelation::getTableName,table));
    }
    @Override
    public List<SchemeUserRelation> listByTableDto(TableNameDto dto){
        return list(new LambdaQueryWrapper<SchemeUserRelation>().eq(SchemeUserRelation::getTableName,dto.getTableName())
                .like(dto.getUserName()!=null,SchemeUserRelation::getArchiveUserName,dto.getUserName()));
    }
    @Override
    public List<UserDataVO> selectUserData(SelectUserDataDto selectUserDataDto){

        String sql = String.format("select %s,%s from %s ",selectUserDataDto.getUserDataCodeField(),selectUserDataDto.getUserDataNameField()
        ,selectUserDataDto.getUserDataTable());
        if (selectUserDataDto.getQuery()!=null && !selectUserDataDto.getQuery().isEmpty()){
            sql = sql + String.format("where %s like '%%%s%%' ",selectUserDataDto.getUserDataNameField(),selectUserDataDto.getQuery());
        }
       Map<String,String> deleteFiledMap =  tableFieldRelationService.getDeleteFlagFieldMap(Collections.singletonList(selectUserDataDto.getUserDataTable()));
        String delFlag = deleteFiledMap.get(selectUserDataDto.getUserDataTable());
        if (delFlag!=null){
            if (sql.contains("where")){
                sql+=" and "+delFlag+" = 0";
            }else {
                sql+=" where "+delFlag+" = 0";
            }
        }

        log.info("sql:{}",sql);
        List<Map<String,Object>> maps = dataSourceService.execute(sql,selectUserDataDto.getDataSourceId());
        // 将maps转换为List<UserDataVo>的形式
        List<UserDataVO> userDataVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(maps) ){
            return userDataVOS;
        }
        for (Map<String, Object> map : maps) {
                     UserDataVO userDataVO = new UserDataVO();
            userDataVO.setCode(map.get(selectUserDataDto.getUserDataCodeField()).toString());
            userDataVO.setName(map.get(selectUserDataDto.getUserDataNameField()).toString());
            userDataVOS.add(userDataVO);
        }
        return  userDataVOS;

    }

    /**
     * 批量绑定用户到计划中，重复的直接跳过绑定，返回导入总数x,成功绑定（重复绑定的数量加到这里）y,未找到用户数量z,
     * @param entity
     */
    @Override
    public String bindUserBatch(BindUserDto entity){
        String sql = String.format("select %s,%s from %s where %s in  ",entity.getUserDataCodeField(),entity.getUserDataNameField()
                ,entity.getUserDataTable(),entity.getUserDataNameField());
        // 将entity.getUserNameList()转换为字符串的形式，以'',
        // 例如：["张三","李四","王五"]转换为'张三','李四','王五'
        String userNameListStr = StringUtils.join(entity.getUserNameList(), "','");
        // 将userNameListStr拼接到sql中，例如：select id,name from user where name in ('张三','李四','王五')
        sql+= "('" + userNameListStr + "')";
        sql+=" order by id desc";
        // 将userNameListStr拼接到sql中，例如：select id,name from user where name in ('张三','李四','王五')
        List<Map<String,Object>> maps = dataSourceService.execute(sql,entity.getDataSourceId());
        // 将maps转换为name:code的map形式
        Map<String,String>  userNameCodeMap = new HashMap<>();
        for (Map<String, Object> map : maps) {
            userNameCodeMap.put(map.get(entity.getUserDataNameField()).toString(),map.get(entity.getUserDataCodeField()).toString());
        }

        // 获取该计划中已经绑定过的用户的数据
        List<SchemeUserRelation> haveBindList = list(new LambdaQueryWrapper<SchemeUserRelation>()
                .eq(SchemeUserRelation::getSchemeCode,entity.getSchemeCode()));
        Map<String,SchemeUserRelation> haveBindMap = haveBindList.stream()
                .collect(Collectors.toMap(SchemeUserRelation::getArchiveUserName, schemeUserRelation -> schemeUserRelation));
        List<String> bindFailNameList = new ArrayList<>();
        List<SchemeUserRelation> schemeUserRelationList = new ArrayList<>();
        for (String name : entity.getUserNameList()) {
            if (userNameCodeMap.get(name) !=null){
                if (haveBindMap.get(name)==null){
                    schemeUserRelationList.add(SchemeUserRelation.builder()
                            .archiveUserCode(userNameCodeMap.get(name))
                            .archiveUserName(name)
                            .schemeCode(entity.getSchemeCode())
                            .build());
                }
            }else {
                bindFailNameList.add(name);
            }
        }
        saveBatch(schemeUserRelationList);
        if (!bindFailNameList.isEmpty()){
            return String.format("导入%s个用户，成功绑定%s个用户，部分用户绑定失败，失败用户：%s",
                                       entity.getUserNameList(),entity.getUserNameList().size()-bindFailNameList.size(),bindFailNameList);
        }
        return String.format("导入%s个用户，全部绑定成功",entity.getUserNameList().size());
    }
    @Override
    public  List<BelongTableVO> geBelongTable(){
        List<SchemeMain> schemeMainList =
        schemeMainService.list(new LambdaQueryWrapper<SchemeMain>()
                        .select(SchemeMain::getUserDataTable)
                ).stream().distinct()
                .collect(Collectors.toList());
        List<String> tableNameList = schemeMainList.stream().map(SchemeMain::getUserDataTable)
                .collect(Collectors.toList());
        Map<String, OrderTableRelation> orderTableRelationMap = orderTableRelationService.list(new LambdaQueryWrapper<OrderTableRelation>()
                .in(OrderTableRelation::getOrderTable,tableNameList)).stream().collect(Collectors.toMap(OrderTableRelation::getOrderTable,item->item));
        List<BelongTableVO> belongTableVOList = new ArrayList<>();
        for (String tableName : tableNameList) {
            belongTableVOList.add(BelongTableVO.builder()
                            .table(tableName)
                            .tableName(orderTableRelationMap.get(tableName).getOrderTableName())
                    .build());
        }
        return belongTableVOList;
    }
    @Override
    public   List<UserAndSchemeVO> getUserAndScheme(TableNameDto tableName, Integer pageNum, Integer pageSize){
        List<SchemeUserRelation> schemeUserRelationList =
                listByTableDto(tableName);

        Map<String,List<SchemeUserRelation>> schemeUserRelationMap = schemeUserRelationList.stream().collect(Collectors.groupingBy(SchemeUserRelation::getArchiveUserCode));
        List<UserAndSchemeVO> userAndSchemeVOList = new ArrayList<>();
        List<String> schemeCodeList = schemeUserRelationList.stream().map(SchemeUserRelation::getSchemeCode).distinct().collect(Collectors.toList());
        List<SchemeMain> schemeMainList = schemeMainService.listByCode(schemeCodeList);
        if (schemeMainList==null){
            throw new GlobalException(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        Map<String,SchemeMain> schemeMainMap = schemeMainList.stream().collect(Collectors.toMap(SchemeMain::getSchemeCode,item->item));

        for (String userCode : schemeUserRelationMap.keySet()) {
            List<SchemeUserRelation> schemeUserRelationList1 = schemeUserRelationMap.get(userCode);
            List<SchemeMainUserVO> schemeMainUserVOS = new ArrayList<>();
            for (SchemeUserRelation schemeUserRelation : schemeUserRelationList1) {
                schemeMainUserVOS.add(SchemeMainUserVO.builder()
                        .schemeName(schemeMainMap.get(schemeUserRelation.getSchemeCode()).getName())
                        .schemeCode(schemeUserRelation.getSchemeCode())
                        .id(schemeUserRelation.getId())
                        .build());
            }
            userAndSchemeVOList.add(UserAndSchemeVO.builder()
                    .schemeMainList(schemeMainUserVOS)
                    .userCode(userCode)
                    .userName(schemeUserRelationMap.get(userCode).get(0).getArchiveUserName())
                    .build());
        }
        return userAndSchemeVOList;
    }
    @Override
    public List<SchemeUserRelation> getUserBySchemeCode(GetUserBySchemeDto getUserBySchemeDto){
        if (getUserBySchemeDto.getCollectDataTypeEnum().equals(CollectDataTypeEnum.SCHEME)){
            return list(new LambdaQueryWrapper<SchemeUserRelation>()
                    .eq(SchemeUserRelation::getSchemeCode,getUserBySchemeDto.getSchemeCode()));
        }else {
            List<CollectSchemeDetail>collectSchemeDetails = collectSchemeDetailService.list(new LambdaQueryWrapper<CollectSchemeDetail>()
                    .eq(CollectSchemeDetail::getCollectSchemeCode,getUserBySchemeDto.getSchemeCode()));

            List<String> schemeCodeList = collectSchemeDetails.stream().map(CollectSchemeDetail::getSchemeCode).filter(Objects::nonNull).collect(Collectors.toList());
            List<SchemeUserRelation> schemeUserRelations = list(new LambdaQueryWrapper<SchemeUserRelation>().in(SchemeUserRelation::getSchemeCode,schemeCodeList));
            Map<String, SchemeUserRelation> uniqueRelations = schemeUserRelations.stream()
                    .collect(Collectors.toMap(
                            SchemeUserRelation::getArchiveUserCode, // 键是ArchiveUserCode
                            s->s,
                            (existing, replacement) -> existing     // 键冲突时保留旧值（或根据需要选择replacement）
                    ));
            // uniqueRelations转换回list的形式
            return new ArrayList<>(uniqueRelations.values());
        }

    }
}

