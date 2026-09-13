package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysPostEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysPostMapper extends BaseMapperX<SysPostEntity> {
    default PageResult<SysPostEntity> selectPage(
            PageParam pageParam, String postCode, String postName, String status) {
        LambdaQueryWrapperX<SysPostEntity> queryWrapper = build();
        queryWrapper.likeIfPresent(SysPostEntity::getPostCode, postCode);
        queryWrapper.likeIfPresent(SysPostEntity::getPostName, postName);
        queryWrapper.eqIfPresent(SysPostEntity::getStatus, status);
        return selectPage(pageParam, queryWrapper);
    }

    List<Long> selectPostListByUserId(Long userId);

    List<SysPostEntity> selectPostsByUserName(String userName);

    default List<SysPostEntity> selectAllPosts() {
        LambdaQueryWrapperX<SysPostEntity> queryWrapper = build();
        return selectList(queryWrapper);
    }

    default SysPostEntity selectPostByName(String postName) {
        LambdaQueryWrapperX<SysPostEntity> queryWrapper = build();
        queryWrapper.eq(SysPostEntity::getPostName, postName);
        return selectOne(queryWrapper);
    }

    default SysPostEntity selectPostByCode(String postCode) {
        LambdaQueryWrapperX<SysPostEntity> queryWrapper = build();
        queryWrapper.eq(SysPostEntity::getPostCode, postCode);
        return selectOne(queryWrapper);
    }

}
