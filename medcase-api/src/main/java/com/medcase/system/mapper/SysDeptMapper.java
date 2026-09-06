package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.medcase.common.constant.UserConstants;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.entity.SysDeptEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface SysDeptMapper extends BaseMapperX<SysDeptEntity> {
    default List<SysDeptEntity> selectAllDepartments() {
        return selectList(build()
                .eq(SysDeptEntity::getDelFlag, UserConstants.NORMAL)
                .orderByAsc(SysDeptEntity::getParentId)
                .orderByAsc(SysDeptEntity::getOrderNum));
    }

    default List<SysDeptEntity> selectChildrenByDeptId(Long deptId) {
        return selectList(build().apply("find_in_set({0}, ancestors)", deptId));
    }

    default int updateParentStatusNormal(Collection<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return 0;
        }
        SysDeptEntity entity = new SysDeptEntity();
        entity.setStatus("0");
        return update(entity, new LambdaUpdateWrapper<SysDeptEntity>()
                .in(SysDeptEntity::getDeptId, deptIds));
    }

    default int updateDeptAncestors(Long deptId, String ancestors) {
        SysDeptEntity entity = new SysDeptEntity();
        entity.setAncestors(ancestors);
        return update(entity, new LambdaUpdateWrapper<SysDeptEntity>()
                .eq(SysDeptEntity::getDeptId, deptId));
    }

    default int updateDeptSort(Long deptId, Integer orderNum) {
        SysDeptEntity entity = new SysDeptEntity();
        entity.setOrderNum(orderNum);
        return update(entity, new LambdaUpdateWrapper<SysDeptEntity>()
                .eq(SysDeptEntity::getDeptId, deptId));
    }

    default int deleteDeptById(Long deptId) {
        SysDeptEntity entity = new SysDeptEntity();
        entity.setDelFlag("2");
        return update(entity, new LambdaUpdateWrapper<SysDeptEntity>()
                .eq(SysDeptEntity::getDeptId, deptId));
    }
}
