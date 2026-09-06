package com.medcase.system.service;

import com.medcase.common.constant.Constants;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.TreeSelect;
import com.medcase.common.core.text.Convert;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.domain.vo.MetaVo;
import com.medcase.system.domain.vo.RouterVo;
import com.medcase.system.entity.SysMenuEntity;
import com.medcase.system.mapper.SysMenuMapper;
import com.medcase.system.mapper.SysRoleMenuMapper;
import com.medcase.web.controller.system.dto.MenuQueryRequest;
import com.medcase.web.controller.system.dto.MenuSaveRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单 业务层处理
 */
@Service
public class SysMenuService {

    private static final Logger log = LoggerFactory.getLogger(SysMenuService.class);

    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

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

        List<SysMenuEntity> menuList = null;
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

        List<SysMenuEntity> menus = null;
        if (SecurityUtils.isAdmin(userId)) {

            menus = menuMapper.selectMenuTreeAll();
        }
        else {

            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, MENU_ROOT_ID);
    }

    /**
     * 构建前端路由所需要的菜单
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<SysMenuEntity> menus) {

        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenuEntity menu : menus) {

            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(
                    menu.getMenuName(), menu.getIcon(), org.apache.commons.lang3.Strings.CS.equals("1", menu.getIsCache()),
                    menu.getPath()));
            List<SysMenuEntity> cMenus = menu.getChildren();
            if (!org.springframework.util.CollectionUtils.isEmpty(cMenus)
                    && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {

                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            }
            else if (isMenuFrame(menu)) {

                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(getRouteName(menu.getRouteName(), menu.getPath()));
                children.setMeta(new MetaVo(
                        menu.getMenuName(), menu.getIcon(),
                        org.apache.commons.lang3.Strings.CS.equals("1", menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            else if (menu.getParentId().intValue() == MENU_ROOT_ID && isInnerLink(menu)) {

                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(getRouteName(menu.getRouteName(), routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要树结构
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenuEntity> buildMenuTree(List<SysMenuEntity> menus) {

        List<SysMenuEntity> returnList = new ArrayList<SysMenuEntity>();
        List<Long> tempList = menus.stream().map(SysMenuEntity::getMenuId).collect(Collectors.toList());
        for (Iterator<SysMenuEntity> iterator = menus.iterator(); iterator.hasNext();) {

            SysMenuEntity menu = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {

                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {

            returnList = menus;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenuEntity> menus) {

        List<SysMenuEntity> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
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
        int row = menuMapper.insert(entity);
        return row;
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

                menuMapper.updateMenuSort(
                        Convert.toLong(menuIds[i]), Convert.toInt(orderNums[i]));
            }
        }
        catch (Exception e) {

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

        Long menuId = menu.getMenuId() == null ? -1L : menu.getMenuId();
        SysMenuEntity info = menuMapper.selectMenuByName(menu.getMenuName(), menu.getParentId());
        if (info != null && info.getMenuId().longValue() != menuId.longValue()) {

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

        Long menuId = menu.getMenuId() == null ? -1L : menu.getMenuId();
        Long parentId = menu.getParentId();
        String path = menu.getPath();
        String routeName = !org.springframework.util.StringUtils.hasText(menu.getRouteName())
                ? path : menu.getRouteName();
        List<SysMenuEntity> sysMenuList = menuMapper.selectMenusByPathOrRouteName(path, routeName);
        for (SysMenuEntity sysMenu : sysMenuList) {

            if (sysMenu.getMenuId().longValue() != menuId.longValue()) {

                Long dbParentId = sysMenu.getParentId();
                String dbPath = sysMenu.getPath();
                String dbRouteName = !org.springframework.util.StringUtils.hasText(sysMenu.getRouteName())
                        ? dbPath : sysMenu.getRouteName();
                if (org.apache.commons.lang3.Strings.CI.equalsAny(path, dbPath)
                        && parentId.longValue() == dbParentId.longValue()) {

                    log.warn("[同级路由冲突] 同级下已存在相同路由路径 '{}'，冲突菜单：{}", dbPath, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
                else if (org.apache.commons.lang3.Strings.CI.equalsAny(path, dbPath)
                        && parentId.longValue() == MENU_ROOT_ID) {

                    log.warn("[根目录路由冲突] 根目录下路由 '{}' 必须唯一，已被菜单 '{}' 占用", path, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
                else if (org.apache.commons.lang3.Strings.CI.equalsAny(routeName, dbRouteName)) {

                    log.warn("[路由名称冲突] 路由名称 '{}' 需全局唯一，已被菜单 '{}' 使用", routeName, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
            }
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 获取路由名称
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenuEntity menu) {

        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {

            return "";
        }
        return getRouteName(menu.getRouteName(), menu.getPath());
    }

    /**
     * 获取路由名称，如没有配置路由名称则取路由地址
     * @param name 路由名称
     * @param path 路由地址
     * @return 路由名称（驼峰格式）
     */
    public String getRouteName(String name, String path) {

        String routerName = org.springframework.util.StringUtils.hasText(name) ? name : path;
        return org.springframework.util.StringUtils.capitalize(routerName);
    }

    /**
     * 获取路由地址
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenuEntity menu) {

        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != MENU_ROOT_ID && isInnerLink(menu)) {

            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (MENU_ROOT_ID == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {

            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {

            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenuEntity menu) {

        String component = UserConstants.LAYOUT;
        if (org.springframework.util.StringUtils.hasText(menu.getComponent()) && !isMenuFrame(menu)) {

            component = menu.getComponent();
        }
        else if (!org.springframework.util.StringUtils.hasText(menu.getComponent())
                && menu.getParentId().intValue() != MENU_ROOT_ID && isInnerLink(menu)) {

            component = UserConstants.INNER_LINK;
        }
        else if (!org.springframework.util.StringUtils.hasText(menu.getComponent()) && isParentView(menu)) {

            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenuEntity menu) {

        return menu.getParentId().intValue() == MENU_ROOT_ID && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为parent_view组件
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenuEntity menu) {

        return menu.getParentId().intValue() != MENU_ROOT_ID && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    /**
     * 是否为内链组件
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenuEntity menu) {

        return menu.getIsFrame().equals(UserConstants.NO_FRAME)
                && org.apache.commons.lang3.Strings.CS.startsWithAny(
                        menu.getPath(), Constants.HTTP, Constants.HTTPS);
    }

    /**
     * 根据父节点的ID获取所有子节点
     * @param list 分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenuEntity> getChildPerms(List<SysMenuEntity> list, long parentId) {

        List<SysMenuEntity> returnList = new ArrayList<SysMenuEntity>();
        for (Iterator<SysMenuEntity> iterator = list.iterator(); iterator.hasNext();) {

            SysMenuEntity t = iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {

                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     * @param list 分类表
     * @param t 子节点
     */
    private void recursionFn(List<SysMenuEntity> list, SysMenuEntity t) {

        // 得到子节点列表
        List<SysMenuEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenuEntity tChild : childList) {

            if (hasChild(list, tChild)) {

                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenuEntity> getChildList(List<SysMenuEntity> list, SysMenuEntity t) {

        List<SysMenuEntity> tlist = new ArrayList<SysMenuEntity>();
        Iterator<SysMenuEntity> it = list.iterator();
        while (it.hasNext()) {

            SysMenuEntity n = it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {

                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenuEntity> list, SysMenuEntity t) {

        return getChildList(list, t).size() > 0;
    }

    /**
     * 内链域名特殊字符替换
     * @return 替换后的内链域名
     */
    public String innerLinkReplaceEach(String path) {

        return org.apache.commons.lang3.StringUtils.replaceEach(
                path, new String[] { Constants.HTTP, Constants.HTTPS, Constants.WWW, ".", ":" },
                new String[] { "", "", "", "/", "/" });
    }

    private SysMenuEntity toEntity(MenuSaveRequest request) {
        SysMenuEntity entity = new SysMenuEntity();
        entity.setMenuId(request.getMenuId());
        entity.setParentId(request.getParentId());
        entity.setMenuName(request.getMenuName());
        entity.setOrderNum(request.getOrderNum());
        entity.setPath(request.getPath());
        entity.setComponent(request.getComponent());
        entity.setQuery(request.getQuery());
        entity.setRouteName(request.getRouteName());
        entity.setIsFrame(request.getIsFrame());
        entity.setIsCache(request.getIsCache());
        entity.setMenuType(request.getMenuType());
        entity.setVisible(request.getVisible());
        entity.setStatus(request.getStatus());
        entity.setPerms(request.getPerms());
        entity.setIcon(request.getIcon());
        entity.setRemark(request.getRemark());
        return entity;
    }
}
