package com.medcase.system.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.TreeSelect;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.core.text.Convert;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysDeptEntity;
import com.medcase.system.mapper.SysDeptMapper;
import com.medcase.system.mapper.SysUserMapper;
import com.medcase.web.controller.system.dto.DeptQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 * 
 */
@Service
public class SysDeptService {

    private static final String ALL_DEPARTMENTS_CACHE_KEY = "allDepartments";

    private static final long DEPT_CACHE_EXPIRE_MINUTES = 30L;

    private final Cache<String, List<SysDeptEntity>> deptCache = Caffeine.newBuilder()
            .expireAfterWrite(DEPT_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .build();

    private final Object deptCacheLoadLock = new Object();

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询部门管理数据
     * 
     * @param query 部门查询条件
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptList(DeptQueryRequest query) {

        List<SysDeptEntity> departments = all();
        List<SysDept> result = new ArrayList<>();
        for (SysDeptEntity department : departments) {
            if (!matchesDeptQuery(department, query)) {
                continue;
            }
            result.add(SystemEntityConverter.toDomain(department));
        }
        return result;
    }

    /**
     * 查询部门树结构信息
     * 
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    public List<TreeSelect> selectDeptTreeList(SysDept dept) {

        DeptQueryRequest query = new DeptQueryRequest();
        if (dept != null) {
            query.setDeptNameLike(dept.getDeptName());
            query.setStatus(dept.getStatus());
        }
        List<SysDept> depts = selectDeptList(query);
        return buildDeptTreeSelect(depts);
    }

    private boolean matchesDeptQuery(SysDeptEntity department, DeptQueryRequest query) {

        if (query == null) {
            return true;
        }
        if (org.springframework.util.StringUtils.hasText(query.getDeptNameLike())
                && (department.getDeptName() == null
                || !department.getDeptName().contains(query.getDeptNameLike()))) {
            return false;
        }
        return !org.springframework.util.StringUtils.hasText(query.getStatus())
                || query.getStatus().equals(department.getStatus());
    }

    /**
     * 构建前端所需要树结构
     * 
     * @param depts 部门列表
     * @return 树结构列表
     */
    public List<SysDept> buildDeptTree(List<SysDept> depts) {

        List<SysDept> returnList = new ArrayList<SysDept>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts) {

            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {

                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {

            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts) {

        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据部门ID查询信息
     * 
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SysDept selectDeptById(Long deptId) {
        if (deptId == null) {
            return null;
        }

        return all().stream()
                .filter(dept -> deptId.equals(dept.getDeptId()))
                .findFirst()
                .map(SystemEntityConverter::toDomain)
                .orElse(null);
    }

    public List<SysDeptEntity> all() {
        List<SysDeptEntity> departments = deptCache.getIfPresent(ALL_DEPARTMENTS_CACHE_KEY);
        if (departments != null) {
            return departments;
        }

        synchronized (deptCacheLoadLock) {
            departments = deptCache.getIfPresent(ALL_DEPARTMENTS_CACHE_KEY);
            if (departments != null) {
                return departments;
            }

            departments = deptMapper.selectAllDepartments();
            if (departments == null) {
                departments = List.of();
            }
            deptCache.put(ALL_DEPARTMENTS_CACHE_KEY, departments);
        }

        return departments;
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     * 
     * @param deptId 部门ID
     * @return 子部门数
     */
    public int selectNormalChildrenDeptById(Long deptId) {

        if (deptId == null) {
            return 0;
        }
        return Math.toIntExact(all().stream()
                .filter(dept -> UserConstants.DEPT_NORMAL.equals(dept.getStatus()))
                .filter(dept -> dept.getAncestors() != null)
                .filter(dept -> Arrays.asList(dept.getAncestors().split(","))
                        .contains(String.valueOf(deptId)))
                .count());
    }

    /**
     * 是否存在子节点
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    public boolean hasChildByDeptId(Long deptId) {

        if (deptId == null) {
            return false;
        }
        return all().stream()
                .anyMatch(dept -> deptId.equals(dept.getParentId()));
    }

    /**
     * 查询部门是否存在用户
     * 
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkDeptExistUser(Long deptId) {

        int result = Math.toIntExact(userMapper.countByDeptId(deptId));
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     * 
     * @param dept 部门信息
     * @return 结果
     */
    public boolean checkDeptNameUnique(SysDept dept) {

        boolean exists = all().stream()
                .anyMatch(item -> java.util.Objects.equals(item.getDeptName(), dept.getDeptName())
                        && java.util.Objects.equals(item.getParentId(), dept.getParentId())
                        && !java.util.Objects.equals(item.getDeptId(), dept.getDeptId()));
        return exists ? UserConstants.NOT_UNIQUE : UserConstants.UNIQUE;
    }

    /**
     * 新增保存部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(SysDept dept) {

        SysDept info = SystemEntityConverter.toDomain(deptMapper.selectById(dept.getParentId()));
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {

            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_DISABLED);
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        SysDeptEntity entity = SystemEntityConverter.toEntity(dept);
        int row = deptMapper.insert(entity);
        dept.setDeptId(entity.getDeptId());
        if (row > 0) {

            synchronized (deptCacheLoadLock) {

                deptCache.invalidate(ALL_DEPARTMENTS_CACHE_KEY);
            }
        }
        return row;
    }

    /**
     * 修改保存部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(SysDept dept) {

        SysDept newParentDept = SystemEntityConverter.toDomain(
                deptMapper.selectById(dept.getParentId()));
        SysDept oldDept = SystemEntityConverter.toDomain(
                deptMapper.selectById(dept.getDeptId()));
        if (newParentDept != null && oldDept != null) {

            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = deptMapper.updateById(SystemEntityConverter.toEntity(dept));
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())
                && org.springframework.util.StringUtils.hasText(dept.getAncestors())
                && !"0".equals(dept.getAncestors())) {

            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        if (result > 0) {

            synchronized (deptCacheLoadLock) {

                deptCache.invalidate(ALL_DEPARTMENTS_CACHE_KEY);
            }
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     * 
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {

        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);
        deptMapper.updateParentStatusNormal(Arrays.asList(deptIds));
    }

    /**
     * 修改子元素关系
     * 
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {

        List<SysDept> children = SystemEntityConverter.copyList(
                deptMapper.selectChildrenByDeptId(deptId),
                SysDept.class);
        for (SysDept child : children) {

            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {

            for (SysDept child : children) {

                deptMapper.updateDeptAncestors(child.getDeptId(), child.getAncestors());
            }
        }
    }

    /**
     * 保存部门排序
     *
     * @param deptIds 部门ID数组
     * @param orderNums 排序数组
     */
    @Transactional
    public void updateDeptSort(String[] deptIds, String[] orderNums) {

        try {

            for (int i = 0; i < deptIds.length; i++) {

                deptMapper.updateDeptSort(
                        Convert.toLong(deptIds[i]), Convert.toInt(orderNums[i]));
            }
            synchronized (deptCacheLoadLock) {

                deptCache.invalidate(ALL_DEPARTMENTS_CACHE_KEY);
            }
        }
        catch (Exception e) {

            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_SORT_SAVE_FAILED);
        }
    }

    /**
     * 删除部门管理信息
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    public int deleteDeptById(Long deptId) {

        int result = deptMapper.deleteDeptById(deptId);
        if (result > 0) {

            synchronized (deptCacheLoadLock) {

                deptCache.invalidate(ALL_DEPARTMENTS_CACHE_KEY);
            }
        }
        return result;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {

        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {

            if (hasChild(list, tChild)) {

                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {

        List<SysDept> tlist = new ArrayList<SysDept>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext()) {

            SysDept n = (SysDept) it.next();
            if (n.getParentId() != null && n.getParentId().longValue() == t.getDeptId().longValue()) {

                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {

        return getChildList(list, t).size() > 0;
    }
}
