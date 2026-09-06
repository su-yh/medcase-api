package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.MyBatisUtils;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.storage.pojo.FileAttachment;
import com.medcase.system.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapperX<SysUserEntity> {
    List<SysUser> selectUserList(
            @Param("user") SysUser user,
            @Param("deptIds") Collection<Long> deptIds,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    List<SysUser> selectAllocatedList(
            @Param("user") SysUser user, @Param("deptIds") Collection<Long> deptIds);

    List<SysUser> selectUnallocatedList(
            @Param("user") SysUser user, @Param("deptIds") Collection<Long> deptIds);

    default PageResult<SysUser> selectPage(PageParam pageParam, SysUser user) {
        return selectPage(pageParam, user, null);
    }

    default PageResult<SysUser> selectPage(
            PageParam pageParam, SysUser user, Collection<Long> deptIds) {
        return selectPage(pageParam, user, deptIds, null, null);
    }

    default PageResult<SysUser> selectPage(
            PageParam pageParam, SysUser user, Collection<Long> deptIds,
            String beginTime, String endTime) {
        Page<SysUser> page = MyBatisUtils.buildPage(pageParam);
        IPage<SysUser> userPage = selectUserPage(
                page, user, deptIds, beginTime, endTime);
        return new PageResult<>(userPage.getRecords(), userPage.getTotal());
    }

    IPage<SysUser> selectUserPage(
            Page<SysUser> page,
            @Param("user") SysUser user,
            @Param("deptIds") Collection<Long> deptIds,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    default PageResult<SysUser> selectAllocatedPage(PageParam pageParam, SysUser user) {
        return selectAllocatedPage(pageParam, user, null);
    }

    default PageResult<SysUser> selectAllocatedPage(
            PageParam pageParam, SysUser user, Collection<Long> deptIds) {
        Page<SysUser> page = MyBatisUtils.buildPage(pageParam);
        IPage<SysUser> userPage = selectAllocatedPage(page, user, deptIds);
        return new PageResult<>(userPage.getRecords(), userPage.getTotal());
    }

    IPage<SysUser> selectAllocatedPage(
            Page<SysUser> page,
            @Param("user") SysUser user,
            @Param("deptIds") Collection<Long> deptIds);

    default PageResult<SysUser> selectUnallocatedPage(PageParam pageParam, SysUser user) {
        return selectUnallocatedPage(pageParam, user, null);
    }

    default PageResult<SysUser> selectUnallocatedPage(
            PageParam pageParam, SysUser user, Collection<Long> deptIds) {
        Page<SysUser> page = MyBatisUtils.buildPage(pageParam);
        IPage<SysUser> userPage = selectUnallocatedPage(page, user, deptIds);
        return new PageResult<>(userPage.getRecords(), userPage.getTotal());
    }

    IPage<SysUser> selectUnallocatedPage(
            Page<SysUser> page,
            @Param("user") SysUser user,
            @Param("deptIds") Collection<Long> deptIds);

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
        SysUserEntity entity = new SysUserEntity();
        entity.setStatus(status);
        return update(entity, new LambdaUpdateWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUserId, userId));
    }

    default int updateUserAvatar(Long userId, FileAttachment avatar) {
        SysUserEntity entity = new SysUserEntity();
        entity.setAvatar(avatar);
        return update(entity, new LambdaUpdateWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUserId, userId));
    }

    default int updateLoginInfo(Long userId, String loginIp, Date loginDate) {
        SysUserEntity entity = new SysUserEntity();
        entity.setLoginIp(loginIp);
        entity.setLoginDate(loginDate);
        return update(entity, new LambdaUpdateWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUserId, userId));
    }

    default int resetUserPassword(Long userId, String password, Date pwdUpdateDate) {
        SysUserEntity entity = new SysUserEntity();
        entity.setPwdUpdateDate(pwdUpdateDate);
        entity.setPassword(password);
        return update(entity, new LambdaUpdateWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUserId, userId));
    }

    default int deleteUserById(Long userId) {
        SysUserEntity entity = new SysUserEntity();
        entity.setDelFlag("2");
        return update(entity, new LambdaUpdateWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUserId, userId));
    }

    default int deleteUsersByIds(Long[] userIds) {
        SysUserEntity entity = new SysUserEntity();
        entity.setDelFlag("2");
        return update(entity, new LambdaUpdateWrapper<SysUserEntity>()
                .in(SysUserEntity::getUserId, Arrays.asList(userIds)));
    }
}
