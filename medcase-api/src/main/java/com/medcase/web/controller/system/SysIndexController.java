package com.medcase.web.controller.system;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.common.utils.StringUtils;
import com.medcase.system.service.ISysUserService;

/**
 * 首页
 *
 */
@RestController
public class SysIndexController {

    @Autowired
    private ISysUserService userService;

    /**
     * 解锁屏幕
     */
    @PostMapping("/unlockscreen")
    public void unlockScreen(@RequestBody Map<String, String> body) {

        String password = body.get("password");
        if (StringUtils.isEmpty(password)) {
            throw ExceptionUtil.business(ErrorCodeEnums.SCREEN_UNLOCK_PASSWORD_EMPTY);
        }
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username, UserTypeEnums.ADMIN.getCode());
        if (user == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.SCREEN_UNLOCK_USER_NOT_FOUND);
        }
        if (!SecurityUtils.matchesPassword(password, user.getPassword())) {
            throw ExceptionUtil.business(ErrorCodeEnums.SCREEN_UNLOCK_PASSWORD_INVALID);
        }

    }
}
