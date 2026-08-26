package com.ruoyi.common.core.domain.model;

import com.ruoyi.common.core.domain.entity.SysUser;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;

class LoginUserJsonSerializationTest
{
    @Test
    void jacksonSerializationShouldIgnoreSecurityAndPasswordFields() throws Exception
    {
        SysUser user = new SysUser();
        user.setUserName("doctor");
        user.setPassword("secret");
        LoginUser loginUser = new LoginUser(user, null);

        String json = new ObjectMapper().writeValueAsString(loginUser);

        assertFalse(json.contains("secret"));
        assertFalse(json.contains("password"));
        assertFalse(json.contains("accountNonExpired"));
        assertFalse(json.contains("accountNonLocked"));
        assertFalse(json.contains("credentialsNonExpired"));
        assertFalse(json.contains("enabled"));
        assertFalse(json.contains("authorities"));
    }
}
