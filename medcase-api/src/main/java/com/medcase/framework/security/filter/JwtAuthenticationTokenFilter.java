package com.medcase.framework.security.filter;

import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.framework.web.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * token过滤器 验证token有效性
 * 
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    public static final String LOGIN_USER_ID = "user-id";
    public static final String LOGIN_USER_NICK_NAME = "user-nick-name";
    public static final String LOGIN_USER_TYPE = "user-type";

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null && SecurityUtils.getAuthentication() == null) {

            tokenService.verifyToken(loginUser);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            MDC.put(LOGIN_USER_ID, loginUser.getUserId() + "");
            MDC.put(LOGIN_USER_NICK_NAME, loginUser.getUser().getNickName());
            MDC.put(LOGIN_USER_TYPE, loginUser.getUser().getUserType().getDesc());
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(LOGIN_USER_TYPE);
            MDC.remove(LOGIN_USER_NICK_NAME);
            MDC.remove(LOGIN_USER_ID);
        }
    }
}
