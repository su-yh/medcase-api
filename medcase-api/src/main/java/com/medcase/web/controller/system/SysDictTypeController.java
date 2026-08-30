package com.medcase.web.controller.system;

import java.util.List;
import com.github.pagehelper.PageInfo;
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
import com.medcase.common.core.domain.entity.SysDictType;
import com.medcase.common.enums.BusinessType;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.service.ISysDictTypeService;

/**
 * 数据字典信息
 * 
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController {

    @Autowired
    private ISysDictTypeService dictTypeService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public PageResult<SysDictType> list(SysDictType dictType) {

        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return new PageResult<>(list, new PageInfo<>(list).getTotal());
    }

    /**
     * 查询字典类型详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public SysDictType getInfo(@PathVariable Long dictId) {

        return dictTypeService.selectDictTypeById(dictId);
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysDictType dict) {

        if (!dictTypeService.checkDictTypeUnique(dict)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_TYPE_EXISTS);
        }
        dict.setCreateBy(getUsername());
        if (dictTypeService.insertDictType(dict) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_OPERATION_FAILED);
        }
    }

    /**
     * 修改字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody SysDictType dict) {

        if (!dictTypeService.checkDictTypeUnique(dict)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_TYPE_EXISTS);
        }
        dict.setUpdateBy(getUsername());
        if (dictTypeService.updateDictType(dict) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_OPERATION_FAILED);
        }
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public void remove(@PathVariable Long[] dictIds) {

        dictTypeService.deleteDictTypeByIds(dictIds);
    }

    /**
     * 刷新字典缓存
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public void refreshCache() {

        dictTypeService.resetDictCache();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public List<SysDictType> optionselect() {

        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return dictTypes;
    }
}
