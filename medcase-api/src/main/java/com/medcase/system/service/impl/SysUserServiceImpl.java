package com.medcase.system.service.impl;

import com.medcase.common.annotation.DataScope;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.common.utils.StringUtils;
import com.medcase.common.utils.spring.SpringUtils;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.event.UserAvatarUploadedEvent;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.entity.SysUserPostEntity;
import com.medcase.system.entity.SysUserRoleEntity;
import com.medcase.system.entity.SysPostEntity;
import com.medcase.system.mapper.SysPostMapper;
import com.medcase.system.mapper.SysRoleMapper;
import com.medcase.system.mapper.SysUserMapper;
import com.medcase.system.mapper.SysUserPostMapper;
import com.medcase.system.mapper.SysUserRoleMapper;
import com.medcase.system.service.ISysDeptService;
import com.medcase.system.service.ISysUserService;
import com.medcase.storage.pojo.FileAttachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 * 
 */
@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public PageResult<SysUser> selectPage(SysUser user, PageParam pageParam) {
        useAdminUserTypeIfAbsent(user);
        return userMapper.selectPage(pageParam, user);
    }

    /**
     * 根据条件查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user) {
        useAdminUserTypeIfAbsent(user);
        return userMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user) {
        useAdminUserTypeIfAbsent(user);
        return userMapper.selectAllocatedList(user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUnallocatedList(SysUser user) {
        useAdminUserTypeIfAbsent(user);
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @param pageParam 分页参数
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public PageResult<SysUser> selectAllocatedPage(SysUser user, PageParam pageParam) {
        useAdminUserTypeIfAbsent(user);
        return userMapper.selectAllocatedPage(pageParam, user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @param pageParam 分页参数
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public PageResult<SysUser> selectUnallocatedPage(SysUser user, PageParam pageParam) {
        useAdminUserTypeIfAbsent(user);
        return userMapper.selectUnallocatedPage(pageParam, user);
    }

    private void useAdminUserTypeIfAbsent(SysUser user) {
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
    @Override
    public SysUser selectUserByUserName(String userName, String userType) {

        return SystemEntityConverter.toDomain(
                userMapper.selectUserByUserName(userName, userType, "0"));
    }

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {

        return SystemEntityConverter.toDomain(userMapper.selectById(userId));
    }

    /**
     * 查询用户所属角色组
     * 
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {

        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {

            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     * 
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {

        List<SysPostEntity> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {

            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPostEntity::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        useAdminUserTypeIfAbsent(user);
        SysUser info = SystemEntityConverter.toDomain(
                userMapper.selectUserByUserNameAndType(
                        user.getUserName(), user.getUserType(), "0"));
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
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
    @Override
    public boolean checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        useAdminUserTypeIfAbsent(user);
        SysUser info = SystemEntityConverter.toDomain(
                userMapper.selectUserByPhoneAndType(
                        user.getPhonenumber(), user.getUserType(), "0"));
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
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
    @Override
    public boolean checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        useAdminUserTypeIfAbsent(user);
        SysUser info = SystemEntityConverter.toDomain(
                userMapper.selectUserByEmailAndType(
                        user.getEmail(), user.getUserType(), "0"));
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     * 
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw ExceptionUtil.business(ErrorCodeEnums.SUPER_ADMIN_USER_OPERATION);
        }
    }

    /**
     * 校验用户是否有数据权限
     * 
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {

        if (!SecurityUtils.isAdmin()) {

            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
            if (StringUtils.isEmpty(users)) {

                throw ExceptionUtil.business(ErrorCodeEnums.USER_DATA_SCOPE_DENIED);
            }
        }
    }

    /**
     * 新增保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SysUser user) {

        user.setUserType(UserTypeEnums.ADMIN);
        SysUserEntity entity = SystemEntityConverter.toEntity(user);
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
    @Override
    public boolean registerUser(SysUser user) {

        user.setUserType(UserTypeEnums.ADMIN);
        return userMapper.insert(SystemEntityConverter.toEntity(user)) > 0;
    }

    /**
     * 修改保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user) {

        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        return userMapper.updateById(SystemEntityConverter.toEntity(user));
    }

    /**
     * 用户授权角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    @Override
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
    @Override
    public int updateUserStatus(SysUser user) {

        return userMapper.updateUserStatus(user.getUserId(), user.getStatus());
    }

    /**
     * 修改用户基本信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {

        return userMapper.updateById(SystemEntityConverter.toEntity(user));
    }

    /**
     * 修改用户头像
     * 
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
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
     * 更新用户登录信息（IP和登录时间）
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP地址
     * @param loginDate 登录时间
     * @return 结果
     */
    public void updateLoginInfo(Long userId, String loginIp, Date loginDate) {

        userMapper.updateLoginInfo(userId, loginIp, loginDate);
    }

    /**
     * 重置用户密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {

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
    @Override
    public int resetUserPwd(Long userId, String password) {

        return userMapper.resetUserPassword(userId, password, new Date());
    }

    /**
     * 新增用户角色信息
     * 
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {

        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户岗位信息
     * 
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {

        Long[] posts = user.getPostIds();
        if (StringUtils.isNotEmpty(posts)) {

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

        if (StringUtils.isNotEmpty(roleIds)) {

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
     * 通过用户ID删除用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId) {

        // 删除用户与角色关联
        userRoleMapper.deleteByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {

        for (Long userId : userIds) {

            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteByUserIds(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteByUserIds(userIds);
        return userMapper.deleteUsersByIds(userIds);
    }

}
