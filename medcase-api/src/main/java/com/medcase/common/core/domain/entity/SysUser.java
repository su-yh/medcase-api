package com.medcase.common.core.domain.entity;

import java.util.Date;
import java.util.List;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medcase.common.core.domain.BaseEntity;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.common.xss.Xss;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

/**
 * 用户对象 sys_user
 */
@Data
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 用户账号 */
    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    private String userName;

    /** 用户昵称 */
    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickName;

    /** 用户类型 */
    private UserTypeEnums userType;

    /** 用户邮箱 */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /** 手机号码 */
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    private String phonenumber;

    /** 用户性别 */
    private String sex;

    /** 用户头像 */
    private FileAttachment avatar;

    /** 密码 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 账号状态（0正常 1停用） */
    private String status;

    /** 删除标志（false代表存在 true代表删除） */
    private Boolean delFlag;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    /** 密码最后更新时间 */
    private Date pwdUpdateDate;

    /** 部门对象 */
    private SysDept dept;

    /** 角色对象 */
    private List<SysRole> roles;

    /** 角色组 */
    private Long[] roleIds;

    /** 岗位组 */
    private Long[] postIds;

    /** 角色ID */
    private Long roleId;

    public SysUser() {

    }

    public SysUser(Long userId) {

        this.userId = userId;
    }

    public boolean isAdmin() {

        return SecurityUtils.isAdmin(this.userId);
    }
}
