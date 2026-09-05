package com.medcase.web.controller.system;

import java.util.ArrayList;
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
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.enums.BusinessType;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.service.SysDictDataService;
import com.medcase.web.controller.system.dto.DictDataQueryRequest;
import com.medcase.web.controller.system.dto.DictDataSaveRequest;

/**
 * 数据字典信息
 * 
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    @Autowired
    private SysDictDataService dictDataService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public PageResult<SysDictDataEntity> list(PageParam pageParam, DictDataQueryRequest request) {

        return dictDataService.selectPage(
                pageParam, request.getDictType(), request.getDictLabel(), request.getStatus());
    }

    /**
     * 查询字典数据详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public SysDictDataEntity getInfo(@PathVariable Long dictCode) {

        return dictDataService.selectDictDataById(dictCode);
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public List<SysDictDataEntity> dictType(@PathVariable String dictType) {

        List<SysDictDataEntity> data = dictDataService.selectDictDataByType(dictType);
        if (data == null) {

            data = new ArrayList<>();
        }
        return data;
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(
            @Validated @RequestBody DictDataSaveRequest request,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        SysDictDataEntity dict = toEntity(request);
        dict.setCreateBy(loginUser.getUsername());
        if (dictDataService.insertDictData(dict) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_OPERATION_FAILED);
        }
    }

    /**
     * 修改保存字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(
            @Validated @RequestBody DictDataSaveRequest request,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        SysDictDataEntity dict = toEntity(request);
        dict.setUpdateBy(loginUser.getUsername());
        if (dictDataService.updateDictData(dict) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DICT_OPERATION_FAILED);
        }
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public void remove(@PathVariable Long[] dictCodes) {

        dictDataService.deleteDictDataByIds(dictCodes);
    }

    private SysDictDataEntity toEntity(DictDataSaveRequest request) {

        SysDictDataEntity entity = new SysDictDataEntity();
        entity.setDictCode(request.getDictCode());
        entity.setDictSort(request.getDictSort());
        entity.setDictLabel(request.getDictLabel());
        entity.setDictValue(request.getDictValue());
        entity.setDictType(request.getDictType());
        entity.setCssClass(request.getCssClass());
        entity.setListClass(request.getListClass());
        entity.setIsDefault(request.getIsDefault());
        entity.setStatus(request.getStatus());
        entity.setRemark(request.getRemark());
        return entity;
    }
}
