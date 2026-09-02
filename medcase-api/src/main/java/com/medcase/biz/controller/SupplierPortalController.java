package com.medcase.biz.controller;

import com.medcase.biz.response.SupplierOptionResponse;
import com.medcase.biz.service.SupplierService;
import com.medcase.common.annotation.Anonymous;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 供应商公开选项接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/biz/supplier")
public class SupplierPortalController {
    private final SupplierService supplierService;

    @Anonymous
    @GetMapping("/options")
    public List<SupplierOptionResponse> options() {
        return supplierService.options().stream()
                .map(SupplierOptionResponse::fromEntity)
                .toList();
    }
}
