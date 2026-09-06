package com.medcase.web.controller.system;

import java.util.List;
import java.util.Map;
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
import com.medcase.common.annotation.Log;
import com.medcase.common.constant.Constants;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.core.domain.TreeSelect;
import com.medcase.common.enums.BusinessType;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.system.entity.SysMenuEntity;
import com.medcase.system.service.SysMenuService;
import com.medcase.web.controller.system.dto.MenuQueryRequest;
import com.medcase.web.controller.system.dto.MenuSaveRequest;
import com.medcase.web.controller.system.dto.MenuSortRequest;

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

        List<SysMenuEntity> menus = menuService.selectMenuList(menu, loginUser.getUserId());
        return menus;
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public SysMenuEntity getInfo(@PathVariable Long menuId) {

        return menuService.selectMenuById(menuId);
    }

    /**
     * 获取菜单下拉树列表
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN)")
    @GetMapping("/treeselect")
    public List<TreeSelect> treeselect(
            MenuQueryRequest menu,
            @CurrLoginUser LoginUser loginUser) {

        List<SysMenuEntity> menus = menuService.selectMenuList(menu, loginUser.getUserId());
        return menuService.buildMenuTreeSelect(menus);
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody MenuSaveRequest menu) {

        if (!menuService.checkMenuNameUnique(menu)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_NAME_EXISTS);
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !org.apache.commons.lang3.Strings.CS.startsWithAny(
                menu.getPath(), Constants.HTTP, Constants.HTTPS)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_FRAME_URL_INVALID);
        }
        else if (!menuService.checkRouteConfigUnique(menu)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_ROUTE_EXISTS);
        }
        if (menuService.insertMenu(menu) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_OPERATION_FAILED);
        }
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody MenuSaveRequest menu) {

        if (!menuService.checkMenuNameUnique(menu)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_NAME_EXISTS);
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !org.apache.commons.lang3.Strings.CS.startsWithAny(
                menu.getPath(), Constants.HTTP, Constants.HTTPS)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_FRAME_URL_INVALID);
        }
        else if (menu.getMenuId().equals(menu.getParentId())) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_PARENT_SELF);
        }
        else if (!menuService.checkRouteConfigUnique(menu)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_ROUTE_EXISTS);
        }
        if (menuService.updateMenu(menu) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_OPERATION_FAILED);
        }
    }

    /**
     * 保存菜单排序
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "保存菜单排序", businessType = BusinessType.UPDATE)
    @PutMapping("/updateSort")
    public void updateSort(@Validated @RequestBody MenuSortRequest request) {

        String[] menuIds = request.getMenuIds().split(",");
        String[] orderNums = request.getOrderNums().split(",");
        menuService.updateMenuSort(menuIds, orderNums);
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public void remove(@PathVariable("menuId") Long menuId) {

        if (menuService.hasChildByMenuId(menuId)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_HAS_CHILDREN);
        }
        if (menuService.checkMenuExistRole(menuId)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_ASSIGNED);
        }
        if (menuService.deleteMenuById(menuId) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_OPERATION_FAILED);
        }
    }
}
