package com.medcase.biz.service;

import com.medcase.biz.domain.SupplierEntity;
import com.medcase.biz.mapper.SupplierMapper;
import com.medcase.biz.request.SupplierSaveRequest;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SupplierServiceTest {
    @Mock
    private SupplierMapper supplierMapper;

    private SupplierService supplierService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        supplierService = new SupplierService(supplierMapper);
    }

    @Test
    void createRejectsInvalidStatus() {
        SupplierSaveRequest request = validRequest();
        request.setStatus("2");

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> supplierService.create(request, "admin"));

        assertEquals(ErrorCodeEnums.SUPPLIER_STATUS_INVALID, exception.getEc());
        verify(supplierMapper, never()).insert(any(SupplierEntity.class));
    }

    @Test
    void updateRejectsInvalidStatus() {
        SupplierSaveRequest request = validRequest();
        request.setSupplierId(1L);
        request.setStatus("2");
        when(supplierMapper.selectById(1L)).thenReturn(new SupplierEntity());

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> supplierService.update(request, "admin"));

        assertEquals(ErrorCodeEnums.SUPPLIER_STATUS_INVALID, exception.getEc());
        verify(supplierMapper, never()).updateById(any(SupplierEntity.class));
    }

    @Test
    void createRejectsDuplicateName() {
        SupplierSaveRequest request = validRequest();
        when(supplierMapper.existsByName("供应商A", null)).thenReturn(true);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> supplierService.create(request, "admin"));

        assertEquals(ErrorCodeEnums.SUPPLIER_NICKNAME_EXISTS, exception.getEc());
        verify(supplierMapper, never()).insert(any(SupplierEntity.class));
    }

    private SupplierSaveRequest validRequest() {
        SupplierSaveRequest request = new SupplierSaveRequest();
        request.setName("供应商A");
        request.setSex("0");
        request.setPhone("13800000000");
        request.setIdCardNumber("110101199001011234");
        request.setStatus("0");
        return request;
    }
}
