package com.medcase.web.controller.system.dto;

import com.medcase.common.constant.UserConstants;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 菜单保存请求。
 */
@Data
public class MenuSaveRequest {

    private Long id;

    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    @Size(max = 200, message = "路由地址不能超过200个字符")
    private String routePath;

    @Size(max = 255, message = "组件路径不能超过255个字符")
    @Pattern(regexp = "^(?!/)(?!.*\\.vue$).*$", message = "组件路径不能以/开头且不能以.vue结尾")
    private String vueComponentPath;

    @Size(max = 50, message = "路由名称长度不能超过50个字符")
    private String routeName;

    private String isCache;

    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    private Boolean visible;

    private String status;

    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String perms;

    private String icon;

    private String remark;

    @AssertTrue(message = "菜单路由名称不能为空")
    public boolean isRouteNameValid() {

        return !UserConstants.TYPE_MENU.equals(menuType) || StringUtils.hasText(routeName);
    }
}
