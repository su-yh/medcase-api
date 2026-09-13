package com.medcase.system.service;

import com.medcase.common.constant.UserConstants;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.event.UserAvatarUploadedEvent;
import com.medcase.system.entity.SysDeptEntity;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.entity.SysUserPostEntity;
import com.medcase.system.entity.SysUserRoleEntity;
import com.medcase.system.entity.SysPostEntity;
import com.medcase.system.mapper.SysPostMapper;
import com.medcase.system.mapper.SysUserMapper;
import com.medcase.system.mapper.SysUserPostMapper;
import com.medcase.system.mapper.SysUserRoleMapper;
import com.medcase.storage.pojo.FileAttachment;
import com.medcase.web.controller.system.dto.UserProfileUpdateRequest;
import com.medcase.web.controller.system.dto.UserQueryRequest;
import com.medcase.web.controller.system.dto.UserResetPasswordRequest;
import com.medcase.web.controller.system.dto.UserSaveRequest;
import com.medcase.web.controller.system.dto.UserStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 * 
 */
@Service
public class SysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public PageResult<SysUserEntity> selectPage(UserQueryRequest user, PageParam pageParam) {
        return selectPage(user, pageParam, null, null);
    }

    public PageResult<SysUserEntity> selectPage(
            UserQueryRequest user, PageParam pageParam, String beginTime, String endTime) {
        useAdminUserTypeIfAbsent(user);
        List<Long> deptIds = null;
        if (user != null && user.getDeptId() != null
                && !Long.valueOf(0L).equals(user.getDeptId())) {
            deptIds = new ArrayList<>();
            deptIds.add(user.getDeptId());
            for (SysDeptEntity department : deptService.all()) {
                if (department.getDeptId() != null
                        && department.getAncestors() != null
                        && Arrays.asList(department.getAncestors().split(","))
                        .contains(String.valueOf(user.getDeptId()))) {
                    deptIds.add(department.getDeptId());
                }
            }
        }
        PageResult<SysUserEntity> result = userMapper.selectUserPage(
                pageParam, user, deptIds, beginTime, endTime);
        for (SysUserEntity item : result.getList()) {
            item.setDept(deptService.selectDeptById(item.getDeptId()));
        }
        return result;
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @param pageParam 分页参数
     * @return 用户信息集合信息
     */
    public PageResult<SysUserEntity> selectAllocatedPage(
            UserQueryRequest user, PageParam pageParam) {
        useAdminUserTypeIfAbsent(user);
        List<Long> deptIds = null;
        if (user != null && user.getDeptId() != null
                && !Long.valueOf(0L).equals(user.getDeptId())) {
            deptIds = new ArrayList<>();
            deptIds.add(user.getDeptId());
            for (SysDeptEntity department : deptService.all()) {
                if (department.getDeptId() != null
                        && department.getAncestors() != null
                        && Arrays.asList(department.getAncestors().split(","))
                        .contains(String.valueOf(user.getDeptId()))) {
                    deptIds.add(department.getDeptId());
                }
            }
        }
        PageResult<SysUserEntity> result = userMapper.selectAllocatedPage(pageParam, user, deptIds);
        for (SysUserEntity item : result.getList()) {
            item.setDept(deptService.selectDeptById(item.getDeptId()));
        }
        return result;
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @param pageParam 分页参数
     * @return 用户信息集合信息
     */
    public PageResult<SysUserEntity> selectUnallocatedPage(
            UserQueryRequest user, PageParam pageParam) {
        useAdminUserTypeIfAbsent(user);
        List<Long> deptIds = null;
        if (user != null && user.getDeptId() != null
                && !Long.valueOf(0L).equals(user.getDeptId())) {
            deptIds = new ArrayList<>();
            deptIds.add(user.getDeptId());
            for (SysDeptEntity department : deptService.all()) {
                if (department.getDeptId() != null
                        && department.getAncestors() != null
                        && Arrays.asList(department.getAncestors().split(","))
                        .contains(String.valueOf(user.getDeptId()))) {
                    deptIds.add(department.getDeptId());
                }
            }
        }
        PageResult<SysUserEntity> result = userMapper.selectUnallocatedPage(pageParam, user, deptIds);
        for (SysUserEntity item : result.getList()) {
            item.setDept(deptService.selectDeptById(item.getDeptId()));
        }
        return result;
    }

    private void useAdminUserTypeIfAbsent(UserQueryRequest user) {
        if (user != null && user.getUserType() == null) {
            user.setUserType(UserTypeEnums.ADMIN);
        }
    }

    private void useAdminUserTypeIfAbsent(UserSaveRequest user) {
        if (user != null && user.getUserType() == null) {
            user.setUserType(UserTypeEnums.ADMIN);
        }
    }

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUserEntity selectUserByUserName(String userName, String userType) {

        SysUserEntity user = userMapper.selectUserByUserName(userName, userType, "0");
        if (user != null) {
            user.setDept(deptService.selectDeptById(user.getDeptId()));
            user.setRoles(roleService.selectRolesByUserId(user.getUserId()));
        }
        return user;
    }

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUserEntity selectUserById(Long userId) {

        SysUserEntity user = userMapper.selectById(userId);
        if (user != null) {
            user.setDept(deptService.selectDeptById(user.getDeptId()));
            user.setRoles(roleService.selectRolesByUserId(user.getUserId()));
        }
        return user;
    }

    /**
     * 查询用户所属角色组
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public String selectUserRoleGroup(Long userId) {
        List<SysRoleEntity> roles = roleService.selectRolesByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) {
            return "";
        }
        List<SysRoleEntity> list = roles.stream()
                .filter(SysRoleEntity::isFlag)
                .toList();
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return list.stream().map(SysRoleEntity::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     * 
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserPostGroup(String userName) {

        List<SysPostEntity> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {

            return "";
        }
        return list.stream().map(SysPostEntity::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     * 
     * @param user 用户信息
     * @return 结果
     */
    public boolean checkUserNameUnique(UserSaveRequest user) {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        useAdminUserTypeIfAbsent(user);
        SysUserEntity info = userMapper.selectUserByUserNameAndType(
                user.getUserName(), user.getUserType(), "0");
        if (info != null && !info.getUserId().equals(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public boolean checkPhoneUnique(UserSaveRequest user) {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        useAdminUserTypeIfAbsent(user);
        SysUserEntity info = userMapper.selectUserByPhoneAndType(
                user.getPhonenumber(), user.getUserType(), "0");
        if (info != null && !info.getUserId().equals(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public boolean checkEmailUnique(UserSaveRequest user) {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        useAdminUserTypeIfAbsent(user);
        SysUserEntity info = userMapper.selectUserByEmailAndType(
                user.getEmail(), user.getUserType(), "0");
        if (info != null && !info.getUserId().equals(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     * 
     * @param user 用户信息
     */
    public void checkUserAllowed(Long userId) {
        if (userId != null && org.springframework.util.ObjectUtils.nullSafeEquals(userId, 1L)) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPER_ADMIN_USER_OPERATION);
        }
    }

    /**
     * 新增保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Transactional
    public int insertUser(UserSaveRequest user) {

        user.setUserType(UserTypeEnums.ADMIN);
        SysUserEntity entity = toEntity(user);
        int rows = userMapper.insert(entity);
        user.setUserId(entity.getUserId());
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 注册用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public boolean registerUser(SysUserEntity user) {

        user.setUserType(UserTypeEnums.ADMIN);
        return userMapper.insert(user) > 0;
    }

    /**
     * 修改保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Transactional
    public int updateUser(UserSaveRequest user) {

        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        return userMapper.updateById(toEntity(user));
    }

    /**
     * 用户授权角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {

        userRoleMapper.deleteByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserStatus(UserStatusRequest user) {

        return userMapper.updateUserStatus(user.getUserId(), user.getStatus());
    }

    /**
     * 修改用户基本信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserProfile(Long userId, UserProfileUpdateRequest request) {

        SysUserEntity user = new SysUserEntity();
        user.setUserId(userId);
        user.setNickName(request.getNickName());
        user.setEmail(request.getEmail());
        user.setPhonenumber(request.getPhonenumber());
        user.setSex(request.getSex());
        return userMapper.updateById(user);
    }

    /**
     * 修改用户头像
     * 
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    public boolean updateUserAvatar(Long userId, FileAttachment avatar) {

        return userMapper.updateUserAvatar(userId, avatar) > 0;
    }

    /**
     * 处理用户头像上传事件，更新用户表中的头像路径。
     */
    @EventListener
    public void handleUserAvatarUploaded(UserAvatarUploadedEvent event) {
        if (!updateUserAvatar(event.getLoginUser().getUserId(), event.getAttachment())) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_AVATAR_UPDATE_FAILED);
        }
    }

    /**
     * 重置用户密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int resetPwd(UserResetPasswordRequest user) {

        return userMapper.resetUserPassword(
                user.getUserId(), user.getPassword(), new Date());
    }

    /**
     * 重置用户密码
     * 
     * @param userId 用户ID
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(Long userId, String password) {

        return userMapper.resetUserPassword(userId, password, new Date());
    }

    /**
     * 新增用户角色信息
     * 
     * @param user 用户对象
     */
    public void insertUserRole(UserSaveRequest user) {

        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户岗位信息
     * 
     * @param user 用户对象
     */
    public void insertUserPost(UserSaveRequest user) {

        Long[] posts = user.getPostIds();
        if (!org.springframework.util.ObjectUtils.isEmpty(posts)) {

            // 新增用户与岗位管理
            List<SysUserPostEntity> list = new ArrayList<>(posts.length);
            for (Long postId : posts) {

                SysUserPostEntity up = new SysUserPostEntity();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            userPostMapper.insertUserPosts(list);
        }
    }

    /**
     * 新增用户角色信息
     * 
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {

        if (!org.springframework.util.ObjectUtils.isEmpty(roleIds)) {

            // 新增用户与角色管理
            List<SysUserRoleEntity> list = new ArrayList<>(roleIds.length);
            for (Long roleId : roleIds) {

                SysUserRoleEntity ur = new SysUserRoleEntity();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.insertUserRoles(list);
        }
    }

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Transactional
    public int deleteUserByIds(Long[] userIds) {

        for (Long userId : userIds) {

            checkUserAllowed(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteByUserIds(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteByUserIds(userIds);
        return userMapper.deleteUsersByIds(userIds);
    }

    private SysUserEntity toEntity(UserSaveRequest user) {
        SysUserEntity entity = new SysUserEntity();
        entity.setUserId(user.getUserId());
        entity.setDeptId(user.getDeptId());
        entity.setUserName(user.getUserName());
        entity.setNickName(user.getNickName());
        entity.setUserType(user.getUserType());
        entity.setEmail(user.getEmail());
        entity.setPhonenumber(user.getPhonenumber());
        entity.setSex(user.getSex());
        entity.setPassword(user.getPassword());
        entity.setStatus(user.getStatus());
        entity.setRemark(user.getRemark());
        return entity;
    }

}
