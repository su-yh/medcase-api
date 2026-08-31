package com.medcase.system.plus.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.storage.pojo.FileAttachment;
import com.medcase.system.plus.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapperX<SysUserEntity> {
    List<SysUser> selectUserList(SysUser user);

    List<SysUser> selectAllocatedList(SysUser user);

    List<SysUser> selectUnallocatedList(SysUser user);

    default SysUserEntity selectUserByUserName(
            String userName, String userType, String delFlag) {
        return selectOne(build()
                .eq(SysUserEntity::getUserName, userName)
                .eq(SysUserEntity::getUserType, userType)
                .eq(SysUserEntity::getDelFlag, delFlag));
    }

    default SysUserEntity selectUserByUserNameAndType(
            String userName, UserTypeEnums userType, String delFlag) {
        return selectOne(build()
                .eq(SysUserEntity::getUserName, userName)
                .eq(SysUserEntity::getUserType, userType)
                .eq(SysUserEntity::getDelFlag, delFlag));
    }

    default SysUserEntity selectUserByPhoneAndType(
            String phone, UserTypeEnums userType, String delFlag) {
        return selectOne(build()
                .eq(SysUserEntity::getPhonenumber, phone)
                .eq(SysUserEntity::getUserType, userType)
                .eq(SysUserEntity::getDelFlag, delFlag));
    }

    default SysUserEntity selectUserByEmailAndType(
            String email, UserTypeEnums userType, String delFlag) {
        return selectOne(build()
                .eq(SysUserEntity::getEmail, email)
                .eq(SysUserEntity::getUserType, userType)
                .eq(SysUserEntity::getDelFlag, delFlag));
    }

    default Long countByDeptId(Long deptId) {
        return selectCount(build()
                .eq(SysUserEntity::getDeptId, deptId)
                .eq(SysUserEntity::getDelFlag, "0"));
    }

    default int updateUserStatus(Long userId, String status) {
        return update(null, new LambdaUpdateWrapper<SysUserEntity>()
                .set(SysUserEntity::getStatus, status)
                .eq(SysUserEntity::getUserId, userId));
    }

    default int updateUserAvatar(Long userId, FileAttachment avatar) {
        return update(null, new LambdaUpdateWrapper<SysUserEntity>()
                .set(SysUserEntity::getAvatar, avatar)
                .eq(SysUserEntity::getUserId, userId));
    }

    default int updateLoginInfo(Long userId, String loginIp, Date loginDate) {
        return update(null, new LambdaUpdateWrapper<SysUserEntity>()
                .set(SysUserEntity::getLoginIp, loginIp)
                .set(SysUserEntity::getLoginDate, loginDate)
                .eq(SysUserEntity::getUserId, userId));
    }

    default int resetUserPassword(Long userId, String password, Date pwdUpdateDate) {
        return update(null, new LambdaUpdateWrapper<SysUserEntity>()
                .set(SysUserEntity::getPwdUpdateDate, pwdUpdateDate)
                .set(SysUserEntity::getPassword, password)
                .eq(SysUserEntity::getUserId, userId));
    }

    default int deleteUserById(Long userId) {
        return update(null, new LambdaUpdateWrapper<SysUserEntity>()
                .set(SysUserEntity::getDelFlag, "2")
                .eq(SysUserEntity::getUserId, userId));
    }

    default int deleteUsersByIds(Long[] userIds) {
        return update(null, new LambdaUpdateWrapper<SysUserEntity>()
                .set(SysUserEntity::getDelFlag, "2")
                .in(SysUserEntity::getUserId, Arrays.asList(userIds)));
    }
}
