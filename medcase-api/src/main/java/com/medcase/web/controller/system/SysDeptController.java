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
import com.medcase.common.constant.HttpStatus;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.controller.BaseController;
import com.medcase.mvc.response.R;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.enums.BusinessType;
import com.medcase.common.utils.StringUtils;
import com.medcase.system.service.ISysDeptService;

/**
 * 部门信息
 * 
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public R<List<SysDept>> list(SysDept dept) {

        List<SysDept> depts = deptService.selectDeptList(dept);
        return R.ofSuccess(depts);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public R<List<SysDept>> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {

        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return R.ofSuccess(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public R<SysDept> getInfo(@PathVariable Long deptId) {

        deptService.checkDeptDataScope(deptId);
        return R.ofSuccess(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDept dept) {

        if (!deptService.checkDeptNameUnique(dept)) {

            return R.ofFail("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(getUsername());
        return deptService.insertDept(dept) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDept dept) {

        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept)) {

            return R.ofFail("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if (dept.getParentId().equals(deptId)) {

            return R.ofFail("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0) {

            return R.ofFail("该部门包含未停用的子部门！");
        }
        dept.setUpdateBy(getUsername());
        return deptService.updateDept(dept) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    /**
     * 保存部门排序
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "保存部门排序", businessType = BusinessType.UPDATE)
    @PutMapping("/updateSort")
    public R<Void> updateSort(@RequestBody Map<String, String> params) {

        String[] deptIds = params.get("deptIds").split(",");
        String[] orderNums = params.get("orderNums").split(",");
        deptService.updateDeptSort(deptIds, orderNums);
        return R.ofSuccess();
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public R<Void> remove(@PathVariable Long deptId) {

        if (deptService.hasChildByDeptId(deptId)) {

            return R.ofFail(HttpStatus.WARN, "存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {

            return R.ofFail(HttpStatus.WARN, "部门存在用户,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        return deptService.deleteDeptById(deptId) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }
}
