package com.medcase.system.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.utils.StringUtils;
import com.medcase.system.domain.SysPost;
import com.medcase.system.plus.SystemEntityConverter;
import com.medcase.system.plus.entity.SysPostEntity;
import com.medcase.system.plus.entity.SysUserPostEntity;
import com.medcase.system.plus.mapper.SysPostMapper;
import com.medcase.system.plus.mapper.SysUserPostMapper;
import com.medcase.system.service.ISysPostService;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;

/**
 * 岗位信息 服务层处理
 * 
 */
@Service
public class SysPostServiceImpl implements ISysPostService {

    @Autowired
    private com.medcase.system.mapper.SysPostHistoryMapper postHistoryMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    /**
     * 查询岗位信息集合
     * 
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post) {

        return postHistoryMapper.selectPostList(post);
    }

    /**
     * 查询所有岗位
     * 
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll() {

        return SystemEntityConverter.copyList(postMapper.selectAllPosts(), SysPost.class);
    }

    /**
     * 通过岗位ID查询岗位信息
     * 
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId) {

        return SystemEntityConverter.toDomain(postMapper.selectPostById(postId));
    }

    /**
     * 根据用户ID获取岗位选择框列表
     * 
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId) {

        return postHistoryMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(SysPost post) {

        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = SystemEntityConverter.toDomain(
                postMapper.selectPostByName(post.getPostName()));
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {

            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostCodeUnique(SysPost post) {

        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = SystemEntityConverter.toDomain(
                postMapper.selectPostByCode(post.getPostCode()));
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {

            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId) {

        return Math.toIntExact(userPostMapper.countByPostId(postId));
    }

    /**
     * 删除岗位信息
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId) {

        return postMapper.deletePostById(postId);
    }

    /**
     * 批量删除岗位信息
     * 
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(Long[] postIds) {

        for (Long postId : postIds) {

            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0) {

                throw ExceptionUtil.business(ErrorCodeEnums.POST_ASSIGNED_DELETE, post.getPostName());
            }
        }
        return postMapper.deletePostsByIds(postIds);
    }

    /**
     * 新增保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post) {

        SysPostEntity entity = SystemEntityConverter.toEntity(post);
        int row = postMapper.insertPost(entity);
        post.setPostId(entity.getPostId());
        return row;
    }

    /**
     * 修改保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post) {

        return postMapper.updatePost(SystemEntityConverter.toEntity(post));
    }
}
