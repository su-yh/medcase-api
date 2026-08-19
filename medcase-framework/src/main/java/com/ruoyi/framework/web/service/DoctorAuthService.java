package com.ruoyi.framework.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.DoctorLoginResponse;
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
import com.ruoyi.system.mapper.DoctorUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 医生端注册登录校验方法
 *
 * @author suyh
 */
@Component
public class DoctorAuthService {
    @Autowired
    private DoctorUserMapper doctorUserMapper;

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

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

        SysUser user = new SysUser();
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

    public DoctorLoginResponse login(LoginBody loginBody) {
        String username = loginBody.getUsername();
        loginService.validateCaptcha(username, loginBody.getCode(), loginBody.getUuid());
        loginService.loginPreCheck(username, loginBody.getPassword());

        SysUser user = selectDoctorByUsername(username);
        if (StringUtils.isNull(user)) {
            throw new UserNotExistsException();
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            throw new UserNotExistsException();
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            throw new UserPasswordNotMatchException();
        } else if (!SecurityUtils.matchesPassword(loginBody.getPassword(), user.getPassword())) {
            throw new UserPasswordNotMatchException();
        }

        updateLoginInfo(user.getUserId());
        LoginUser loginUser = new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
        return new DoctorLoginResponse(tokenService.createToken(loginUser));
    }

    private boolean existsDoctorUsername(String username) {
        Long count = doctorUserMapper.selectCount(new QueryWrapper<SysUser>()
                .eq("user_name", username)
                .eq("user_type", UserTypeEnums.DOCTOR)
                .eq("del_flag", "0"));
        return count != null && count > 0;
    }

    private SysUser selectDoctorByUsername(String username) {
        return doctorUserMapper.selectOne(new QueryWrapper<SysUser>()
                .eq("user_name", username)
                .eq("user_type", UserTypeEnums.DOCTOR));
    }

    private void updateLoginInfo(Long userId) {
        doctorUserMapper.update(null, new UpdateWrapper<SysUser>()
                .eq("user_id", userId)
                .set("login_ip", IpUtils.getIpAddr())
                .set("login_date", DateUtils.getNowDate()));
    }
}
