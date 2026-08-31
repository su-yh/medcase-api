package com.medcase.system.plus.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.plus.entity.SysDeptEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface SysDeptMapper extends BaseMapperX<SysDeptEntity> {
    List<SysDept> selectDeptList(SysDept dept);

    List<Long> selectDeptListByRoleId(
            @Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);

    default int selectNormalChildrenCount(Long deptId) {
        return Math.toIntExact(selectCount(build()
                .eq(SysDeptEntity::getStatus, "0")
                .eq(SysDeptEntity::getDelFlag, "0")
                .apply("find_in_set({0}, ancestors)", deptId)));
    }

    default int selectChildrenCount(Long deptId) {
        return Math.toIntExact(selectCount(build()
                .eq(SysDeptEntity::getParentId, deptId)
                .eq(SysDeptEntity::getDelFlag, "0")));
    }

    default List<SysDeptEntity> selectChildrenByDeptId(Long deptId) {
        return selectList(build().apply("find_in_set({0}, ancestors)", deptId));
    }

    default SysDeptEntity selectDeptByName(String deptName, Long parentId) {
        LambdaQueryWrapper<SysDeptEntity> query = build()
                .eq(SysDeptEntity::getDeptName, deptName)
                .eq(SysDeptEntity::getParentId, parentId)
                .eq(SysDeptEntity::getDelFlag, "0");
        return selectOne(query);
    }

    default int updateParentStatusNormal(Collection<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return 0;
        }
        return update(null, new LambdaUpdateWrapper<SysDeptEntity>()
                .set(SysDeptEntity::getStatus, "0")
                .in(SysDeptEntity::getDeptId, deptIds));
    }

    default int updateDeptAncestors(Long deptId, String ancestors) {
        return update(null, new LambdaUpdateWrapper<SysDeptEntity>()
                .set(SysDeptEntity::getAncestors, ancestors)
                .eq(SysDeptEntity::getDeptId, deptId));
    }

    default int updateDeptSort(Long deptId, Integer orderNum) {
        return update(null, new LambdaUpdateWrapper<SysDeptEntity>()
                .set(SysDeptEntity::getOrderNum, orderNum)
                .eq(SysDeptEntity::getDeptId, deptId));
    }

    default int deleteDeptById(Long deptId) {
        return update(null, new LambdaUpdateWrapper<SysDeptEntity>()
                .set(SysDeptEntity::getDelFlag, "2")
                .eq(SysDeptEntity::getDeptId, deptId));
    }
}
