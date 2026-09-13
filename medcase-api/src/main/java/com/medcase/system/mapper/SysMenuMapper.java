package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.medcase.common.constant.UserConstants;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.system.entity.SysMenuEntity;
import com.medcase.web.controller.system.dto.MenuQueryRequest;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapperX<SysMenuEntity> {
    default List<SysMenuEntity> selectMenuList(MenuQueryRequest menu) {
        LambdaQueryWrapperX<SysMenuEntity> query = build();
        query.likeIfPresent(SysMenuEntity::getMenuName, menu.getMenuName());
        query.eqIfPresent(SysMenuEntity::getVisible, menu.getVisible());
        query.eqIfPresent(SysMenuEntity::getStatus, menu.getStatus());
        query.orderByAsc(SysMenuEntity::getParentId);
        query.orderByAsc(SysMenuEntity::getOrderNum);
        return selectList(query);
    }

    List<SysMenuEntity> selectMenuListByUserId(
            @Param("menu") MenuQueryRequest menu, @Param("userId") Long userId);

    List<String> selectMenuPermsByRoleId(Long roleId);

    List<String> selectMenuPermsByUserId(Long userId);

    default List<SysMenuEntity> selectMenuTreeAll() {
        LambdaQueryWrapperX<SysMenuEntity> query = build();
        query.in(SysMenuEntity::getMenuType, UserConstants.TYPE_DIR, UserConstants.TYPE_MENU);
        query.eq(SysMenuEntity::getStatus, UserConstants.NORMAL);
        query.orderByAsc(SysMenuEntity::getParentId);
        query.orderByAsc(SysMenuEntity::getOrderNum);
        return selectList(query);
    }

    List<SysMenuEntity> selectMenuTreeByUserId(Long userId);

    default List<SysMenuEntity> selectMenusByPathOrRouteName(String routePath, String routeName) {
        LambdaQueryWrapperX<SysMenuEntity> query = build();
        query.in(SysMenuEntity::getMenuType, UserConstants.TYPE_DIR, UserConstants.TYPE_MENU);
        query.and(condition -> {
            condition.eq(SysMenuEntity::getRoutePath, routePath);
            condition.or();
            condition.eq(SysMenuEntity::getRouteName, routeName);
        });
        return selectList(query);
    }

    default int selectChildrenCount(Long menuId) {
        LambdaQueryWrapperX<SysMenuEntity> query = build();
        query.eq(SysMenuEntity::getParentId, menuId);
        return Math.toIntExact(selectCount(query));
    }

    default SysMenuEntity selectMenuByName(String menuName, Long parentId) {
        LambdaQueryWrapperX<SysMenuEntity> query = build();
        query.eq(SysMenuEntity::getMenuName, menuName);
        query.eq(SysMenuEntity::getParentId, parentId);
        return selectOne(query);
    }

    default int updateMenuSort(Long menuId, Integer orderNum) {
        SysMenuEntity entity = new SysMenuEntity();
        entity.setOrderNum(orderNum);
        return update(entity, new LambdaUpdateWrapper<SysMenuEntity>()
                .eq(SysMenuEntity::getId, menuId));
    }

}
