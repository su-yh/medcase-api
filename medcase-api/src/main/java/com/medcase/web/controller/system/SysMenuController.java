package com.medcase.web.controller.system;

import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.audit.AuditOperation;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.entity.SysMenuEntity;
import com.medcase.system.service.SysMenuService;
import com.medcase.web.controller.system.dto.MenuQueryRequest;
import com.medcase.web.controller.system.dto.MenuSaveRequest;
import com.medcase.web.controller.system.dto.MenuSortRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单信息
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public List<SysMenuEntity> list(
            MenuQueryRequest menu,
            @CurrLoginUser LoginUser loginUser) {
        return menuService.selectMenuList(menu, loginUser.getUserId());
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{id}")
    public SysMenuEntity getInfo(@PathVariable Long id) {
        return menuService.selectMenuById(id);
    }

    /**
     * 获取菜单下拉树列表
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN)")
    @GetMapping("/treeselect")
    public List<SysMenuEntity> treeselect(
            MenuQueryRequest menu,
            @CurrLoginUser LoginUser loginUser) {
        return menuService.selectMenuList(menu, loginUser.getUserId());
    }

    /**
     * 新增菜单
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).CREATE_MENU, " +
            "#spelReturnValue, #servletRequest, #loginUser, #menu)")
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @PostMapping
    public void add(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @Validated @RequestBody MenuSaveRequest menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_NAME_EXISTS);
        } else if (!menuService.checkRouteConfigUnique(menu)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_ROUTE_EXISTS);
        }
        if (menuService.insertMenu(menu) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_OPERATION_FAILED);
        }
    }

    /**
     * 修改菜单
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).UPDATE_MENU, " +
            "#spelReturnValue, #servletRequest, #loginUser, #menu)")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping
    public void edit(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @Validated @RequestBody MenuSaveRequest menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_NAME_EXISTS);
        }
        if (menu.getId().equals(menu.getParentId())) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_PARENT_SELF);
        }
        if (!menuService.checkRouteConfigUnique(menu)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_ROUTE_EXISTS);
        }
        if (menuService.updateMenu(menu) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_OPERATION_FAILED);
        }
    }

    /**
     * 保存菜单排序
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).UPDATE_MENU_SORT, " +
            "#spelReturnValue, #servletRequest, #loginUser, #request)")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping("/updateSort")
    public void updateSort(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @Validated @RequestBody MenuSortRequest request) {
        String[] menuIds = request.getMenuIds().split(",");
        String[] orderNums = request.getOrderNums().split(",");
        menuService.updateMenuSort(menuIds, orderNums);
    }

    /**
     * 删除菜单
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).DELETE_MENU, " +
            "#spelReturnValue, #servletRequest, #loginUser, #id)")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @DeleteMapping("/{id}")
    public void remove(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @PathVariable("id") Long id) {
        if (menuService.hasChildByMenuId(id)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_HAS_CHILDREN);
        }
        if (menuService.checkMenuExistRole(id)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_ASSIGNED);
        }
        if (menuService.deleteMenuById(id) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_OPERATION_FAILED);
        }
    }
}
