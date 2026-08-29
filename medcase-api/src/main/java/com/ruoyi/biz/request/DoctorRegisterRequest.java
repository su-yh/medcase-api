package com.ruoyi.biz.request;

import com.ruoyi.storage.pojo.FileAttachment;
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

    private String nickName;

    private String idCardNumber;

    private String title;

    private String inviteCode;

    private FileAttachment idCardFront;

    private FileAttachment idCardBack;

    private FileAttachment qualificationCertificate;
}
