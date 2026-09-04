package com.medcase.system.event;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.framework.web.service.TokenService;
import com.medcase.storage.pojo.FileAttachment;
import com.medcase.system.mapper.SysUserMapper;
import com.medcase.system.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

class UserAvatarUploadedEventListenerTest {
    @Test
    void databaseListenerUpdatesAvatarByEventData() {
        SysUserMapper userMapper = mock(SysUserMapper.class);
        SysUserService userService = new SysUserService();
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
        UserAvatarUploadedEvent event = event();
        when(userMapper.updateUserAvatar(12L, event.getAttachment())).thenReturn(1);

        userService.handleUserAvatarUploaded(event);

        verify(userMapper).updateUserAvatar(12L, event.getAttachment());
    }

    @Test
    void tokenListenerUpdatesLoginUserAndRefreshesCache() {
        TokenService tokenService = Mockito.spy(new TokenService());
        UserAvatarUploadedEvent event = event();
        doNothing().when(tokenService).setLoginUser(event.getLoginUser());

        tokenService.handleUserAvatarUploaded(event);

        assertEquals(event.getAttachment(), event.getLoginUser().getUser().getAvatar());
        verify(tokenService).setLoginUser(event.getLoginUser());
    }

    private UserAvatarUploadedEvent event() {
        SysUser user = new SysUser();
        user.setUserId(12L);
        LoginUser loginUser = new LoginUser(user, Set.of());
        loginUser.setUserId(12L);
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("avatar/20260827/avatar.png");
        attachment.setOriginalFilename("avatar.png");
        return new UserAvatarUploadedEvent(
                this, loginUser, attachment);
    }
}
