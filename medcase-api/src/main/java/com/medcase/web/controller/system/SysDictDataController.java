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
import com.medcase.common.core.controller.BaseController;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.common.enums.BusinessType;
import com.medcase.common.utils.StringUtils;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.service.ISysDictDataService;
import com.medcase.system.service.ISysDictTypeService;

/**
 * 数据字典信息
 * 
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {

    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public PageResult<SysDictData> list(PageParam pageParam, SysDictData dictData) {

        return dictDataService.selectPage(pageParam, dictData);
    }

    /**
     * 查询字典数据详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public SysDictData getInfo(@PathVariable Long dictCode) {

        return dictDataService.selectDictDataById(dictCode);
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public List<SysDictData> dictType(@PathVariable String dictType) {

        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data)) {

            data = new ArrayList<SysDictData>();
        }
        return data;
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysDictData dict) {

        dict.setCreateBy(getUsername());
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
    public void edit(@Validated @RequestBody SysDictData dict) {

        dict.setUpdateBy(getUsername());
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
}
