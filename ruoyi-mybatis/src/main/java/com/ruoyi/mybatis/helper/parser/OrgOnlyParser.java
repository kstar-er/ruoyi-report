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
public class OrgOnlyParser extends AbstractExpressionParser {
    public static final String EXPRESSION = "(`create_org` = '%s')";

    @Override
    public String parse(String ruleField) {
        Long deptId = LoginHelper.getDeptId();
        if (deptId == null) {
            throw new GlobalException( "无法设置组织内可见权限");
        }

        return String.format(EXPRESSION, deptId);
    }
}
