package com.ruoyi.biz.response;

import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.common.enums.UserStatusEnums;
import lombok.Data;

/**
 * 医生端当前登录用户资料
 *
 * @author suyh
 */
@Data
public class DoctorProfileVO {
    private Long id;

    private String name;

    private String phone;

    private UserStatusEnums status;

    public static DoctorProfileVO fromEntity(DoctorUserEntity doctor) {
        DoctorProfileVO result = new DoctorProfileVO();
        result.setId(doctor.getUserId());
        result.setName(doctor.getNickName());
        result.setPhone(doctor.getPhonenumber());
        result.setStatus(doctor.getStatus());
        return result;
    }
}
