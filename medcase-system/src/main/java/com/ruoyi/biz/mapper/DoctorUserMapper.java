package com.ruoyi.biz.mapper;

import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.biz.request.DoctorUserQuery;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mp.mybatis.BaseMapperX;
import com.ruoyi.mp.mybatis.LambdaQueryWrapperX;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import org.apache.ibatis.annotations.Mapper;
import org.jspecify.annotations.NonNull;
import org.springframework.util.StringUtils;

/**
 * 医生用户 Mapper
 *
 * @author suyh
 */
@Mapper
public interface DoctorUserMapper extends BaseMapperX<DoctorUserEntity> {
    default boolean usernameExists(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }

        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getUserName, username);
        queryWrapper.eq(DoctorUserEntity::getUserType, UserTypeEnums.DOCTOR);

        return exists(queryWrapper);
    }

    default boolean phoneExists(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }

        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getPhonenumber, phone);
        queryWrapper.eq(DoctorUserEntity::getUserType, UserTypeEnums.DOCTOR);

        return exists(queryWrapper);
    }

    default DoctorUserEntity selectDoctorByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }

        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getUserName, username);
        queryWrapper.eq(DoctorUserEntity::getUserType, UserTypeEnums.DOCTOR);

        return selectOne(queryWrapper);
    }

    default PageResult<DoctorUserEntity> selectDoctorPage(
            PageParam pageParam, @NonNull DoctorUserQuery query) {
        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getUserType, UserTypeEnums.DOCTOR);
        queryWrapper.likeIfPresent(DoctorUserEntity::getNickName, query.getName());
        queryWrapper.likeIfPresent(DoctorUserEntity::getPhonenumber, query.getPhone());
        queryWrapper.eqIfPresent(DoctorUserEntity::getStatus, query.getStatus());
        queryWrapper.orderByDesc(DoctorUserEntity::getCreateTime);

        return selectPage(pageParam, queryWrapper);
    }

    default DoctorUserEntity selectDoctorById(Long userId) {
        if (userId == null) {
            return null;
        }
        LambdaQueryWrapperX<DoctorUserEntity> queryWrapper = build();
        queryWrapper.eq(DoctorUserEntity::getUserId, userId);
        queryWrapper.eq(DoctorUserEntity::getUserType, UserTypeEnums.DOCTOR);
        return selectOne(queryWrapper);
    }
}
