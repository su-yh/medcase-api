package com.medcase.framework.security.handle;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.medcase.common.constant.Constants;
import com.medcase.mvc.response.R;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.utils.MessageUtils;
import com.medcase.common.utils.ServletUtils;
import com.medcase.common.utils.json.JsonUtils;
import com.medcase.framework.manager.AsyncManager;
import com.medcase.framework.manager.factory.AsyncFactory;
import com.medcase.framework.web.service.TokenService;

/**
 * 自定义退出处理类 返回成功
 * 
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     * 
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null) {

            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, MessageUtils.message("user.logout.success")));
        }
        ServletUtils.renderString(response, JsonUtils.toJSONString(
                R.ofSuccess(null, MessageUtils.message("user.logout.success"))));
    }
}
