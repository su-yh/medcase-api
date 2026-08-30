package com.medcase.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Log;
import com.medcase.common.core.controller.BaseController;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.enums.BusinessType;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.domain.SysPost;
import com.medcase.system.service.ISysPostService;

/**
 * 岗位信息操作处理
 * 
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController {

    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public PageResult<SysPost> list(SysPost post) {

        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return getPageResult(list);
    }
    
    /**
     * 根据岗位编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public SysPost getInfo(@PathVariable Long postId) {

        return postService.selectPostById(postId);
    }

    /**
     * 新增岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysPost post) {

        if (!postService.checkPostNameUnique(post)) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_NAME_EXISTS);
        }
        else if (!postService.checkPostCodeUnique(post)) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_CODE_EXISTS);
        }
        post.setCreateBy(getUsername());
        if (postService.insertPost(post) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_OPERATION_FAILED);
        }
    }

    /**
     * 修改岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody SysPost post) {

        if (!postService.checkPostNameUnique(post)) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_NAME_EXISTS);
        }
        else if (!postService.checkPostCodeUnique(post)) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_CODE_EXISTS);
        }
        post.setUpdateBy(getUsername());
        if (postService.updatePost(post) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_OPERATION_FAILED);
        }
    }

    /**
     * 删除岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public void remove(@PathVariable Long[] postIds) {

        if (postService.deletePostByIds(postIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_OPERATION_FAILED);
        }
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public List<SysPost> optionselect() {

        List<SysPost> posts = postService.selectPostAll();
        return posts;
    }
}
