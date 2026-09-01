package com.medcase.common.core.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.utils.DateUtils;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.common.utils.StringUtils;

/**
 * web层通用数据处理
 * 
 */
public class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {

        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {

                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {

        return StringUtils.format("redirect:{}", url);
    }

    /**
     * 获取用户缓存信息
     */
    public LoginUser getLoginUser() {

        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取登录用户id
     */
    public Long getUserId() {

        return getLoginUser().getUserId();
    }

    /**
     * 获取登录部门id
     */
    public Long getDeptId() {

        return getLoginUser().getDeptId();
    }

    /**
     * 获取登录用户名
     */
    public String getUsername() {

        return getLoginUser().getUsername();
    }
}
