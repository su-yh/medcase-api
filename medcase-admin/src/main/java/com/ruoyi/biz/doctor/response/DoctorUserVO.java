package com.ruoyi.biz.doctor.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.entity.SysUser;
import java.util.Date;
import lombok.Data;

/**
 * 医生管理返回对象
 *
 * @author suyh
 */
@Data
public class DoctorUserVO {
    private Long id;

    private String name;

    private String username;

    private String phone;

    private String status;

    private String department;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public static DoctorUserVO fromUser(SysUser user) {
        DoctorUserVO result = new DoctorUserVO();
        result.setId(user.getUserId());
        result.setName(user.getNickName());
        result.setUsername(user.getUserName());
        result.setPhone(user.getPhonenumber());
        result.setStatus(user.getStatus());
        result.setDepartment(user.getDept() == null ? null : user.getDept().getDeptName());
        result.setCreateTime(user.getCreateTime());
        return result;
    }
}
