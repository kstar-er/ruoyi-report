package com.ruoyi.colorfulfog.config.mp;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.utils.LoginHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;


@Primary
@Component
@Slf4j
public class FieldFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", Optional.ofNullable(metaObject.getValue("createTime")).orElse(new Date().getTime()), metaObject);
        this.setFieldValByName("updateTime", new Date().getTime(), metaObject);
        //如果当前线程没有登录，则将创建/更新者设为机器人
        // Long 类型转换为Integer
        this.setFieldValByName("createUser", LoginHelper.getUserId(), metaObject);
        this.setFieldValByName("updateUser", LoginHelper.getUserId(), metaObject);
        //如果当前线程没有登录，则创建/更新组织为'none'
        this.setFieldValByName("createOrg", LoginHelper.getDeptId(), metaObject);
        this.setFieldValByName("updateOrg", LoginHelper.getDeptId(), metaObject);
    }

    /**
     * update(Wrapper updateWrapper) 不会触发这个方法。
     * boolean update(T entity, Wrapper updateWrapper) 会。
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date().getTime(), metaObject);
        this.setFieldValByName("updateUser", LoginHelper.getUserId(), metaObject);
        this.setFieldValByName("updateOrg", LoginHelper.getDeptId(), metaObject);
        //在商家对账、销司对账、工单发生更新时，将“是否同步给销售端”的字段置成false，以便下次销售端同步时，能再次同步到数据
    }
}
