package com.medcase.framework.config;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonJsonRedisSerializerTest {

    @Test
    void serializeAndDeserializeLoginUser() throws Exception {

        JacksonJsonRedisSerializer<Object> serializer = new JacksonJsonRedisSerializer<>(Object.class);

        SysUser user = new SysUser();
        user.setUserName("doctor");
        user.setPassword("secret");
        LoginUser loginUser = new LoginUser(user, null);

        byte[] bytes = serializer.serialize(loginUser);
        Object value = serializer.deserialize(bytes);

        assertInstanceOf(LoginUser.class, value);
        assertEquals("doctor", ((LoginUser) value).getUsername());
    }
}
