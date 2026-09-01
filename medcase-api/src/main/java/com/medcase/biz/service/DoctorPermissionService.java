package com.medcase.biz.service;

import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 病例端用户权限判断。
 *
 * @author suyh
 */
@Service("dp")
public class DoctorPermissionService {
    /**
     * 判断病例端用户当前状态是否属于允许状态列表。
     *
     * @param doctorUser 当前登录用户
     * @param statuses   允许的用户状态
     * @return 医生用户且状态命中时返回 true
     */
    public boolean hasAnyStatus(@NonNull LoginUser doctorUser, UserStatusEnums... statuses) {
        return Arrays.stream(statuses).anyMatch(status -> status != null && status.getCode().equals(doctorUser.getUser().getStatus()));
    }
}
