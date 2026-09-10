package com.medcase.system.service;

import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.text.Convert;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.entity.SysMenuEntity;
import com.medcase.system.mapper.SysMenuMapper;
import com.medcase.system.mapper.SysRoleMenuMapper;
import com.medcase.web.controller.system.dto.MenuQueryRequest;
import com.medcase.web.controller.system.dto.MenuSaveRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 菜单 业务层处理
 */
@Service
@Slf4j
public class SysMenuService {
    public static final Long MENU_ROOT_ID = 0L;

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 根据用户查询系统菜单列表
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenuEntity> selectMenuList(Long userId) {
        return selectMenuList(new MenuQueryRequest(), userId);
    }

    /**
     * 查询系统菜单列表
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenuEntity> selectMenuList(MenuQueryRequest menu, Long userId) {
        List<SysMenuEntity> menuList;
        // 管理员显示所有菜单信息
        if (SecurityUtils.isAdmin(userId)) {
            menuList = menuMapper.selectMenuList(menu);
        }
        else {
            menuList = menuMapper.selectMenuListByUserId(menu, userId);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (org.springframework.util.StringUtils.hasText(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据角色ID查询权限
     * @param roleId 角色ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByRoleId(Long roleId) {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (org.springframework.util.StringUtils.hasText(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     * @param userId 用户名称
     * @return 菜单列表
     */
    public List<SysMenuEntity> selectMenuTreeByUserId(Long userId) {
        return SecurityUtils.isAdmin(userId)
                ? menuMapper.selectMenuTreeAll()
                : menuMapper.selectMenuTreeByUserId(userId);
    }

    /**
     * 根据菜单ID查询信息
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    public SysMenuEntity selectMenuById(Long menuId) {
        return menuMapper.selectById(menuId);
    }

    /**
     * 是否存在菜单子节点
     * @param menuId 菜单ID
     * @return 结果
     */
    public boolean hasChildByMenuId(Long menuId) {
        int result = menuMapper.selectChildrenCount(menuId);
        return result > 0;
    }

    /**
     * 查询菜单使用数量
     * @param menuId 菜单ID
     * @return 结果
     */
    public boolean checkMenuExistRole(Long menuId) {
        int result = Math.toIntExact(roleMenuMapper.countByMenuId(menuId));
        return result > 0;
    }

    /**
     * 新增保存菜单信息
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(MenuSaveRequest menu) {
        SysMenuEntity entity = toEntity(menu);
        return menuMapper.insert(entity);
    }

    /**
     * 修改保存菜单信息
     * @param menu 菜单信息
     * @return 结果
     */
    public int updateMenu(MenuSaveRequest menu) {
        return menuMapper.updateById(toEntity(menu));
    }

    /**
     * 保存菜单排序
     * @param menuIds 菜单ID
     * @param orderNums 排序ID
     */
    @Transactional
    public void updateMenuSort(String[] menuIds, String[] orderNums) {
        try {
            for (int i = 0; i < menuIds.length; i++) {
                menuMapper.updateMenuSort(Convert.toLong(menuIds[i]), Convert.toInt(orderNums[i]));
            }
        } catch (Exception e) {
            throw ExceptionUtil.business(ErrorCodeEnums.MENU_SORT_SAVE_FAILED);
        }
    }

    /**
     * 删除菜单管理信息
     * @param menuId 菜单ID
     * @return 结果
     */
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean checkMenuNameUnique(MenuSaveRequest menu) {
        Long menuId = menu.getId() == null ? -1L : menu.getId();
        SysMenuEntity info = menuMapper.selectMenuByName(menu.getMenuName(), menu.getParentId());
        if (info != null && info.getId().longValue() != menuId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验路由名称是否唯一
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean checkRouteConfigUnique(MenuSaveRequest menu) {
        if (!UserConstants.TYPE_MENU.equals(menu.getMenuType())) {
            return UserConstants.UNIQUE;
        }
        if (!org.springframework.util.StringUtils.hasText(menu.getRouteName())) {
            return UserConstants.NOT_UNIQUE;
        }

        Long menuId = menu.getId() == null ? -1L : menu.getId();
        Long parentId = menu.getParentId();
        String routePath = menu.getRoutePath();
        String routeName = menu.getRouteName();
        List<SysMenuEntity> sysMenuList = menuMapper.selectMenusByPathOrRouteName(routePath, routeName);
        for (SysMenuEntity sysMenu : sysMenuList) {
            if (sysMenu.getId().longValue() != menuId.longValue()) {
                Long dbParentId = sysMenu.getParentId();
                String dbRoutePath = sysMenu.getRoutePath();
                if (org.apache.commons.lang3.Strings.CI.equalsAny(routePath, dbRoutePath)
                        && parentId.longValue() == dbParentId.longValue()) {
                    log.warn("[同级路由冲突] 同级下已存在相同路由路径 '{}'，冲突菜单：{}", dbRoutePath, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
                else if (org.apache.commons.lang3.Strings.CI.equalsAny(routePath, dbRoutePath)
                        && parentId.longValue() == MENU_ROOT_ID) {
                    log.warn("[根目录路由冲突] 根目录下路由 '{}' 必须唯一，已被菜单 '{}' 占用", routePath, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
                else if (org.apache.commons.lang3.Strings.CI.equalsAny(routeName, sysMenu.getRouteName())) {
                    log.warn("[路由名称冲突] 路由名称 '{}' 需全局唯一，已被菜单 '{}' 使用", routeName, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
            }
        }
        return UserConstants.UNIQUE;
    }

    private SysMenuEntity toEntity(MenuSaveRequest request) {
        SysMenuEntity entity = new SysMenuEntity();
        entity.setId(request.getId());
        entity.setParentId(request.getParentId());
        entity.setMenuName(request.getMenuName());
        entity.setOrderNum(request.getOrderNum());
        entity.setRoutePath(request.getRoutePath());
        entity.setVueComponentPath(request.getVueComponentPath());
        entity.setRouteName(request.getRouteName());
        entity.setIsCache(request.getIsCache());
        entity.setMenuType(request.getMenuType());
        entity.setVisible(Boolean.TRUE.equals(request.getVisible()));
        entity.setStatus(request.getStatus());
        entity.setPerms(request.getPerms());
        entity.setIcon(request.getIcon());
        entity.setRemark(request.getRemark());
        return entity;
    }
}
