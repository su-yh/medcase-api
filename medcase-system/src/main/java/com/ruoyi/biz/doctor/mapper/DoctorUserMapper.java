package com.ruoyi.biz.doctor.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.biz.doctor.domain.DoctorUserEntity;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mp.mybatis.BaseMapperX;
import com.ruoyi.mp.mybatis.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;
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
        queryWrapper.last("limit 1");

        return selectOne(queryWrapper) != null;
    }

    default DoctorUserEntity selectDoctorByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }

        LambdaQueryWrapper<DoctorUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DoctorUserEntity::getUserName, username);
        queryWrapper.eq(DoctorUserEntity::getUserType, UserTypeEnums.DOCTOR);

        return selectOne(queryWrapper);
    }
}
