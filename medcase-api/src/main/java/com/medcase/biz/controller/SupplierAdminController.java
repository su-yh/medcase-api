package com.medcase.biz.controller;

import com.medcase.biz.domain.SupplierEntity;
import com.medcase.biz.request.CasePageRequest;
import com.medcase.biz.request.SupplierQuery;
import com.medcase.biz.request.SupplierSaveRequest;
import com.medcase.biz.request.SupplierStatusRequest;
import com.medcase.biz.request.UserQuery;
import com.medcase.biz.response.SupplierOptionResponse;
import com.medcase.biz.response.SupplierResponse;
import com.medcase.biz.response.UserVO;
import com.medcase.biz.response.CaseVO;
import com.medcase.biz.service.CaseService;
import com.medcase.biz.service.SupplierService;
import com.medcase.biz.service.UserService;
import com.medcase.common.core.controller.BaseController;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 供应商管理。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/biz/supplier")
public class SupplierAdminController extends BaseController {
    private final SupplierService supplierService;
    private final UserService userService;
    private final CaseService caseService;

    @PreAuthorize("@ss.hasPermi('supplier:list')")
    @GetMapping("/list")
    public PageResult<SupplierResponse> list(PageParam pageParam, SupplierQuery query) {
        PageResult<SupplierEntity> page = supplierService.page(pageParam, query);
        PageResult<SupplierResponse> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setList(page.getList().stream()
                .map(SupplierResponse::fromEntity)
                .toList());
        return result;
    }

    @PreAuthorize("@ss.hasPermi('supplier:query')")
    @GetMapping("/{supplierId}")
    public SupplierResponse getInfo(@PathVariable Long supplierId) {
        SupplierEntity entity = supplierService.detail(supplierId);
        return entity == null ? null : SupplierResponse.fromEntity(entity);
    }

    @PreAuthorize("@ss.hasPermi('supplier:add')")
    @PostMapping
    public void add(
            @Valid @RequestBody SupplierSaveRequest request,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {
        supplierService.create(request, loginUser.getUsername());
    }

    @PreAuthorize("@ss.hasPermi('supplier:edit')")
    @PutMapping
    public void edit(
            @Valid @RequestBody SupplierSaveRequest request,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {
        supplierService.update(request, loginUser.getUsername());
    }

    @PreAuthorize("@ss.hasPermi('supplier:status')")
    @PutMapping("/{supplierId}/status")
    public void updateStatus(
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierStatusRequest request,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {
        supplierService.updateStatus(supplierId, request, loginUser.getUsername());
    }

    @PreAuthorize("@ss.hasPermi('supplier:query')")
    @GetMapping("/{supplierId}/users")
    public PageResult<UserVO> userList(
            @PathVariable Long supplierId, PageParam pageParam, UserQuery query) {
        query.setSupplierId(supplierId);
        return userService.page(pageParam, query);
    }

    @PreAuthorize("@ss.hasPermi('supplier:query')")
    @GetMapping("/{supplierId}/users/{userId}/cases")
    public PageResult<CaseVO> caseList(
            @PathVariable Long supplierId,
            @PathVariable Long userId,
            PageParam pageParam,
            CasePageRequest request) {
        SupplierEntity supplier = supplierService.detail(supplierId);
        if (supplier == null) {
            return PageResult.empty();
        }
        UserVO user = userService.detailAny(userId);
        if (user == null || user.getSupplierId() == null || !user.getSupplierId().equals(supplierId)) {
            return PageResult.empty();
        }
        return caseService.pageByUser(userId, pageParam, request);
    }
}
