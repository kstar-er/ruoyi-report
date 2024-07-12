package com.ruoyi.mybatis.helper.parser;


import com.ruoyi.common.core.exception.GlobalException;
import com.ruoyi.common.utils.LoginHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FieldEqUserIdParser extends AbstractExpressionParser {
    public static final String EXPRESSION = "(`%s` = '%s')";

    @Override
    public String parse(String ruleField) {
        Long userId = LoginHelper.getUserId();
        if (userId == null) {
            throw new GlobalException( "无法设置商家可见权限");
        }

        return String.format(EXPRESSION, ruleField, userId);
    }
}
