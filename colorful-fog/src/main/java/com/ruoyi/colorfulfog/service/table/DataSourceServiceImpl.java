
package com.ruoyi.colorfulfog.service.table;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.code.ResponseCode;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.BusinessConstant;
import com.ruoyi.colorfulfog.constant.enums.Enabled;
import com.ruoyi.colorfulfog.modules.dataset.controller.dto.DataSetDto;
import com.ruoyi.colorfulfog.modules.datasetparam.service.DataSetParamService;
import com.ruoyi.colorfulfog.modules.datasource.controller.dto.DataSourceDto;
import com.ruoyi.colorfulfog.modules.datasource.controller.param.ConnectionParam;
import com.ruoyi.colorfulfog.mapper.DataSourceMapper;
import com.ruoyi.colorfulfog.modules.datasource.dao.entity.DataSource;
import com.ruoyi.colorfulfog.modules.datasource.service.impl.JdbcConstants;
import com.ruoyi.colorfulfog.service.table.interfaces.DataSourceService;
import com.ruoyi.colorfulfog.modules.datasource.service.JdbcService;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Raod
 * @desc DataSource 数据集服务实现
 * @date 2021-03-18 12:09:57.728203200
 **/
@Service
@Slf4j
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {


    @Resource(name = "dataSourceRestTemplate")
    private RestTemplate restTemplate;

    @Resource
    private DataSetParamService dataSetParamService;

    @Resource
    private JdbcService jdbcService;


    /**
     * 获取所有数据源
     * @return
     */
    @Override
    public List<DataSource> queryAllDataSource() {
        LambdaQueryWrapper<DataSource> wrapper = Wrappers.lambdaQuery();
        wrapper.select(DataSource::getSourceCode, DataSource::getSourceName)
                .eq(DataSource::getEnableFlag, Enabled.YES.getValue());
        wrapper.orderByDesc(DataSource::getUpdateTime);
        return this.baseMapper.selectList(wrapper);
    }
    @Override
    public List<DataSource> listAllDataSource() {
//        DataPermissionHelper.setVariable("user.userId",LoginHelper.getUserId());
//        return baseMapper.selectAll();
        return baseMapper.selectList(new LambdaQueryWrapper<DataSource>()
                .eq(DataSource::getEnableFlag, Enabled.YES.getValue()));
    }

    /**
     * 测试 连接
     *
     * @param connectionParam
     * @return
     */
    @Override
    public Boolean testConnection(ConnectionParam connectionParam) {
        String sourceType = connectionParam.getSourceType();
        String sourceConfig = connectionParam.getSourceConfig();
        DataSourceDto dto = new DataSourceDto();
        dto.setSourceConfig(sourceConfig);
        switch (sourceType) {
            case JdbcConstants.ELASTIC_SEARCH_SQL:
                testElasticsearchSqlConnection(dto);
                break;
            case JdbcConstants.MYSQL:
            case JdbcConstants.KUDU_IMAPLA:
            case JdbcConstants.ORACLE:
            case JdbcConstants.SQL_SERVER:
            case JdbcConstants.JDBC:
            case JdbcConstants.POSTGRESQL:
                testRelationalDb(dto);
                break;
            case JdbcConstants.HTTP:
                testHttp(dto);
                break;
            default:
                throw new GlobalException(ResponseCode.DATA_SOURCE_TYPE_DOES_NOT_MATCH_TEMPORARILY);
        }
        log.info("测试连接成功：{}", JSONObject.toJSONString(connectionParam));
        return true;

    }
    @Override
    public List<Map<String,Object>> execute(String sql,Integer dataSourceId){
        // 设置数据源信息
        DataSource dataSource = this.getOne(new LambdaQueryWrapper<DataSource>()
                .eq(DataSource::getId, dataSourceId));
        // 设置数据源信息
        DataSourceDto dataSourcedto = new DataSourceDto();
        BeanUtils.copyProperties(dataSource, dataSourcedto);
        dataSourcedto.setDynSentence(sql);

        return execute(dataSourcedto);
    }

    @Override
    public List<Map<String,Object>> execute(DataSourceDto dto) {
        String sourceType = dto.getSourceType();
        switch (sourceType) {
            case JdbcConstants.ELASTIC_SEARCH_SQL:
                return executeElasticsearchSql(dto);
            case JdbcConstants.MYSQL:
            case JdbcConstants.KUDU_IMAPLA:
            case JdbcConstants.ORACLE:
            case JdbcConstants.SQL_SERVER:
            case JdbcConstants.JDBC:
            case JdbcConstants.POSTGRESQL:
                return executeRelationalDb(dto);
            case JdbcConstants.HTTP:
                return executeHttp(dto);
            default:
                throw new GlobalException(ResponseCode.DATA_SOURCE_TYPE_DOES_NOT_MATCH_TEMPORARILY);
        }
    }

    /**
     * 执行sql,统计数据total
     *
     * @param dto
     * @return
     */
    @Override
    public long total(DataSourceDto sourceDto, DataSetDto dto) {
        //区分数据类型
        String sourceType = sourceDto.getSourceType();
        switch (sourceType) {
            case JdbcConstants.ELASTIC_SEARCH_SQL:
                return 0;
            case JdbcConstants.MYSQL:
                return mysqlTotal(sourceDto, dto);
            default:
                throw new GlobalException(ResponseCode.DATA_SOURCE_TYPE_DOES_NOT_MATCH_TEMPORARILY);
        }

    }

    /**
     * 获取mysql count 和添加limit分页信息
     * @param sourceDto
     * @param dto
     * @return
     */
    public long mysqlTotal(DataSourceDto sourceDto, DataSetDto dto){
        String dynSentence = sourceDto.getDynSentence();
        String sql = "select count(1) as count from (" + dynSentence + ") as gaeaExecute";
        sourceDto.setDynSentence(sql);
        List<Map<String,Object>> result = execute(sourceDto);

        //sql 拼接 limit 分页信息
        int pageNumber = Integer.parseInt(dto.getContextData().getOrDefault("pageNumber", "1").toString());
        int pageSize = Integer.parseInt(dto.getContextData().getOrDefault("pageSize", "10").toString());
        String sqlLimit = " limit " + (pageNumber - 1) * pageSize + "," + pageSize;
        sourceDto.setDynSentence(dynSentence.concat(sqlLimit));
        log.info("当前total：{}, 添加分页参数,sql语句：{}", JSONObject.toJSONString(result), sourceDto.getDynSentence());
        return (long) result.get(0).get("count");
    }



    public List<Map<String,Object>> executeElasticsearchSql(DataSourceDto dto) {
        analysisHttpConfig(dto);
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(JSONObject.parseObject(dto.getHeader(), Map.class));
        HttpEntity<String> entity = new HttpEntity<>(dto.getDynSentence(), headers);
        ResponseEntity<JSONObject> exchange;
        try {
            exchange = restTemplate.exchange(dto.getApiUrl(), HttpMethod.valueOf(dto.getMethod()), entity, JSONObject.class);
        } catch (Exception e) {
            log.error("error",e);
            throw new GlobalException(ResponseCode.DATA_SOURCE_CONNECTION_FAILED, e.getMessage());
        }
        if (exchange.getStatusCode().isError()) {
            throw new GlobalException(Objects.requireNonNull(exchange.getBody()).toString());
        }
        List<Map<String,Object>> result;
        try {
            JSONObject body = exchange.getBody();
            //解析es sql数据
            if (null == body) {
                return null;
            }
            JSONArray columns = body.getJSONArray("columns");
            JSONArray rows = body.getJSONArray("rows");
            result = new ArrayList<>();
            for (int i = 0; i < rows.size(); i++) {
                JSONArray row = rows.getJSONArray(i);
                Map<String,Object> jsonObject = new HashMap<>();
                for (int j = 0; j < row.size(); j++) {
                    String name = columns.getJSONObject(j).getString("name");
                    String value = row.getString(j);
                    jsonObject.put(name, value);
                }
                result.add(jsonObject);
            }
        } catch (Exception e) {
            log.error("error",e);
            throw new GlobalException(ResponseCode.ANALYSIS_DATA_ERROR, e.getMessage());
        }
        return result;
    }

    public List<Map<String,Object>> executeRelationalDb(DataSourceDto dto) {
        analysisRelationalDbConfig(dto);
        Connection pooledConnection = null;
        try {
            pooledConnection = jdbcService.getPooledConnection(dto);

            PreparedStatement statement = pooledConnection.prepareStatement(dto.getDynSentence());
            ResultSet rs = statement.executeQuery();

            int columnCount = rs.getMetaData().getColumnCount();

            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rs.getMetaData().getColumnLabel(i);
                columns.add(columnName);
            }
            List<Map<String,Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String,Object> jo = new HashMap<>();
                columns.forEach(t -> {
                    try {
                        Object value = rs.getObject(t);
                        //数据类型转换
                        Object result = dealResult(value);
                        jo.put(t, result);
                    } catch (SQLException throwable) {
                        log.error("error",throwable);
                        throw new GlobalException(ResponseCode.EXECUTE_SQL_ERROR, throwable.getMessage());
                    }
                });
                list.add(jo);
            }
            return list;
        } catch (Exception throwable) {
            log.error("error",throwable);
            throw new GlobalException(ResponseCode.EXECUTE_SQL_ERROR, throwable.getMessage());
        } finally {
            try {
                if (pooledConnection != null) {
                    pooledConnection.close();
                }
            } catch (SQLException throwable) {
                log.error("error",throwable);
                throw new GlobalException(ResponseCode.DATA_SOURCE_CONNECTION_FAILED, throwable.getMessage());
            }
        }
    }

    /**
     * 解决sql返回值 类型问题
     * (through reference chain: java.util.HashMap["pageData"]->java.util.ArrayList[0]->java.util.HashMap["UPDATE_TIME"]->oracle.sql.TIMESTAMP["stream"])
     * @param result
     * @return
     * @throws SQLException
     */
    private Object dealResult(Object result) throws SQLException {
        if (null == result) {
            return result;
        }
        String type = result.getClass().getName();
        if ("oracle.sql.TIMESTAMP".equals(type)) {
            //oracle.sql.TIMESTAMP处理逻辑
            return new Date((Long) JSONObject.toJSON(result));
        }

        return result;
    }

    /**
     * http 执行获取数据
     *
     * @param dto
     */
    public List<Map<String,Object>> executeHttp(DataSourceDto dto) {
        analysisHttpConfig(dto);
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(JSONObject.parseObject(dto.getHeader(), Map.class));
        HttpEntity<String> entity = new HttpEntity<>(dto.getDynSentence(), headers);
        ResponseEntity<Object> exchange;
        try {
            exchange = restTemplate.exchange(dto.getApiUrl(), HttpMethod.valueOf(dto.getMethod()), entity, Object.class);
        } catch (Exception e) {
            log.error("error",e);
            throw new GlobalException(ResponseCode.DATA_SOURCE_CONNECTION_FAILED, e.getMessage());
        }
        if (exchange.getStatusCode().isError()) {
            throw new GlobalException(ResponseCode.DATA_SOURCE_CONNECTION_FAILED, Objects.requireNonNull(exchange.getBody()).toString());
        }
        Object body = exchange.getBody();
        String jsonStr = JSONObject.toJSONString(body);
        List<Map<String,Object>> result = new ArrayList<>();
        if (jsonStr.trim().startsWith(BusinessConstant.LEFT_BIG_BOAST) && jsonStr.trim().endsWith(BusinessConstant.RIGTH_BIG_BOAST)) {
            //JSONObject
            result.add(JSONObject.parseObject(jsonStr));
        } else if (jsonStr.trim().startsWith(BusinessConstant.LEFT_MIDDLE_BOAST) && jsonStr.trim().endsWith(BusinessConstant.RIGHT_MIDDLE_BOAST)) {
            //List
            // 修改别人的代码，这个要改成Map<String,Object>的才能适配,暂时还没有用到的
            result = null;
//            result = JSONArray.parseArray(jsonStr, JSONObject.class);
        } else {
            result.add(new HashMap<>());
        }
        return result;
    }

    /**
     * 关系型数据库 测试连接
     *
     * @param dto
     */
    public void testRelationalDb(DataSourceDto dto) {
        analysisRelationalDbConfig(dto);
        try {
            Connection unPooledConnection = jdbcService.getUnPooledConnection(dto);
            String catalog = unPooledConnection.getCatalog();
            log.info("数据库测试连接成功：{}", catalog);
            unPooledConnection.close();
        } catch (SQLException e) {
            log.error("error",e);
            if (e.getCause() instanceof ClassNotFoundException) {
                throw new GlobalException(ResponseCode.CLASS_NOT_FOUND, e.getCause().getMessage());
            } else {
                throw new GlobalException(ResponseCode.DATA_SOURCE_CONNECTION_FAILED, e.getMessage());
            }

        }
    }

    /**
     * http 测试连接
     *
     * @param dto
     */
    public void testHttp(DataSourceDto dto) {
        analysisHttpConfig(dto);
        String apiUrl = dto.getApiUrl();
        String method = dto.getMethod();
        String body = dto.getBody();
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(JSONObject.parseObject(dto.getHeader(), Map.class));
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Object> exchange;
        try {
            exchange = restTemplate.exchange(apiUrl, HttpMethod.valueOf(method), entity, Object.class);
            if (exchange.getStatusCode().isError()) {
                throw new GlobalException(ResponseCode.DATA_SOURCE_CONNECTION_FAILED, Objects.requireNonNull(exchange.getBody()).toString());
            }
        } catch (RestClientException e) {
            throw new GlobalException(ResponseCode.DATA_SOURCE_CONNECTION_FAILED, e.getMessage());
        }
    }


    /**
     * 关系型数据库 测试连接
     *
     * @param dto
     */
    public void testElasticsearchSqlConnection(DataSourceDto dto) {
        analysisHttpConfig(dto);
        String apiUrl = dto.getApiUrl();
        String method = dto.getMethod();
        String body = dto.getBody();
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(JSONObject.parseObject(dto.getHeader(), Map.class));
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Object> exchange;
        try {
            exchange = restTemplate.exchange(apiUrl, HttpMethod.valueOf(method), entity, Object.class);
            if (exchange.getStatusCode().isError()) {
                throw new GlobalException(ResponseCode.DATA_SOURCE_CONNECTION_FAILED, Objects.requireNonNull(exchange.getBody()).toString());
            }
        } catch (RestClientException e) {
            throw new GlobalException(ResponseCode.DATA_SOURCE_CONNECTION_FAILED, e.getMessage());
        }

    }


    public void analysisRelationalDbConfig(DataSourceDto dto) {
        JSONObject json = JSONObject.parseObject(dto.getSourceConfig());
        // GaeaAssert.isFalse(json.containsKey("jdbcUrl"), ResponseCode.PARAM_IS_NULL,"jdbcUrl not empty");
        // GaeaAssert.isFalse(json.containsKey("driverName"), ResponseCode.PARAM_IS_NULL,"driverName not empty");
        String jdbcUrl = json.getString("jdbcUrl");
        String username = json.getString("username");
        String password = json.getString("password");
        String driverName = json.getString("driverName");
        dto.setJdbcUrl(jdbcUrl);
        dto.setDriverName(driverName);
        dto.setUsername(username);
        dto.setPassword(password);
    }


    /**
     * es通过api获取数据
     *
     * @param dto
     * @return
     */
    public void analysisHttpConfig(DataSourceDto dto) {
        JSONObject json = JSONObject.parseObject(dto.getSourceConfig());
        // GaeaAssert.isFalse(json.containsKey("apiUrl"), ResponseCode.PARAM_IS_NULL,"apiUrl not empty");
        // GaeaAssert.isFalse(json.containsKey("method"), ResponseCode.PARAM_IS_NULL,"method not empty");
        // GaeaAssert.isFalse(json.containsKey("header"), ResponseCode.PARAM_IS_NULL,"header not empty");
        // GaeaAssert.isFalse(json.containsKey("body"), ResponseCode.PARAM_IS_NULL,"body not empty");
        String apiUrl = json.getString("apiUrl");
        String method = json.getString("method");
        String header = json.getString("header");
        String body = json.getString("body");
        //解决url中存在的动态参数
        apiUrl = dataSetParamService.transform(dto.getContextData(), apiUrl);
        //请求头中动态参数
        header = dataSetParamService.transform(dto.getContextData(), header);
        dto.setApiUrl(apiUrl);
        dto.setMethod(method);
        dto.setHeader(header);
        dto.setBody(body);
    }


    public void processAfterOperation(DataSource entity) {
        jdbcService.removeJdbcConnectionPool(entity.getId());
    }
}
