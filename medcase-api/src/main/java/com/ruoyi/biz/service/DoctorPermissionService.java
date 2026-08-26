package com.ruoyi.biz.service;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatusEnums;
import com.ruoyi.common.enums.UserTypeEnums;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 医生端权限判断。
 *
 * @author suyh
 */
@Service("dp")
public class DoctorPermissionService {
    /**
     * 判断医生当前状态是否属于允许状态列表。
     *
     * @param doctorUser 当前登录医生
     * @param statuses 允许的用户状态
     * @return 医生用户且状态命中时返回 true
     */
    public boolean hasAnyStatus(LoginUser doctorUser, UserStatusEnums... statuses) {
        return doctorUser != null
                && doctorUser.getUser() != null
                && UserTypeEnums.DOCTOR == doctorUser.getUser().getUserType()
                && statuses != null
                && Arrays.stream(statuses)
                .anyMatch(status -> status != null
                        && status.getCode().equals(doctorUser.getUser().getStatus()));
    }
}
