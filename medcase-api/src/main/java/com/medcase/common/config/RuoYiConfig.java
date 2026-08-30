package com.medcase.common.config;

import com.medcase.common.utils.file.FileUtils;
import com.medcase.common.utils.file.FileUploadUtils;
import com.medcase.common.utils.file.ImageUtils;
import com.medcase.common.utils.ip.AddressUtils;
import com.medcase.common.utils.poi.ExcelUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 */
@Component
@ConfigurationProperties(prefix = "ruoyi")
public class RuoYiConfig {

    /** 项目名称 */
    private String name;

    /** 版权年份 */
    private String copyrightYear;

    /** 上传路径 */
    private String profile;

    /** 获取地址开关 */
    private boolean addressEnabled;

    /** 验证码类型 */
    private String captchaType;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getCopyrightYear() {

        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear) {

        this.copyrightYear = copyrightYear;
    }

    public String getProfile() {

        return profile;
    }

    public void setProfile(String profile) {

        this.profile = profile;
        FileUploadUtils.setDefaultBaseDir(profile);
        FileUtils.setImportPath(profile + "/import");
        ImageUtils.setLocalPath(profile);
        ExcelUtil.setDownloadPath(profile + "/download/");
    }

    public boolean isAddressEnabled() {

        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled) {

        this.addressEnabled = addressEnabled;
        AddressUtils.setAddressEnabled(addressEnabled);
    }

    public String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        this.captchaType = captchaType;
    }

    /**
     * 获取导入上传路径
     */
    public String getImportPath() {

        return getProfile() + "/import";
    }

    /**
     * 获取头像上传路径
     */
    public String getAvatarPath() {

        return getProfile() + "/avatar";
    }

}
