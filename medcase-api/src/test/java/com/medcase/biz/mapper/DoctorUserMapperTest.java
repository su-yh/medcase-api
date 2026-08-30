package com.medcase.biz.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoctorUserMapperTest {
    @Test
    void isARegisteredBaseMapper() {
        assertTrue(BaseMapperX.class.isAssignableFrom(DoctorUserMapper.class));
        assertNotNull(DoctorUserMapper.class.getAnnotation(Mapper.class));
    }

    @Test
    void pageQueryRequiresNonNullQueryObject() throws NoSuchMethodException {
        Method method = DoctorUserMapper.class.getMethod(
                "selectDoctorPage",
                com.medcase.mp.mybatis.PageParam.class,
                com.medcase.biz.request.DoctorUserQuery.class);

        assertNotNull(method.getParameters()[1].getAnnotatedType()
                .getAnnotation(NonNull.class));
    }

    @Test
    void exposesPhoneDuplicateQueryForDoctor() throws NoSuchMethodException {
        Method method = DoctorUserMapper.class.getMethod(
                "phoneExists", String.class);

        assertEquals(boolean.class, method.getReturnType());
    }

}
