package com.medcase.web.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.constant.CacheConstants;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.core.redis.RedisCache;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.audit.AuditOperation;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.system.domain.SysUserOnline;
import com.medcase.system.service.SysUserOnlineService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 在线用户监控
 * 
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController {

    @Autowired
    private SysUserOnlineService userOnlineService;

    @Autowired
    private RedisCache redisCache;

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public PageResult<SysUserOnline> list(PageParam pageParam, String ipaddr, String userName) {

        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys) {

            LoginUser user = redisCache.getCacheObject(key);
            if (org.springframework.util.StringUtils.hasText(ipaddr)
                    && org.springframework.util.StringUtils.hasText(userName)) {

                userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
            }
            else if (org.springframework.util.StringUtils.hasText(ipaddr)) {

                userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
            }
            else if (org.springframework.util.StringUtils.hasText(userName)
                    && user.getUser() != null) {

                userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
            }
            else {

                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return new PageResult<>(PageParam.doPageList(pageParam, userOnlineList), (long) userOnlineList.size());
    }

    /**
     * 强退用户
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).FORCE_LOGOUT_ONLINE_USER, " +
            "#spelReturnValue, #servletRequest, #loginUser, #tokenId)")
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @DeleteMapping("/{tokenId}")
    public void forceLogout(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @PathVariable String tokenId) {
        redisCache.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId);
    }
}
