package com.medcase.biz.mapper;

import com.medcase.biz.domain.DoctorUserEntity;
import com.medcase.biz.request.DoctorUserQuery;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import org.apache.ibatis.annotations.Mapper;
import org.jspecify.annotations.NonNull;
import org.springframework.util.StringUtils;

/**
 * 用户 Mapper。
 *
 * @author suyh
 */
@Mapper
public interface DoctorUserMapper extends BaseMapperX<DoctorUserEntity> {
    default boolean usernameExists(String username, UserTypeEnums userType) {
        if (!StringUtils.hasText(username)) {
            return false;
        }

        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getUserName, username);
        queryWrapper.eq(DoctorUserEntity::getUserType, userType);
        return exists(queryWrapper);
    }

    default boolean phoneExists(String phone, UserTypeEnums userType) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }

        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getPhonenumber, phone);
        queryWrapper.eq(DoctorUserEntity::getUserType, userType);
        return exists(queryWrapper);
    }

    default DoctorUserEntity selectUserByUsername(String username, UserTypeEnums userType) {
        if (!StringUtils.hasText(username)) {
            return null;
        }

        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getUserName, username);
        queryWrapper.eq(DoctorUserEntity::getUserType, userType);
        return selectOne(queryWrapper);
    }

    default DoctorUserEntity selectUserById(Long userId, UserTypeEnums userType) {
        if (userId == null) {
            return null;
        }

        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getUserId, userId);
        queryWrapper.eq(DoctorUserEntity::getUserType, userType);
        return selectOne(queryWrapper);
    }

    default PageResult<DoctorUserEntity> selectUserPage(
            PageParam pageParam, @NonNull DoctorUserQuery query, UserTypeEnums userType) {
        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getUserType, userType);
        queryWrapper.likeIfPresent(DoctorUserEntity::getNickName, query.getNickName());
        queryWrapper.likeIfPresent(DoctorUserEntity::getPhonenumber, query.getPhone());
        queryWrapper.eqIfPresent(DoctorUserEntity::getStatus, query.getStatus());
        queryWrapper.orderByDesc(DoctorUserEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }


    default boolean phoneExists(String phone) {
        return phoneExists(phone, UserTypeEnums.DOCTOR);
    }

}
