package com.medcase.system.plus.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.system.plus.entity.SysPostEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysPostMapper extends BaseMapperX<SysPostEntity> {
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
