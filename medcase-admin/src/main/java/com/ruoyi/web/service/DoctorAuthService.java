package com.ruoyi.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserNotExistsException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.DoctorUserEntity;
import com.ruoyi.system.mapper.DoctorUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 医生端注册登录校验方法
 *
 * @author suyh
 */
@Component
@RequiredArgsConstructor
public class DoctorAuthService {
    private final DoctorUserMapper doctorUserMapper;

    private final SysLoginService loginService;

    private final SysPermissionService permissionService;

    private final TokenService tokenService;

    public void register(RegisterBody registerBody) {
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        loginService.validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());

        if (StringUtils.isEmpty(username)) {
            throw new ServiceException("用户名不能为空");
        } else if (StringUtils.isEmpty(password)) {
            throw new ServiceException("用户密码不能为空");
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        } else if (existsDoctorUsername(username)) {
            throw new ServiceException("保存用户'" + username + "'失败，注册账号已存在");
        }

        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserName(username);
        user.setNickName(username);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatus.OK.getCode());
        user.setDelFlag("0");
        user.setPwdUpdateDate(DateUtils.getNowDate());
        user.setPassword(SecurityUtils.encryptPassword(password));
        if (doctorUserMapper.insert(user) <= 0) {
            throw new ServiceException("注册失败,请联系系统管理人员");
        }
    }

    public String login(LoginBody loginBody) {
        String username = loginBody.getUsername();
        loginService.validateCaptcha(username, loginBody.getCode(), loginBody.getUuid());
        loginService.loginPreCheck(username, loginBody.getPassword());

        DoctorUserEntity doctorUser = selectDoctorByUsername(username);
        if (StringUtils.isNull(doctorUser)) {
            throw new UserNotExistsException();
        } else if (UserStatus.DISABLE.getCode().equals(doctorUser.getStatus())) {
            throw new UserPasswordNotMatchException();
        } else if (!SecurityUtils.matchesPassword(loginBody.getPassword(), doctorUser.getPassword())) {
            throw new UserPasswordNotMatchException();
        }

        updateLoginInfo(doctorUser.getUserId());
        SysUser sysUser = toSysUser(doctorUser);
        LoginUser loginUser = new LoginUser(sysUser.getUserId(), sysUser.getDeptId(), sysUser,
                permissionService.getMenuPermission(sysUser));
        return tokenService.createToken(loginUser);
    }

    private boolean existsDoctorUsername(String username) {
        Long count = doctorUserMapper.selectCount(new QueryWrapper<DoctorUserEntity>()
                .eq("user_name", username)
                .eq("user_type", UserTypeEnums.DOCTOR)
                .eq("del_flag", "0"));
        return count != null && count > 0;
    }

    private DoctorUserEntity selectDoctorByUsername(String username) {
        return doctorUserMapper.selectOne(new QueryWrapper<DoctorUserEntity>()
                .eq("user_name", username)
                .eq("user_type", UserTypeEnums.DOCTOR)
                .eq("del_flag", "0"));
    }

    private void updateLoginInfo(Long userId) {
        doctorUserMapper.update(null, new UpdateWrapper<DoctorUserEntity>()
                .eq("user_id", userId)
                .set("login_ip", IpUtils.getIpAddr())
                .set("login_date", DateUtils.getNowDate()));
    }

    private SysUser toSysUser(DoctorUserEntity doctorUser) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(doctorUser.getUserId());
        sysUser.setDeptId(doctorUser.getDeptId());
        sysUser.setUserName(doctorUser.getUserName());
        sysUser.setNickName(doctorUser.getNickName());
        sysUser.setUserType(doctorUser.getUserType());
        sysUser.setPassword(doctorUser.getPassword());
        sysUser.setStatus(doctorUser.getStatus());
        sysUser.setDelFlag(doctorUser.getDelFlag());
        sysUser.setLoginIp(doctorUser.getLoginIp());
        sysUser.setLoginDate(doctorUser.getLoginDate());
        sysUser.setPwdUpdateDate(doctorUser.getPwdUpdateDate());
        return sysUser;
    }
}
