package com.ruoyi.system.event;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.storage.pojo.FileAttachment;
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
    private final FileAttachment attachment;

    public UserAvatarUploadedEvent(Object source, LoginUser loginUser, FileAttachment attachment) {
        super(source);
        this.loginUser = loginUser;
        this.attachment = attachment;
    }
}
