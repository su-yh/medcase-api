package com.ruoyi.system.event;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.impl.SysUserServiceImpl;
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
        SysUserServiceImpl userService = new SysUserServiceImpl();
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
        UserAvatarUploadedEvent event = event();
        when(userMapper.updateUserAvatar(12L, "avatar/20260827/avatar.png")).thenReturn(1);

        userService.handleUserAvatarUploaded(event);

        verify(userMapper).updateUserAvatar(12L, "avatar/20260827/avatar.png");
    }

    @Test
    void tokenListenerUpdatesLoginUserAndRefreshesCache() {
        TokenService tokenService = Mockito.spy(new TokenService());
        UserAvatarUploadedEvent event = event();
        doNothing().when(tokenService).setLoginUser(event.getLoginUser());

        tokenService.handleUserAvatarUploaded(event);

        assertEquals("avatar/20260827/avatar.png", event.getLoginUser().getUser().getAvatar());
        verify(tokenService).setLoginUser(event.getLoginUser());
    }

    private UserAvatarUploadedEvent event() {
        SysUser user = new SysUser();
        user.setUserId(12L);
        LoginUser loginUser = new LoginUser(user, Set.of());
        loginUser.setUserId(12L);
        return new UserAvatarUploadedEvent(
                this, loginUser, "avatar/20260827/avatar.png");
    }
}
