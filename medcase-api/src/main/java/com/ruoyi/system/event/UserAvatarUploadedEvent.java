package com.ruoyi.system.event;

import com.ruoyi.common.core.domain.model.LoginUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 用户头像上传完成事件。
 *
 * @author suyh
 */
@Getter
public class UserAvatarUploadedEvent extends ApplicationEvent {
    private final LoginUser loginUser;
    private final String filePath;

    public UserAvatarUploadedEvent(Object source, LoginUser loginUser, String filePath) {
        super(source);
        this.loginUser = loginUser;
        this.filePath = filePath;
    }
}
