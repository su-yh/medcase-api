package com.medcase.system.plus.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.plus.entity.SysMenuEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysMenuMapper extends BaseMapperX<SysMenuEntity> {
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
