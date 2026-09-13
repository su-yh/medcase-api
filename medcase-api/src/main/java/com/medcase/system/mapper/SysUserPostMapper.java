package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.system.entity.SysUserPostEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.Collection;

@Mapper
public interface SysUserPostMapper extends BaseMapperX<SysUserPostEntity> {
    default Long countByPostId(Long postId) {
        return selectCount(SysUserPostEntity::getPostId, postId);
    }

    default int deleteByUserId(Long userId) {
        LambdaQueryWrapperX<SysUserPostEntity> queryWrapper = build();
        queryWrapper.eq(SysUserPostEntity::getUserId, userId);
        return delete(queryWrapper);
    }

    default int deleteByUserIds(Long[] userIds) {
        LambdaQueryWrapperX<SysUserPostEntity> queryWrapper = build();
        queryWrapper.in(SysUserPostEntity::getUserId, Arrays.asList(userIds));
        return delete(queryWrapper);
    }

    default void insertUserPosts(Collection<SysUserPostEntity> entities) {
        insertBatch(entities);
    }
}
