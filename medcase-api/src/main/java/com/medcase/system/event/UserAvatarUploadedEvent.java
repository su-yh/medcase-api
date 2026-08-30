package com.medcase.system.event;

import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.storage.pojo.FileAttachment;
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
