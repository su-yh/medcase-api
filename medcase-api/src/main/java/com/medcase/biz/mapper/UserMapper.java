package com.medcase.biz.mapper;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.request.UserQuery;
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
public interface UserMapper extends BaseMapperX<UserEntity> {
    default boolean usernameExists(String username, UserTypeEnums userType) {
        if (!StringUtils.hasText(username)) {
            return false;
        }

        LambdaQueryWrapperX<UserEntity> queryWrapper = build();
        queryWrapper.eq(UserEntity::getUserName, username);
        queryWrapper.eq(UserEntity::getUserType, userType);
        return exists(queryWrapper);
    }

    default boolean phoneExists(String phone, UserTypeEnums userType) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }

        LambdaQueryWrapperX<UserEntity> queryWrapper = build();
        queryWrapper.eq(UserEntity::getPhonenumber, phone);
        queryWrapper.eq(UserEntity::getUserType, userType);
        return exists(queryWrapper);
    }

    default UserEntity selectUserByUsername(String username, UserTypeEnums userType) {
        if (!StringUtils.hasText(username)) {
            return null;
        }

        LambdaQueryWrapperX<UserEntity> queryWrapper = build();
        queryWrapper.eq(UserEntity::getUserName, username);
        queryWrapper.eq(UserEntity::getUserType, userType);
        return selectOne(queryWrapper);
    }

    default UserEntity selectUserById(Long userId, UserTypeEnums userType) {
        if (userId == null) {
            return null;
        }

        LambdaQueryWrapperX<UserEntity> queryWrapper = build();
        queryWrapper.eq(UserEntity::getUserId, userId);
        queryWrapper.eq(UserEntity::getUserType, userType);
        return selectOne(queryWrapper);
    }

    default PageResult<UserEntity> selectUserPage(
            PageParam pageParam, @NonNull UserQuery query, UserTypeEnums userType) {
        LambdaQueryWrapperX<UserEntity> queryWrapper = build();
        queryWrapper.eq(UserEntity::getUserType, userType);
        queryWrapper.likeIfPresent(UserEntity::getNickName, query.getNickName());
        queryWrapper.likeIfPresent(UserEntity::getPhonenumber, query.getPhone());
        queryWrapper.eqIfPresent(UserEntity::getStatus, query.getStatus());
        queryWrapper.orderByDesc(UserEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }


    default boolean phoneExists(String phone) {
        return phoneExists(phone, UserTypeEnums.DOCTOR);
    }

}
