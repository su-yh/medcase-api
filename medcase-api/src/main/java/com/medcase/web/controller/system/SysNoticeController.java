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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Log;
import com.medcase.common.core.controller.BaseController;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.core.text.Convert;
import com.medcase.common.enums.BusinessType;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.system.entity.SysNoticeEntity;
import com.medcase.system.service.SysNoticeReadService;
import com.medcase.system.service.SysNoticeService;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.web.controller.system.dto.NoticeQueryRequest;
import com.medcase.web.controller.system.dto.NoticeReadUserResponse;
import com.medcase.web.controller.system.dto.NoticeResponse;
import com.medcase.web.controller.system.dto.NoticeSaveRequest;
import com.medcase.web.controller.system.dto.NoticeTopItemResponse;
import com.medcase.web.controller.system.dto.NoticeTopResponse;

/**
 * 公告 信息操作处理
 * 
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController {

    @Autowired
    private SysNoticeService noticeService;

    @Autowired
    private SysNoticeReadService noticeReadService;

    /**
     * 获取通知公告列表
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    public PageResult<NoticeResponse> list(PageParam pageParam, NoticeQueryRequest request) {

        PageResult<SysNoticeEntity> entityPage = noticeService.selectPage(
                pageParam, request.getNoticeTitle(), request.getNoticeType(), request.getCreateBy());
        PageResult<NoticeResponse> result = new PageResult<>();
        result.setList(entityPage.getList().stream()
                .map(NoticeResponse::fromEntity)
                .toList());
        result.setTotal(entityPage.getTotal());
        return result;
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @GetMapping(value = "/{noticeId}")
    public NoticeResponse getInfo(@PathVariable Long noticeId) {

        return NoticeResponse.fromEntity(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(
            @Validated @RequestBody NoticeSaveRequest request,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        SysNoticeEntity notice = toEntity(request);
        notice.setCreateBy(loginUser.getUsername());
        if (noticeService.insertNotice(notice) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.NOTICE_OPERATION_FAILED);
        }
    }

    /**
     * 修改通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(
            @Validated @RequestBody NoticeSaveRequest request,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        SysNoticeEntity notice = toEntity(request);
        notice.setUpdateBy(loginUser.getUsername());
        if (noticeService.updateNotice(notice) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.NOTICE_OPERATION_FAILED);
        }
    }

    /**
     * 首页顶部公告列表（返回全部正常公告，带当前用户已读标记，最多5条）
     */
    @GetMapping("/listTop")
    @ResponseBody
    public NoticeTopResponse listTop(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        List<NoticeTopItemResponse> list = noticeReadService.selectNoticeListWithReadStatus(
                loginUser.getUserId(), 5);
        long unreadCount = list.stream()
                .filter(item -> !item.isRead())
                .count();
        return new NoticeTopResponse(list, unreadCount);
    }

    /**
     * 标记公告已读
     */
    @PostMapping("/markRead")
    @ResponseBody
    public void markRead(
            Long noticeId,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        noticeReadService.markRead(noticeId, loginUser.getUserId());
    }

    /**
     * 批量标记已读
     */
    @PostMapping("/markReadAll")
    @ResponseBody
    public void markReadAll(
            String ids,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        Long[] noticeIds = Convert.toLongArray(ids);
        noticeReadService.markReadBatch(loginUser.getUserId(), noticeIds);
    }

    /**
     * 已读用户列表数据
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/readUsers/list")
    @ResponseBody
    public PageResult<NoticeReadUserResponse> readUsersList(
            PageParam pageParam, Long noticeId,
            String searchValue) {

        List<NoticeReadUserResponse> list = noticeReadService.selectReadUsersByNoticeId(
                noticeId, searchValue);
        return new PageResult<>(PageParam.doPageList(pageParam, list), (long) list.size());
    }

    /**
     * 删除通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public void remove(@PathVariable Long[] noticeIds) {

        noticeReadService.deleteByNoticeIds(noticeIds);
        if (noticeService.deleteNoticeByIds(noticeIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.NOTICE_OPERATION_FAILED);
        }
    }

    private SysNoticeEntity toEntity(NoticeSaveRequest request) {
        SysNoticeEntity entity = new SysNoticeEntity();
        entity.setNoticeId(request.getNoticeId());
        entity.setNoticeTitle(request.getNoticeTitle());
        entity.setNoticeType(request.getNoticeType());
        entity.setNoticeContent(request.getNoticeContent());
        entity.setStatus(request.getStatus());
        return entity;
    }
}
