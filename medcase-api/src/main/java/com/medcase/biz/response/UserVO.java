package com.medcase.biz.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medcase.biz.domain.UserEntity;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

import java.util.Date;

/**
 * 用户管理返回对象
 *
 * @author suyh
 */
@Data
public class UserVO {
    private Long id;

    private String nickName;

    private String sex;

    private String idCardNumber;

    private String title;

    private FileAttachment idCardFront;

    private FileAttachment idCardBack;

    private FileAttachment qualificationCertificate;

    private String username;

    private String phone;

    private String status;

    private String reviewReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public static UserVO fromEntity(UserEntity user) {
        UserVO result = new UserVO();
        result.setId(user.getUserId());
        result.setNickName(user.getNickName());
        result.setSex(user.getSex());
        result.setIdCardNumber(user.getIdCardNumber());
        result.setTitle(user.getTitle());
        result.setIdCardFront(user.getIdCardFront());
        result.setIdCardBack(user.getIdCardBack());
        result.setQualificationCertificate(user.getQualificationCertificate());
        result.setUsername(user.getUserName());
        result.setPhone(user.getPhonenumber());
        if (user.getStatus() != null) {
            result.setStatus(user.getStatus().getCode());
        }
        result.setReviewReason(user.getReviewReason());
        result.setCreateTime(user.getCreateTime());
        return result;
    }
}
