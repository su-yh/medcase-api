package com.medcase.system.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.utils.StringUtils;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysPostEntity;
import com.medcase.system.entity.SysUserPostEntity;
import com.medcase.system.mapper.SysPostMapper;
import com.medcase.system.mapper.SysUserPostMapper;
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
    public PageResult<SysPostEntity> selectPage(
            PageParam pageParam, String postCode, String postName, String status) {

        return postMapper.selectPage(pageParam, postCode, postName, status);
    }

    /**
     * 查询所有岗位
     * 
     * @return 岗位列表
     */
    @Override
    public List<SysPostEntity> selectPostAll() {

        return postMapper.selectAllPosts();
    }

    /**
     * 通过岗位ID查询岗位信息
     * 
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPostEntity selectPostById(Long postId) {

        return postMapper.selectById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     * 
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId) {

        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(Long postId, String postName) {

        Long currentPostId = StringUtils.isNull(postId) ? -1L : postId;
        SysPostEntity info = postMapper.selectPostByName(postName);
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != currentPostId.longValue()) {

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
    public boolean checkPostCodeUnique(Long postId, String postCode) {

        Long currentPostId = StringUtils.isNull(postId) ? -1L : postId;
        SysPostEntity info = postMapper.selectPostByCode(postCode);
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != currentPostId.longValue()) {

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

        return postMapper.deleteById(postId);
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

            SysPostEntity post = selectPostById(postId);
            if (countUserPostById(postId) > 0) {

                throw ExceptionUtil.business(ErrorCodeEnums.POST_ASSIGNED_DELETE, post.getPostName());
            }
        }
        return postMapper.deleteByIds(Arrays.asList(postIds));
    }

    /**
     * 新增保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPostEntity post) {

        int row = postMapper.insert(post);
        return row;
    }

    /**
     * 修改保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPostEntity post) {

        return postMapper.updateById(post);
    }
}
