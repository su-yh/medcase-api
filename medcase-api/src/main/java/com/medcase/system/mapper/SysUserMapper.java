package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medcase.common.constant.UserConstants;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapperX<SysUserEntity> {
    List<SysUserEntity> selectAllocatedList(
            @Param("user") UserQueryRequest user, @Param("deptIds") Collection<Long> deptIds);

    List<SysUserEntity> selectUnallocatedList(
            @Param("user") UserQueryRequest user, @Param("deptIds") Collection<Long> deptIds);

    default PageResult<SysUserEntity> selectUserPage(
            PageParam pageParam, UserQueryRequest user, Collection<Long> deptIds,
            String beginTime, String endTime) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.eq(SysUserEntity::getDelFlag, UserConstants.NORMAL);
        query.select(SysUserEntity::getUserId, SysUserEntity::getDeptId,
                SysUserEntity::getNickName, SysUserEntity::getUserName,
                SysUserEntity::getUserType, SysUserEntity::getEmail,
                SysUserEntity::getAvatar, SysUserEntity::getPhonenumber,
                SysUserEntity::getSex, SysUserEntity::getStatus,
                SysUserEntity::getDelFlag, SysUserEntity::getCreateBy,
                SysUserEntity::getCreateTime, SysUserEntity::getRemark);
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

    default SysUserEntity selectUserByUserName(
            String userName, String userType, String delFlag) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.eq(SysUserEntity::getUserName, userName);
        query.eq(SysUserEntity::getUserType, userType);
        query.eq(SysUserEntity::getDelFlag, delFlag);
        return selectOne(query);
    }

    default SysUserEntity selectUserByUserNameAndType(
            String userName, UserTypeEnums userType, String delFlag) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.eq(SysUserEntity::getUserName, userName);
        query.eq(SysUserEntity::getUserType, userType);
        query.eq(SysUserEntity::getDelFlag, delFlag);
        return selectOne(query);
    }

    default SysUserEntity selectUserByPhoneAndType(
            String phone, UserTypeEnums userType, String delFlag) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.eq(SysUserEntity::getPhonenumber, phone);
        query.eq(SysUserEntity::getUserType, userType);
        query.eq(SysUserEntity::getDelFlag, delFlag);
        return selectOne(query);
    }

    default SysUserEntity selectUserByEmailAndType(
            String email, UserTypeEnums userType, String delFlag) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.eq(SysUserEntity::getEmail, email);
        query.eq(SysUserEntity::getUserType, userType);
        query.eq(SysUserEntity::getDelFlag, delFlag);
        return selectOne(query);
    }

    default Long countByDeptId(Long deptId) {
        LambdaQueryWrapperX<SysUserEntity> query = build();
        query.eq(SysUserEntity::getDeptId, deptId);
        query.eq(SysUserEntity::getDelFlag, "0");
        return selectCount(query);
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

    default int resetUserPassword(Long userId, String password, Date pwdUpdateDate) {
        SysUserEntity entity = new SysUserEntity();
        entity.setPwdUpdateDate(pwdUpdateDate);
        entity.setPassword(password);
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
