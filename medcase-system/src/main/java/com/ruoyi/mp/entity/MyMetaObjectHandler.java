package com.ruoyi.mp.entity;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author suyh
 * @since 2026-08-22
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject, AbstractBaseEntity.Fields.createTime, Date::new, Date.class);
        strictInsertFill(metaObject, AbstractBaseEntity.Fields.updateTime, Date::new, Date.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, AbstractBaseEntity.Fields.updateTime, Date::new, Date.class);
    }
}

