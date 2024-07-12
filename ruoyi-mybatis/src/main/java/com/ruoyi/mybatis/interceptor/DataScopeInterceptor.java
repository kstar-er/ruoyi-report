package com.ruoyi.mybatis.interceptor;


import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.ruoyi.mybatis.contextholder.AuthInfoContextHolder;
import com.ruoyi.mybatis.entity.AuthInfoEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class DataScopeInterceptor implements InnerInterceptor {
    @SneakyThrows
    @Override
    public void beforeQuery(Executor executor,
                            MappedStatement ms,
                            Object parameter,
                            RowBounds rowBounds,
                            ResultHandler resultHandler,
                            BoundSql boundSql) throws SQLException {
        AuthInfoEntity authEntity = AuthInfoContextHolder.getAuthEntity();
        //清空是为了防止死循环。因为在拼接sql的过程中也需要通过数据库查询权限。
        AuthInfoContextHolder.clear();
        if (authEntity == null || !authEntity.getIsAuth()) {
            return;
        }

        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        String originSql = mpBoundSql.sql();
        Statement statement = CCJSqlParserUtil.parse(originSql);

        //如果不是查询，目前的设计是不进行权限设置。
        if (!(statement instanceof Select)) {
            //设置回去，可以做到一个注解多次校验。
            AuthInfoContextHolder.setEntity(authEntity);
            return;
        }

        Select select = (Select) statement;
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableList = tablesNamesFinder.getTableList(select);

        //本系统中，默认只查单表。
        //需要注意的是，这个拦截器只拦截mp自动生成的sql。
        if (tableList.size() != 1) {
            log.warn("系统查询出现了多表查询！sql语句如下:{}", originSql);
//            AuthInfoContextHolder.setEntity(authEntity);
//            return;
        }
        String tableName = tableList.get(0);
        String ruleTableEnum = authEntity.getParams().get("ruleTable").toString();
        if (!ruleTableEnum.toString().equals(tableName)) {
            AuthInfoContextHolder.setEntity(authEntity);
            return;
        }

        processSingleSelect(select, authEntity.getParams().get("sqlPart").toString());

        String authSql = statement.toString();
        log.info( "执行权限sql："
                + "用户id及账号："
                + "，表名："
                + tableName
                + "，拼接后的sql："
                + authSql);

        mpBoundSql.sql(authSql);

        //设置回去，可以做到一个注解多次校验。
        AuthInfoContextHolder.setEntity(authEntity);
    }

    private void processSingleSelect(Select select, String sqlPart) throws JSQLParserException {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

        Expression expression = CCJSqlParserUtil.parseCondExpression(sqlPart);
        Expression where = plainSelect.getWhere();
        if (where == null) {
            plainSelect.setWhere(new Parenthesis(expression));
        } else {
            plainSelect.setWhere(new AndExpression(plainSelect.getWhere(),expression));
        }
    }
}
