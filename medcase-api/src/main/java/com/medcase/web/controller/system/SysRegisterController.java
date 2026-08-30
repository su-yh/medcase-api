package com.medcase.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Anonymous;
import com.medcase.common.core.controller.BaseController;
import com.medcase.mvc.response.R;
import com.medcase.common.core.domain.model.RegisterBody;
import com.medcase.common.utils.StringUtils;
import com.medcase.framework.web.service.SysRegisterService;
import com.medcase.system.service.ISysConfigService;

/**
 * 注册验证
 * 
 */
@RestController
public class SysRegisterController extends BaseController {

    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @Anonymous
    @PostMapping("/register")
    public R<Void> register(@RequestBody RegisterBody user) {

        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {

            return R.ofFail("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? R.ofSuccess() : R.ofFail(msg);
    }
}
