package com.medcase.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Anonymous;
import com.medcase.common.core.controller.BaseController;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.core.domain.model.RegisterBody;
import com.medcase.framework.web.service.SysRegisterService;
import com.medcase.system.service.SysConfigService;

/**
 * 注册验证
 * 
 */
@RestController
public class SysRegisterController extends BaseController {

    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private SysConfigService configService;

    @Anonymous
    @PostMapping("/register")
    public void register(@RequestBody RegisterBody user) {

        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_REGISTER_DISABLED);
        }
        registerService.register(user);
    }
}
