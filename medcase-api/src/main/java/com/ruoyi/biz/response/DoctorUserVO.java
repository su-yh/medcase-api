package com.ruoyi.biz.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.biz.domain.DoctorUserEntity;
import lombok.Data;

import java.util.Date;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public static DoctorUserVO fromEntity(DoctorUserEntity user) {
        DoctorUserVO result = new DoctorUserVO();
        result.setId(user.getUserId());
        result.setName(user.getNickName());
        result.setUsername(user.getUserName());
        result.setPhone(user.getPhonenumber());
        if (user.getStatus() != null) {
            result.setStatus(user.getStatus().getCode());
        }
        result.setCreateTime(user.getCreateTime());
        return result;
    }
}
