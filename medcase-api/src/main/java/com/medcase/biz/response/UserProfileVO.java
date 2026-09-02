package com.medcase.biz.response;

import com.medcase.biz.domain.UserEntity;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

/**
 * 病例端当前登录用户资料
 *
 * @author suyh
 */
@Data
public class UserProfileVO {
    private Long id;

    private String nickName;

    private String sex;

    private String phone;

    private UserTypeEnums userType;

    private String idCardNumber;

    private String title;

    private FileAttachment idCardFront;

    private FileAttachment idCardBack;

    private FileAttachment qualificationCertificate;

    private UserStatusEnums status;

    private String reviewReason;

    public static UserProfileVO fromEntity(UserEntity user) {
        UserProfileVO result = new UserProfileVO();
        result.setId(user.getUserId());
        result.setNickName(user.getNickName());
        result.setSex(user.getSex());
        result.setPhone(user.getPhonenumber());
        result.setUserType(user.getUserType());
        result.setIdCardNumber(user.getIdCardNumber());
        result.setTitle(user.getTitle());
        result.setIdCardFront(user.getIdCardFront());
        result.setIdCardBack(user.getIdCardBack());
        result.setQualificationCertificate(user.getQualificationCertificate());
        result.setStatus(user.getStatus());
        result.setReviewReason(user.getReviewReason());
        return result;
    }
}
