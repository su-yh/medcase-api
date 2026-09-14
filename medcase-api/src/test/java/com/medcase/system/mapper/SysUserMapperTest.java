package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SysUserMapperTest {
    @Test
    void isARegisteredBaseMapper() {
        assertTrue(BaseMapperX.class.isAssignableFrom(SysUserMapper.class));
        assertNotNull(SysUserMapper.class.getAnnotation(Mapper.class));
    }

    @Test
    void exposesPhoneDuplicateQueryForDoctor() throws NoSuchMethodException {
        Method method = SysUserMapper.class.getMethod(
                "phoneExists", String.class, com.medcase.common.enums.UserTypeEnums.class);

        assertEquals(boolean.class, method.getReturnType());
    }

    @Test
    void exposesUserLookupById() throws NoSuchMethodException {
        Method method = SysUserMapper.class.getMethod("selectUserById", Long.class);

        assertEquals(SysUserEntity.class, method.getReturnType());
    }

    @Test
    void usesSysUserEntityAsMapperModel() {
        ParameterizedType mapperType = (ParameterizedType) SysUserMapper.class
                .getGenericInterfaces()[0];
        assertEquals(SysUserEntity.class, mapperType.getActualTypeArguments()[0]);
    }

}
