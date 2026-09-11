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
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.audit.AuditOperation;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.system.entity.SysPostEntity;
import com.medcase.system.service.SysPostService;
import com.medcase.web.controller.system.dto.PostQueryRequest;
import com.medcase.web.controller.system.dto.PostResponse;
import com.medcase.web.controller.system.dto.PostSaveRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 岗位信息操作处理
 * 
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController {

    @Autowired
    private SysPostService postService;

    /**
     * 获取岗位列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public PageResult<PostResponse> list(PageParam pageParam, PostQueryRequest request) {

        PageResult<SysPostEntity> entityPage = postService.selectPage(
                pageParam, request.getPostCode(), request.getPostName(), request.getStatus());
        PageResult<PostResponse> result = new PageResult<>();
        result.setList(entityPage.getList().stream()
                .map(PostResponse::fromEntity)
                .toList());
        result.setTotal(entityPage.getTotal());
        return result;
    }
    
    /**
     * 根据岗位编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public PostResponse getInfo(@PathVariable Long postId) {

        return PostResponse.fromEntity(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).CREATE_POST, " +
            "#spelReturnValue, #servletRequest, #loginUser, #request)")
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @PostMapping
    public void add(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @Validated @RequestBody PostSaveRequest request) {

        if (!postService.checkPostNameUnique(request.getPostId(), request.getPostName())) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_NAME_EXISTS);
        }
        else if (!postService.checkPostCodeUnique(request.getPostId(), request.getPostCode())) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_CODE_EXISTS);
        }
        SysPostEntity post = toEntity(request);
        if (postService.insertPost(post) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_OPERATION_FAILED);
        }
    }

    /**
     * 修改岗位
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).UPDATE_POST, " +
            "#spelReturnValue, #servletRequest, #loginUser, #request)")
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @PutMapping
    public void edit(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @Validated @RequestBody PostSaveRequest request) {

        if (!postService.checkPostNameUnique(request.getPostId(), request.getPostName())) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_NAME_EXISTS);
        }
        else if (!postService.checkPostCodeUnique(request.getPostId(), request.getPostCode())) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_CODE_EXISTS);
        }
        SysPostEntity post = toEntity(request);
        if (postService.updatePost(post) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_OPERATION_FAILED);
        }
    }

    /**
     * 删除岗位
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).DELETE_POST, " +
            "#spelReturnValue, #servletRequest, #loginUser, #postIds)")
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @DeleteMapping("/{postIds}")
    public void remove(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @PathVariable Long[] postIds) {

        if (postService.deletePostByIds(postIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.POST_OPERATION_FAILED);
        }
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public List<PostResponse> optionselect() {

        return postService.selectPostAll().stream()
                .map(PostResponse::fromEntity)
                .toList();
    }

    private SysPostEntity toEntity(PostSaveRequest request) {
        SysPostEntity entity = new SysPostEntity();
        entity.setPostId(request.getPostId());
        entity.setPostCode(request.getPostCode());
        entity.setPostName(request.getPostName());
        entity.setPostSort(request.getPostSort());
        entity.setStatus(request.getStatus());
        entity.setRemark(request.getRemark());
        return entity;
    }
}
