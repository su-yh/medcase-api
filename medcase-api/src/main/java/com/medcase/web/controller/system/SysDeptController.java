package com.medcase.web.controller.system;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
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
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.controller.BaseController;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.enums.BusinessType;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.system.service.SysDeptService;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;

/**
 * 部门信息
 * 
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    @Autowired
    private SysDeptService deptService;

    /**
     * 获取部门列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public List<SysDept> list(SysDept dept) {

        List<SysDept> depts = deptService.selectDeptList(dept);
        return depts;
    }

    /**
     * 查询部门列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public List<SysDept> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {

        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId
                || ArrayUtils.contains(
                org.springframework.util.StringUtils.tokenizeToStringArray(d.getAncestors(), ",", false, true),
                deptId + ""));
        return depts;
    }

    /**
     * 根据部门编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public SysDept getInfo(@PathVariable Long deptId) {

        deptService.checkDeptDataScope(deptId);
        return deptService.selectDeptById(deptId);
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(
            @Validated @RequestBody SysDept dept,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        if (!deptService.checkDeptNameUnique(dept)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_NAME_EXISTS);
        }
        dept.setCreateBy(loginUser.getUsername());
        if (deptService.insertDept(dept) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_OPERATION_FAILED);
        }
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(
            @Validated @RequestBody SysDept dept,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_NAME_EXISTS);
        }
        else if (dept.getParentId().equals(deptId)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_PARENT_SELF);
        }
        else if (org.apache.commons.lang3.Strings.CS.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && deptService.selectNormalChildrenDeptById(deptId) > 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_ENABLED_CHILDREN);
        }
        dept.setUpdateBy(loginUser.getUsername());
        if (deptService.updateDept(dept) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_OPERATION_FAILED);
        }
    }

    /**
     * 保存部门排序
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "保存部门排序", businessType = BusinessType.UPDATE)
    @PutMapping("/updateSort")
    public void updateSort(@RequestBody Map<String, String> params) {

        String[] deptIds = params.get("deptIds").split(",");
        String[] orderNums = params.get("orderNums").split(",");
        deptService.updateDeptSort(deptIds, orderNums);
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public void remove(@PathVariable Long deptId) {

        if (deptService.hasChildByDeptId(deptId)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_HAS_CHILDREN);
        }
        if (deptService.checkDeptExistUser(deptId)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_HAS_USERS);
        }
        deptService.checkDeptDataScope(deptId);
        if (deptService.deleteDeptById(deptId) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_OPERATION_FAILED);
        }
    }
}
