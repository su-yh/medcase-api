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
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.audit.AuditOperation;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.system.entity.SysDictTypeEntity;
import com.medcase.system.service.SysDictTypeService;
import com.medcase.web.controller.system.dto.DictTypeQueryRequest;
import com.medcase.web.controller.system.dto.DictTypeSaveRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 数据字典信息
 * 
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {

    @Autowired
    private SysDictTypeService dictTypeService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public PageResult<SysDictTypeEntity> list(PageParam pageParam, DictTypeQueryRequest request) {

        return dictTypeService.selectPage(
                pageParam, request.getDictName(), request.getStatus(), request.getDictType(),
                request.getBeginTime(), request.getEndTime());
    }

    /**
     * 查询字典类型详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public SysDictTypeEntity getInfo(@PathVariable Long dictId) {

        return dictTypeService.selectDictTypeById(dictId);
    }

    /**
     * 新增字典类型
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).CREATE_DICT_TYPE, " +
            "#spelReturnValue, #servletRequest, #loginUser, #request)")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public void add(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @Validated @RequestBody DictTypeSaveRequest request) {

        if (!dictTypeService.checkDictTypeUnique(request.getDictId(), request.getDictType())) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_TYPE_EXISTS);
        }
        SysDictTypeEntity dict = toEntity(request);
        if (dictTypeService.insertDictType(dict) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_OPERATION_FAILED);
        }
    }

    /**
     * 修改字典类型
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).UPDATE_DICT_TYPE, " +
            "#spelReturnValue, #servletRequest, #loginUser, #request)")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public void edit(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @Validated @RequestBody DictTypeSaveRequest request) {

        if (!dictTypeService.checkDictTypeUnique(request.getDictId(), request.getDictType())) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_TYPE_EXISTS);
        }
        SysDictTypeEntity dict = toEntity(request);
        if (dictTypeService.updateDictType(dict) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_OPERATION_FAILED);
        }
    }

    /**
     * 删除字典类型
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).DELETE_DICT_TYPE, " +
            "#spelReturnValue, #servletRequest, #loginUser, #dictIds)")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/{dictIds}")
    public void remove(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @PathVariable Long[] dictIds) {

        dictTypeService.deleteDictTypeByIds(dictIds);
    }

    /**
     * 刷新字典缓存
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).REFRESH_DICT_CACHE, " +
            "#spelReturnValue, #servletRequest, #loginUser)")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/refreshCache")
    public void refreshCache(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser) {

        dictTypeService.resetDictCache();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public List<SysDictTypeEntity> optionselect() {

        List<SysDictTypeEntity> dictTypes = dictTypeService.selectDictTypeAll();
        return dictTypes;
    }

    private SysDictTypeEntity toEntity(DictTypeSaveRequest request) {

        SysDictTypeEntity entity = new SysDictTypeEntity();
        entity.setDictId(request.getDictId());
        entity.setDictName(request.getDictName());
        entity.setDictType(request.getDictType());
        entity.setStatus(request.getStatus());
        entity.setRemark(request.getRemark());
        return entity;
    }
}
