package com.medcase.framework.manager.factory;

import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.LogUtils;
import com.medcase.common.utils.ServletUtils;
import com.medcase.common.utils.http.UserAgentUtils;
import com.medcase.common.utils.ip.AddressUtils;
import com.medcase.common.utils.ip.IpUtils;
import com.medcase.common.utils.spring.SpringUtils;
import com.medcase.system.entity.LoginRecordEntity;
import com.medcase.system.service.LoginRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 * 
 */
public class AsyncFactory {

    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 记录登录信息
     * 
     * @param username 用户名
     * @param success 状态
     * @param message 消息
     * @param args 列表
     * @return 任务task
     */
    public static TimerTask recordLogin(final String username, final Long userId,
            final UserTypeEnums userType, final Boolean success,
            final String message, final Object... args) {

        final jakarta.servlet.http.HttpServletRequest request = ServletUtils.getRequest();
        final String userAgent = request == null
                ? "" : Objects.requireNonNullElse(request.getHeader("User-Agent"), "");
        final String ip = IpUtils.getIpAddr(request);
        return new TimerTask() {

            @Override
            public void run() {

                String address = AddressUtils.getRealAddressByIP(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(String.valueOf(success)));
                s.append(LogUtils.getBlock(message));
                // 打印信息到日志
                sys_user_logger.info(s.toString(), args);
                // 获取客户端操作系统
                String os = UserAgentUtils.getOperatingSystem(userAgent);
                // 获取客户端浏览器
                String browser = UserAgentUtils.getBrowser(userAgent);
                // 封装对象
                LoginRecordEntity loginRecord = new LoginRecordEntity();
                loginRecord.setUserId(userId);
                loginRecord.setUserType(userType);
                loginRecord.setUserName(username);
                loginRecord.setSuccess(success);
                loginRecord.setIpaddr(ip);
                loginRecord.setLoginLocation(address);
                loginRecord.setBrowser(browser);
                loginRecord.setOs(os);
                loginRecord.setMsg(message);
                loginRecord.setLoginTime(new java.util.Date());
                SpringUtils.getBean(LoginRecordService.class).insert(loginRecord);
            }
        };
    }

}
