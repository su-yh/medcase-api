package com.medcase.mp.entity;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author suyh
 * @since 2026-08-22
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final Long SYSTEM_USER_ID = 0L;

    private static final String SYSTEM_USER_NAME = "system";

    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = SYSTEM_USER_ID;
        String userName = SYSTEM_USER_NAME;
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                userId = loginUser.getUserId() == null ? SYSTEM_USER_ID : loginUser.getUserId();
                if (loginUser.getUser() != null && loginUser.getUser().getNickName() != null) {
                    userName = loginUser.getUser().getNickName();
                }
            }
        }
        catch (Exception ignored) {
        }
        final Long auditUserId = userId;
        final String auditUserName = userName;
        strictInsertFill(metaObject, AbstractBaseEntity.Fields.createUserId, () -> auditUserId, Long.class);
        strictInsertFill(metaObject, AbstractBaseEntity.Fields.createBy, () -> auditUserName, String.class);
        strictInsertFill(metaObject, AbstractBaseEntity.Fields.createTime, Date::new, Date.class);
        strictInsertFill(metaObject, AbstractBaseEntity.Fields.updateUserId, () -> auditUserId, Long.class);
        strictInsertFill(metaObject, AbstractBaseEntity.Fields.updateBy, () -> auditUserName, String.class);
        strictInsertFill(metaObject, AbstractBaseEntity.Fields.updateTime, Date::new, Date.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId = SYSTEM_USER_ID;
        String userName = SYSTEM_USER_NAME;
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                userId = loginUser.getUserId() == null ? SYSTEM_USER_ID : loginUser.getUserId();
                if (loginUser.getUser() != null && loginUser.getUser().getNickName() != null) {
                    userName = loginUser.getUser().getNickName();
                }
            }
        }
        catch (Exception ignored) {
        }
        final Long auditUserId = userId;
        final String auditUserName = userName;
        strictUpdateFill(metaObject, AbstractBaseEntity.Fields.updateUserId, () -> auditUserId, Long.class);
        strictUpdateFill(metaObject, AbstractBaseEntity.Fields.updateBy, () -> auditUserName, String.class);
        strictUpdateFill(metaObject, AbstractBaseEntity.Fields.updateTime, Date::new, Date.class);
    }
}
