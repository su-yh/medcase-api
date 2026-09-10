package com.medcase.web.controller.system;

import java.util.List;
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
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
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
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody MenuSaveRequest menu) {

        if (!menuService.checkMenuNameUnique(menu)) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_NAME_EXISTS);
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
        else if (menu.getId().equals(menu.getParentId())) {
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
    @DeleteMapping("/{id}")
    public void remove(@PathVariable("id") Long id) {

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
