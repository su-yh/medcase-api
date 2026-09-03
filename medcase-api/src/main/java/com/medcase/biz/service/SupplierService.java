package com.medcase.biz.service;

import com.medcase.biz.domain.SupplierEntity;
import com.medcase.biz.enums.SupplierStatusEnums;
import com.medcase.biz.mapper.SupplierMapper;
import com.medcase.biz.request.SupplierQuery;
import com.medcase.biz.request.SupplierSaveRequest;
import com.medcase.biz.request.SupplierStatusRequest;
import com.medcase.common.utils.StringUtils;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 供应商业务。
 */
@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierMapper supplierMapper;

    public PageResult<SupplierEntity> page(PageParam pageParam, SupplierQuery query) {
        return supplierMapper.selectPage(pageParam, query);
    }

    public SupplierEntity detail(Long supplierId) {
        return supplierMapper.selectById(supplierId);
    }

    public List<SupplierEntity> options() {
        return supplierMapper.selectEnabledList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(SupplierSaveRequest request, String username) {
        validateStatus(request.getStatus());
        String name = request.getName().trim();
        ensureNameUnique(null, name);

        SupplierEntity entity = toEntity(request);
        entity.setName(name);
        entity.setCreateBy(username);
        if (supplierMapper.insert(entity) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPPLIER_OPERATION_FAILED);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(SupplierSaveRequest request, String username) {
        if (request.getSupplierId() == null || supplierMapper.selectById(request.getSupplierId()) == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPPLIER_NOT_FOUND);
        }

        validateStatus(request.getStatus());
        String name = request.getName().trim();
        ensureNameUnique(request.getSupplierId(), name);

        SupplierEntity entity = toEntity(request);
        entity.setName(name);
        entity.setUpdateBy(username);
        if (supplierMapper.updateById(entity) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPPLIER_OPERATION_FAILED);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long supplierId, SupplierStatusRequest request, String username) {
        if (supplierMapper.selectById(supplierId) == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPPLIER_NOT_FOUND);
        }
        if (request.getStatus() == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPPLIER_STATUS_INVALID);
        }

        SupplierEntity entity = new SupplierEntity();
        entity.setId(supplierId);
        entity.setStatus(request.getStatus());
        entity.setUpdateBy(username);
        if (supplierMapper.updateById(entity) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPPLIER_STATUS_UPDATE_FAILED);
        }
    }

    private void ensureNameUnique(Long supplierId, String name) {
        if (supplierMapper.existsByName(name, supplierId)) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPPLIER_NICKNAME_EXISTS, name);
        }
    }

    private void validateStatus(SupplierStatusEnums status) {
        if (status == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPPLIER_STATUS_INVALID);
        }
    }

    private SupplierEntity toEntity(SupplierSaveRequest request) {
        SupplierEntity entity = new SupplierEntity();
        entity.setId(request.getSupplierId());
        entity.setSex(request.getSex());
        entity.setPhonenumber(request.getPhone());
        entity.setEmail(StringUtils.hasText(request.getEmail()) ? request.getEmail().trim() : null);
        entity.setIdCardNumber(request.getIdCardNumber());
        entity.setStatus(request.getStatus());
        entity.setRemark(request.getRemark());
        return entity;
    }
}
