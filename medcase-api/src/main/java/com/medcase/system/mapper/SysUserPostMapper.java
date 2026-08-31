package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
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
        return delete(build().eq(SysUserPostEntity::getUserId, userId));
    }

    default int deleteByUserIds(Long[] userIds) {
        return delete(build().in(SysUserPostEntity::getUserId, Arrays.asList(userIds)));
    }

    default void insertUserPosts(Collection<SysUserPostEntity> entities) {
        insertBatch(entities);
    }
}
