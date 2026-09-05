package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.utils.DateUtils;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Calendar;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

@Mapper
public interface SysRoleMapper extends BaseMapperX<SysRoleEntity> {
    default List<SysRoleEntity> selectRoleList(SysRole role, String beginTime, String endTime) {
        Date beginDate = beginTime == null || beginTime.isEmpty() ? null : DateUtils.parseDate(beginTime);
        Date endDate = endTime == null || endTime.isEmpty() ? null : DateUtils.parseDate(endTime);
        Calendar calendar = Calendar.getInstance();
        Date endDateExclusive = null;
        if (endDate != null) {
            calendar.setTime(endDate);
            calendar.add(Calendar.DATE, 1);
            endDateExclusive = calendar.getTime();
        }
        return selectList(build()
                .eq(SysRoleEntity::getDelFlag, "0")
                .eq(role != null && role.getRoleId() != null && role.getRoleId() != 0,
                        SysRoleEntity::getRoleId, role.getRoleId())
                .like(role != null && role.getRoleName() != null && !role.getRoleName().isEmpty(),
                        SysRoleEntity::getRoleName, role.getRoleName())
                .eq(role != null && role.getStatus() != null && !role.getStatus().isEmpty(),
                        SysRoleEntity::getStatus, role.getStatus())
                .like(role != null && role.getRoleKey() != null && !role.getRoleKey().isEmpty(),
                        SysRoleEntity::getRoleKey, role.getRoleKey())
                .ge(beginDate != null, SysRoleEntity::getCreateTime, beginDate)
                .lt(endDateExclusive != null, SysRoleEntity::getCreateTime, endDateExclusive)
                .orderByAsc(SysRoleEntity::getRoleSort));
    }

    List<SysRole> selectRolePermissionByUserId(Long userId);

    List<Long> selectRoleListByUserId(Long userId);

    List<SysRole> selectRolesByUserName(String userName);

    default SysRoleEntity selectRoleByName(String roleName) {
        return selectOne(build()
                .eq(SysRoleEntity::getRoleName, roleName)
                .eq(SysRoleEntity::getDelFlag, "0"));
    }

    default SysRoleEntity selectRoleByKey(String roleKey) {
        return selectOne(build()
                .eq(SysRoleEntity::getRoleKey, roleKey)
                .eq(SysRoleEntity::getDelFlag, "0"));
    }

    default int deleteRoleById(Long roleId) {
        return update(null, new LambdaUpdateWrapper<SysRoleEntity>()
                .set(SysRoleEntity::getDelFlag, "2")
                .eq(SysRoleEntity::getRoleId, roleId));
    }

    default int deleteRolesByIds(Long[] roleIds) {
        return update(null, new LambdaUpdateWrapper<SysRoleEntity>()
                .set(SysRoleEntity::getDelFlag, "2")
                .in(SysRoleEntity::getRoleId, Arrays.asList(roleIds)));
    }
}
