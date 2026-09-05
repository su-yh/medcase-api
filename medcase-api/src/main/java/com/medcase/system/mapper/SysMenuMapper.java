package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.common.core.domain.entity.SysMenu;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.entity.SysMenuEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapperX<SysMenuEntity> {
    List<SysMenu> selectMenuList(SysMenu menu);

    List<String> selectMenuPerms();

    List<SysMenu> selectMenuListByUserId(
            @Param("menu") SysMenu menu, @Param("userId") Long userId);

    List<String> selectMenuPermsByRoleId(Long roleId);

    List<String> selectMenuPermsByUserId(Long userId);

    List<SysMenu> selectMenuTreeAll();

    List<SysMenu> selectMenuTreeByUserId(Long userId);

    List<Long> selectMenuListByRoleId(
            @Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    List<SysMenu> selectMenusByPathOrRouteName(
            @Param("path") String path, @Param("routeName") String routeName);

    default int selectChildrenCount(Long menuId) {
        return Math.toIntExact(selectCount(
                build().eq(SysMenuEntity::getParentId, menuId)));
    }

    default SysMenuEntity selectMenuByName(String menuName, Long parentId) {
        LambdaQueryWrapper<SysMenuEntity> query = build()
                .eq(SysMenuEntity::getMenuName, menuName)
                .eq(SysMenuEntity::getParentId, parentId);
        return selectOne(query);
    }

    default int updateMenuSort(Long menuId, Integer orderNum) {
        return update(null, new LambdaUpdateWrapper<SysMenuEntity>()
                .set(SysMenuEntity::getOrderNum, orderNum)
                .eq(SysMenuEntity::getMenuId, menuId));
    }

}
