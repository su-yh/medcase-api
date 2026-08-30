package com.medcase.web.controller.system;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.mvc.response.R;
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
    public R<Void> unlockScreen(@RequestBody Map<String, String> body) {

        String password = body.get("password");
        if (StringUtils.isEmpty(password)) {

            return R.ofFail("密码不能为空");
        }
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username, UserTypeEnums.ADMIN.getCode());
        if (user == null) {

            return R.ofFail("服务器超时，请重新登录");
        }
        if (!SecurityUtils.matchesPassword(password, user.getPassword())) {

            return R.ofFail("密码错误，请重新输入");
        }

        return R.ofSuccess(null, "解锁成功");
    }
}
