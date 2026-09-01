package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysPostEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysPostMapper extends BaseMapperX<SysPostEntity> {
    default PageResult<SysPostEntity> selectPage(
            PageParam pageParam, String postCode, String postName, String status) {
        return selectPage(pageParam, build()
                .likeIfPresent(SysPostEntity::getPostCode, postCode)
                .likeIfPresent(SysPostEntity::getPostName, postName)
                .eqIfPresent(SysPostEntity::getStatus, status));
    }

    List<Long> selectPostListByUserId(Long userId);

    List<SysPostEntity> selectPostsByUserName(String userName);

    default List<SysPostEntity> selectAllPosts() {
        return selectList(build());
    }

    default SysPostEntity selectPostByName(String postName) {
        return selectOne(build().eq(SysPostEntity::getPostName, postName));
    }

    default SysPostEntity selectPostByCode(String postCode) {
        return selectOne(build().eq(SysPostEntity::getPostCode, postCode));
    }

}
