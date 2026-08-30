package com.ruoyi.web.controller.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码响应数据。
 */
@Getter
@AllArgsConstructor
public class CaptchaResponse {

    private boolean captchaEnabled;
    private String uuid;
    private String img;
}
