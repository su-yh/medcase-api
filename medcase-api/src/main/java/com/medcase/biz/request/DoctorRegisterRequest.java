package com.medcase.biz.request;

import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

/**
 * 医生端注册请求
 *
 * @author suyh
 * @since 2026-08-20
 */
@Data
public class DoctorRegisterRequest {
    private String username;

    private String password;

    private String phone;

    private String smsCode;

    private String nickName;

    private String sex;

    private String idCardNumber;

    private String title;

    private String inviteCode;

    private FileAttachment idCardFront;

    private FileAttachment idCardBack;

    private FileAttachment qualificationCertificate;
}
