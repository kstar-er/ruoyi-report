package com.ruoyi.mybatis.helper;


import com.ruoyi.common.core.enums.ErrorCodeEnum;
import com.ruoyi.common.core.exception.GlobalException;
import com.ruoyi.mybatis.enums.ExpressionEnum;
import com.ruoyi.mybatis.helper.parser.AbstractExpressionParser;
import com.ruoyi.mybatis.helper.parser.ParserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zwh
 * @date 2023/2/8 11:22
 * 权限注意点：
 * 1.如果expression=ALL，则只会插入"(1=1)"，并不会排除其他规则。因此配置的时候要注意。
 * 同时因为设计的是单角色模式，所以不会产生角色之间权限的冲突。
 * 2.IN表达式的值默认是用英文逗号分隔的，所以值本身不可含有逗号。
 */
@Component
public class ExpressionHelper {
    @Autowired
    private ParserHelper parserHelper;

    public static final String ALL_EXPRESSION = "(1=1)";
    public static final String NONE_EXPRESSION = "(1=2)";
    //rule_field in expression_value，也就是说，expression_value是sql的形式。
    public static final String IN_EXPRESSION = "(`%s` in %s)";
    //rule_field = expression_value
    public static final String EQUAL_EXPRESSION = "(`%s` = '%s')";
    //( expression_value )
    public static final String SQL_PLAIN_EXPRESSION = "(%s)";

    public static final String AND = "and";
    public static final String OR = "or";
    public static final String BLANK = " ";
    public final static String BRACKETS_OPEN = "(";
    public final static String BRACKETS_CLOSE = ")";
    public final static String COMMA = ",";
    public final static String SEMICOLON = "'";

    /**
     * 处理规则组
     * @param dataRuleList 规则列表
     * @return 若规则列表为空，则返回无权限sql
     */
//    public String parseSql(List<DataRule> dataRuleList) {
//        if (dataRuleList == null || dataRuleList.size() == 0) {
//            return NONE_EXPRESSION;
//        }
//
//        Map<String, List<DataRule>> groupMap = dataRuleList.stream().collect(Collectors.groupingBy(DataRule::getGroupCode));
//        StringBuilder result = new StringBuilder();
//        //组装不同组
//        for (Map.Entry<String, List<DataRule>> entry : groupMap.entrySet()) {
//            List<DataRule> andRules = entry.getValue();
//            StringBuilder andSql = new StringBuilder();
//            //组装相同组。
//            // 形式：(xxx) and (xxx) and (xxx)
//            for (int i = 0; i < andRules.size(); i++) {
//                if (i != 0) {
//                    andSql.append(BLANK).append(AND).append(BLANK);
//                }
//                andSql.append(BRACKETS_OPEN).append(parseSql(andRules.get(i))).append(BRACKETS_CLOSE);
//            }
//
//            //拼接在最终结果中
//            //形式：((xxx) and (xxx) and (xxx)) or ((xxx) and (xxx) and (xxx))
//            if (result.length() != 0) {
//                result.append(BLANK).append(OR).append(BLANK);
//            }
//            result.append(BRACKETS_OPEN).append(andSql).append(BRACKETS_CLOSE);
//        }
//
//        //最终结果形式： (  ((xxx) and (xxx) and (xxx)) or ((xxx) and (xxx) and (xxx))  )
//        result.insert( 0, BRACKETS_OPEN);
//        result.append(BRACKETS_CLOSE);
//
//        return result.toString();
//    }
//
//    //处理单个规则
//    private String parseSql(DataRule dataRule) {
//        if (dataRule == null
//                || dataRule.getExpression() == null) {
//            throw new GlobalException(ErrorCodeEnum.DATA_RULE_CONFIG_ERROR);
//        }
//
//        if (ExpressionEnum.ALL.equals(dataRule.getExpression())) {
//            return ALL_EXPRESSION;
//        }
//
//        if (ExpressionEnum.NONE.equals(dataRule.getExpression())) {
//            return NONE_EXPRESSION;
//        }
//
//        if (ExpressionEnum.EQUAL.equals(dataRule.getExpression())) {
//            return String.format(EQUAL_EXPRESSION, dataRule.getRuleField(), dataRule.getExpressionValue());
//        }
//
//        if (ExpressionEnum.IN.equals(dataRule.getExpression())) {
//            return String.format(IN_EXPRESSION, dataRule.getRuleField(), getInConditionValue(dataRule.getExpressionValue()));
//        }
//
//        if (ExpressionEnum.SQL_PLAIN.equals(dataRule.getExpression())) {
//            return String.format(SQL_PLAIN_EXPRESSION, dataRule.getExpressionValue());
//        }
//
//        if (ExpressionEnum.PARSER.equals(dataRule.getExpression())) {
//            AbstractExpressionParser parser = parserHelper.getParser(dataRule.getExpressionValue());
//            if (parser == null) {
//                throw new GlobalException(ErrorCodeEnum.DATA_RULE_CONFIG_ERROR);
//            }
//            return parser.parse(dataRule.getRuleField());
//        }
//
//        return NONE_EXPRESSION;
//    }

    /**
     * IN字符串转换
     * 区域1, 区域2, 区域3  --> ("区域1","区域2","区域3")
     * 江西大区  --> ("江西大区")
     */
    private static String getInConditionValue(String ruleValue) {
        String[] temp = ruleValue.split(",");
        StringBuilder res = new StringBuilder();
        for (String string : temp) {
            res.append(",'").append(string).append("'");
        }
        return "(" + res.substring(1) + ")";
    }
}
