package com.ruoyi.mybatis.helper.parser;

import com.ruoyi.mybatis.helper.parser.ParserNameEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zwh
 * @date 2023/2/8 10:46
 */
@Component
public class ParserHelper {
    public static final Map<ParserNameEnum, AbstractExpressionParser> map = new HashMap<>();

    @Autowired
    public ParserHelper(CreatorOnlyParser creatorOnlyParser,
                        OrgOnlyParser orgOnlyParser,
                        FieldEqUserIdParser fieldEqUserIdParser
                   ) {
        map.put(ParserNameEnum.CREATOR_ONLY, creatorOnlyParser);
        map.put(ParserNameEnum.ORG_ONLY, orgOnlyParser);
        map.put(ParserNameEnum.FIELD_EQ_USER_ID, fieldEqUserIdParser);

    }

    /**
     * 注册一个新运行时解析类，可以在此扩展。
     * @param parserName 解析类名字
     * @param parser 解析类的具体实现类
     */
    public void  registerParser(ParserNameEnum parserName, AbstractExpressionParser parser) {
    }

    public AbstractExpressionParser getParser(ParserNameEnum parserName) {
        return map.get(parserName);
    }

    public AbstractExpressionParser getParser(String parserName) {
        ParserNameEnum parser;
        try {
            parser = ParserNameEnum.valueOf(parserName);
        } catch (Exception e) {
            return null;
        }

        return getParser(parser);
    }
}
