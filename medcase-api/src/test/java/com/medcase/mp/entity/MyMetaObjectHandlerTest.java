package com.medcase.mp.entity;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.system.entity.SysRoleEntity;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyMetaObjectHandlerTest {

    @BeforeAll
    static void initTableInfo() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "test");
        assistant.setCurrentNamespace("test");
        TableInfoHelper.initTableInfo(assistant, SysRoleEntity.class);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void fillsCurrentUserIdAndNicknameWhenLoginUserExists() {
        SysUserEntity user = new SysUserEntity();
        user.setNickName("审核管理员");
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(8L);
        loginUser.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));

        SysRoleEntity entity = new SysRoleEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        new MyMetaObjectHandler().insertFill(metaObject);

        assertEquals(8L, metaObject.getValue("createUserId"));
        assertEquals("审核管理员", metaObject.getValue("createBy"));
        assertEquals(8L, metaObject.getValue("updateUserId"));
        assertEquals("审核管理员", metaObject.getValue("updateBy"));
    }

    @Test
    void fillsSystemAuditValuesWhenNoLoginUserExists() {
        SysRoleEntity entity = new SysRoleEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        new MyMetaObjectHandler().insertFill(metaObject);

        assertEquals(0L, metaObject.getValue("createUserId"));
        assertEquals("system", metaObject.getValue("createBy"));
        assertEquals(0L, metaObject.getValue("updateUserId"));
        assertEquals("system", metaObject.getValue("updateBy"));
    }

    @Test
    void fillsSystemUpdateValuesWhenNoLoginUserExists() {
        SysRoleEntity entity = new SysRoleEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        new MyMetaObjectHandler().updateFill(metaObject);

        assertEquals(0L, metaObject.getValue("updateUserId"));
        assertEquals("system", metaObject.getValue("updateBy"));
    }
}
