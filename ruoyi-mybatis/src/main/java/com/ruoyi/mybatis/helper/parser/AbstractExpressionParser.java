package com.ruoyi.mybatis.helper.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
 * @author zwh
 * @date 2023/2/8 10:33
 */
@Slf4j
@Component
public abstract class AbstractExpressionParser {
    public final static String BRACKETS_OPEN = "(";
    public final static String BRACKETS_CLOSE = ")";
    public final static String COMMA = ",";
    public final static String REPLACE_PREFIX = "$";
    public final static String SEMICOLON = "'";

    public abstract String parse(String ruleField);
}
