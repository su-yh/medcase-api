package com.medcase.biz.response;

import com.medcase.biz.domain.DoctorUserEntity;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

/**
 * 医生端当前登录用户资料
 *
 * @author suyh
 */
@Data
public class DoctorProfileVO {
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

    public static DoctorProfileVO fromEntity(DoctorUserEntity doctor) {
        DoctorProfileVO result = new DoctorProfileVO();
        result.setId(doctor.getUserId());
        result.setNickName(doctor.getNickName());
        result.setSex(doctor.getSex());
        result.setPhone(doctor.getPhonenumber());
        result.setUserType(doctor.getUserType());
        result.setIdCardNumber(doctor.getIdCardNumber());
        result.setTitle(doctor.getTitle());
        result.setIdCardFront(doctor.getIdCardFront());
        result.setIdCardBack(doctor.getIdCardBack());
        result.setQualificationCertificate(doctor.getQualificationCertificate());
        result.setStatus(doctor.getStatus());
        result.setReviewReason(doctor.getReviewReason());
        return result;
    }
}
