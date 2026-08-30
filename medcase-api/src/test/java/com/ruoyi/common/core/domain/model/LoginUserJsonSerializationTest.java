package com.ruoyi.common.core.domain.model;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.storage.pojo.FileAttachment;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginUserJsonSerializationTest {

    @Test
    void jacksonSerializationShouldIgnoreSecurityAndPasswordFields() throws Exception {

        SysUser user = new SysUser();
        user.setUserName("doctor");
        user.setPassword("secret");
        FileAttachment avatar = new FileAttachment();
        avatar.setFilePath("avatar/20260828/user.png");
        avatar.setOriginalFilename("user.png");
        user.setAvatar(avatar);
        LoginUser loginUser = new LoginUser(user, null);

        String json = new ObjectMapper().writeValueAsString(loginUser);

        assertFalse(json.contains("secret"));
        assertFalse(json.contains("password"));
        assertFalse(json.contains("accountNonExpired"));
        assertFalse(json.contains("accountNonLocked"));
        assertFalse(json.contains("credentialsNonExpired"));
        assertFalse(json.contains("enabled"));
        assertFalse(json.contains("authorities"));
        JsonNode root = new ObjectMapper().readTree(json);
        assertTrue(root.path("user").path("avatar").isObject());
        assertEquals("avatar/20260828/user.png",
                root.path("user").path("avatar").path("filePath").asText());
    }
}
