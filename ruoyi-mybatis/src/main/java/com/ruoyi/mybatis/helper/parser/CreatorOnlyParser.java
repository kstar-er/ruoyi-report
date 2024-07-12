package com.ruoyi.mybatis.helper.parser;


import com.ruoyi.common.core.exception.GlobalException;
import com.ruoyi.common.utils.LoginHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zwh
 * @date 2023/2/8 10:33
 */
@Slf4j
@Component
public class CreatorOnlyParser extends AbstractExpressionParser {
    public static final String EXPRESSION = "(`create_user` = '%s')";

    @Override
    public String parse(String ruleField) {
        Long userId = LoginHelper.getUserId();
        if (userId == null) {
            throw new GlobalException( "无法设置仅创建者可见权限");
        }

        return String.format(EXPRESSION, userId);
    }
}
