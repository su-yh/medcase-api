package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medcase.biz.request.UserQuery;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.MyBatisUtils;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.storage.pojo.FileAttachment;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.web.controller.system.dto.UserQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapperX<SysUserEntity> {
    default boolean usernameExists(String username, UserTypeEnums userType) {
        if (!StringUtils.hasText(username)) {
            return false;
        }

        LambdaQueryWrapperX<SysUserEntity> queryWrapper = build();
        queryWrapper.eq(SysUserEntity::getUserName, username);
        queryWrapper.eq(SysUserEntity::getUserType, userType);
        return exists(queryWrapper);
    }

    default boolean phoneExists(String phone, UserTypeEnums userType) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }

        LambdaQueryWrapperX<SysUserEntity> queryWrapper = build();
        queryWrapper.eq(SysUserEntity::getPhonenumber, phone);
        queryWrapper.eq(SysUserEntity::getUserType, userType);
        return exists(queryWrapper);
    }

    default boolean phoneExists(String phone) {
        return phoneExists(phone, UserTypeEnums.DOCTOR);
    }

    default SysUserEntity selectUserById(Long userId) {
        if (userId == null) {
            return null;
        }

        return selectById(userId);
    }

    default SysUserEntity selectUserById(Long userId, UserTypeEnums userType) {
        if (userId == null) {
            return null;
        }

        LambdaQueryWrapperX<SysUserEntity> queryWrapper = build();
        queryWrapper.eq(SysUserEntity::getUserId, userId);
        queryWrapper.eq(SysUserEntity::getUserType, userType);
        return selectOne(queryWrapper);
    }

    default SysUserEntity selectUserByUserName(String userName, UserTypeEnums userType) {
        if (!StringUtils.hasText(userName)) {
            return null;
        }

        LambdaQueryWrapperX<SysUserEntity> queryWrapper = build();
        queryWrapper.eq(SysUserEntity::getUserName, userName);
        queryWrapper.eq(SysUserEntity::getUserType, userType);
        return selectOne(queryWrapper);
    }

    default PageResult<SysUserEntity> selectUserPage(PageParam pageParam, UserQuery query) {
        LambdaQueryWrapperX<SysUserEntity> queryWrapper = build();
        queryWrapper.eqIfPresent(SysUserEntity::getSupplierId, query.getSupplierId());
        queryWrapper.likeIfPresent(SysUserEntity::getNickName, query.getNickName());
        queryWrapper.likeIfPresent(SysUserEntity::getPhonenumber, query.getPhone());
        queryWrapper.eqIfPresent(SysUserEntity::getStatus, query.getStatus());
        queryWrapper.orderByDesc(SysUserEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }

    default PageResult<SysUserEntity> selectUserPage(
            PageParam pageParam, UserQuery query, UserTypeEnums userType) {
        LambdaQueryWrapperX<SysUserEntity> queryWrapper = build();
        queryWrapper.eqIfPresent(SysUserEntity::getSupplierId, query.getSupplierId());
        queryWrapper.eq(SysUserEntity::getUserType, userType);
        queryWrapper.likeIfPresent(SysUserEntity::getNickName, query.getNickName());
        queryWrapper.likeIfPresent(SysUserEntity::getPhonenumber, query.getPhone());
        queryWrapper.eqIfPresent(SysUserEntity::getStatus, query.getStatus());
        queryWrapper.orderByDesc(SysUserEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }

    List<SysUserEntity> selectAllocatedList(
            @Param("user") UserQueryRequest user, @Param("deptIds") Collection<Long> deptIds);

    List<SysUserEntity> selectUnallocatedList(
            @Param("user") UserQueryRequest user, @Param("deptIds") Collection<Long> deptIds);

    default PageResult<SysUserEntity> selectUserPage(
            PageParam pageParam, UserQueryRequest user, Collection<Long> deptIds,
            String beginTime, String endTime) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.select(SysUserEntity::getUserId, SysUserEntity::getDeptId,
                SysUserEntity::getNickName, SysUserEntity::getUserName,
                SysUserEntity::getUserType, SysUserEntity::getEmail,
                SysUserEntity::getAvatar, SysUserEntity::getPhonenumber,
                SysUserEntity::getSex, SysUserEntity::getStatus,
                SysUserEntity::getCreateBy, SysUserEntity::getCreateTime,
                SysUserEntity::getRemark);
        if (user != null) {
            query.eq(user.getUserId() != null && !Long.valueOf(0L).equals(user.getUserId()),
                    SysUserEntity::getUserId, user.getUserId());
            query.likeIfPresent(SysUserEntity::getUserName, user.getUserName());
            query.likeIfPresent(SysUserEntity::getNickName, user.getNickName());
            query.eqIfPresent(SysUserEntity::getStatus, user.getStatus());
            query.eqIfPresent(SysUserEntity::getUserType, user.getUserType());
            query.likeIfPresent(SysUserEntity::getPhonenumber, user.getPhonenumber());
        }
        query.apply(StringUtils.hasText(beginTime),
                "date_format(create_time,'%Y%m%d') >= date_format({0},'%Y%m%d')",
                beginTime);
        query.apply(StringUtils.hasText(endTime),
                "date_format(create_time,'%Y%m%d') <= date_format({0},'%Y%m%d')",
                endTime);
        query.inIfPresent(SysUserEntity::getDeptId, deptIds);
        return selectPage(pageParam, query);
    }

    default PageResult<SysUserEntity> selectAllocatedPage(PageParam pageParam, UserQueryRequest user) {
        return selectAllocatedPage(pageParam, user, null);
    }

    default PageResult<SysUserEntity> selectAllocatedPage(
            PageParam pageParam, UserQueryRequest user, Collection<Long> deptIds) {
        Page<SysUserEntity> page = MyBatisUtils.buildPage(pageParam);
        IPage<SysUserEntity> userPage = selectAllocatedPage(page, user, deptIds);
        return new PageResult<>(userPage.getRecords(), userPage.getTotal());
    }

    IPage<SysUserEntity> selectAllocatedPage(
            Page<SysUserEntity> page,
            @Param("user") UserQueryRequest user,
            @Param("deptIds") Collection<Long> deptIds);

    default PageResult<SysUserEntity> selectUnallocatedPage(PageParam pageParam, UserQueryRequest user) {
        return selectUnallocatedPage(pageParam, user, null);
    }

    default PageResult<SysUserEntity> selectUnallocatedPage(
            PageParam pageParam, UserQueryRequest user, Collection<Long> deptIds) {
        Page<SysUserEntity> page = MyBatisUtils.buildPage(pageParam);
        IPage<SysUserEntity> userPage = selectUnallocatedPage(page, user, deptIds);
        return new PageResult<>(userPage.getRecords(), userPage.getTotal());
    }

    IPage<SysUserEntity> selectUnallocatedPage(
            Page<SysUserEntity> page,
            @Param("user") UserQueryRequest user,
            @Param("deptIds") Collection<Long> deptIds);

    default SysUserEntity selectUserByPhoneAndType(
            String phone, UserTypeEnums userType) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.eq(SysUserEntity::getPhonenumber, phone);
        query.eq(SysUserEntity::getUserType, userType);
        return selectOne(query);
    }

    default SysUserEntity selectUserByEmailAndType(
            String email, UserTypeEnums userType) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.eq(SysUserEntity::getEmail, email);
        query.eq(SysUserEntity::getUserType, userType);
        return selectOne(query);
    }

    default Long countByDeptId(Long deptId) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.eq(SysUserEntity::getDeptId, deptId);
        return selectCount(query);
    }

    default int updateUserStatus(Long userId, UserStatusEnums status) {
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

    default int resetUserPassword(Long userId, String password, Date pwdUpdateDate) {
        SysUserEntity entity = new SysUserEntity();
        entity.setPwdUpdateDate(pwdUpdateDate);
        entity.setPassword(password);
        return update(entity, new LambdaUpdateWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUserId, userId));
    }

}
