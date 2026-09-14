# 用户实体、Mapper 与 Service 合并设计

## 目标

将当前都映射 `sys_user` 表的 `SysUserEntity` 和 `UserEntity` 合并为一个实体，并将 `SysUserMapper`、`UserMapper` 以及用户管理相关 Service 合并，消除同一张表的重复数据模型和访问入口。

合并后不修改数据库表结构、不修改管理端和病例端已有 HTTP 路径及响应字段，只调整后端内部类型和调用链。

## 最终结构

保留系统模块中的用户实现作为唯一用户数据实现：

```text
com.medcase.system.entity.SysUserEntity
com.medcase.system.mapper.SysUserMapper
com.medcase.system.service.SysUserService
```

删除业务模块中的重复实现：

```text
com.medcase.biz.domain.UserEntity
com.medcase.biz.mapper.UserMapper
com.medcase.biz.service.UserService
```

`SysUserEntity` 作为 `sys_user` 表的唯一实体，继续被 `LoginUser`、系统 Controller、系统 DTO 和权限服务使用。

## 实体合并规则

以 `SysUserEntity` 为基准，保留并核对以下字段：

- 系统字段：`userId`、`deptId`、`userName`、`nickName`、`email`、`phonenumber`、`password`、`status`、`delFlag`、`pwdUpdateDate`、`remark`、审计字段
- 用户类型字段：`userType`、`supplierId`
- 医生/患者资料字段：`sex`、`idCardNumber`、`title`、`idCardFront`、`idCardBack`、`qualificationCertificate`、`reviewReason`
- 用户扩展字段：`avatar`
- 系统管理非持久化字段：`dept`、`roles`、`roleIds`、`postIds`、`roleId`

保留现有注解行为：

- `@TableName(value = "sys_user", autoResultMap = true)`
- 文件附件字段使用 `JacksonTypeHandler`
- 密码使用 `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)`
- `delFlag` 使用 `@TableLogic` 和 `@JsonIgnore`
- 编码字段继续使用 `UserTypeEnums`、`UserStatusEnums`、`UserSexEnums`

删除 `UserEntity` 后，不再保留 `UserLoginService#toSysUser` 转换方法。

## Mapper 合并规则

将 `UserMapper` 的通用用户查询能力迁移到 `SysUserMapper`，统一以 `SysUserEntity` 为泛型类型：

```java
boolean usernameExists(String username, UserTypeEnums userType);

boolean phoneExists(String phone, UserTypeEnums userType);

SysUserEntity selectUserById(Long userId);

SysUserEntity selectUserById(Long userId, UserTypeEnums userType);

SysUserEntity selectUserByUserName(String userName, UserTypeEnums userType);

PageResult<SysUserEntity> selectUserPage(
        PageParam pageParam, UserQuery query, UserTypeEnums userType);
```

现有系统 Mapper 中使用 `String userType` 的用户名查询方法统一改为 `UserTypeEnums`，避免同一编码字段同时暴露字符串和枚举两种入口。

重复的用户名查询方法合并为一个枚举参数版本：

```java
selectUserByUserName(String, UserTypeEnums)
selectUserByUserNameAndType(String, UserTypeEnums)
```

只保留一个。

保留 `SysUserMapper` 已有的系统管理查询能力：

- 管理端用户分页查询
- 角色已分配/未分配用户分页查询
- 用户名、手机号、邮箱唯一性查询
- 部门用户数量查询
- 用户状态、头像、密码更新

`SysUserMapper.xml` 的 namespace 和结果类型继续使用 `SysUserMapper`、`SysUserEntity`，补充业务用户查询需要的字段映射，确保医生/患者资料字段在查询和写入时不丢失。

## 查询对象与返回对象

`UserQuery` 和 `UserQueryRequest` 继续分别服务病例端用户管理和系统用户管理，避免为了合并内部实体而改变接口参数结构。

`UserQuery.status` 从字符串调整为 `UserStatusEnums`，前端仍使用现有状态编码 JSON，不改变接口传输格式。

`UserVO`、`UserProfileVO` 继续保留为病例端响应对象，不直接让系统实体作为病例端接口响应。两个转换方法改为接收 `SysUserEntity`：

```java
UserVO.fromEntity(SysUserEntity user);

UserProfileVO.fromEntity(SysUserEntity user);
```

这样可以继续隐藏管理端专有字段，并保持病例端原有字段名、字段范围和 JSON 结构。

## Service 合并规则

保留 `SysUserService` 作为唯一用户管理 Service，将业务 `UserService` 的用户数据能力迁移进去：

- 医生/患者分页查询
- 按用户类型查询用户详情
- 查询任意用户
- 医生/患者审核
- 用户类型过滤
- 用户唯一性查询
- 用户状态、头像、密码和资料更新

迁移后的业务用户查询方法使用 `SysUserEntity`：

```java
PageResult<SysUserEntity> selectBizPage(
        PageParam pageParam, UserQuery query, UserTypeEnums userType);

SysUserEntity selectBizUserById(Long userId, UserTypeEnums userType);

SysUserEntity selectAnyUserById(Long userId);

void reviewUser(Long userId, UserReviewRequest request, UserTypeEnums userType);
```

方法命名以现有 Service 风格为基础，最终实现时避免通过返回 `UserVO` 让系统 Service 依赖业务响应对象。病例端 Controller 负责将 `SysUserEntity` 转换为 `UserVO`。

## 其他 Service 调整

以下 Service 不合并到 `SysUserService`，只替换其实体和 Mapper 依赖：

```text
UserAuthService
UserProfileService
UserRegisterSmsCodeService
UserPermissionService
```

调整规则：

- `UserAuthService` 注册和账号注销改用 `SysUserEntity`、`SysUserMapper`
- `UserProfileService` 当前用户资料、手机号和密码修改改用 `SysUserEntity`、`SysUserMapper`
- `UserLoginService` 管理员、医生、患者登录统一加载 `SysUserEntity`
- `UserLoginService` 删除 `toSysUser(UserEntity)` 转换
- `LoginUser` 保持使用 `SysUserEntity`
- `SysPermissionService` 保持使用 `SysUserEntity`

认证流程、登录状态、审核状态、Token 格式和权限校验规则不因实体合并改变。

## Controller 兼容规则

管理端接口路径和返回类型保持不变：

```text
/system/user/**
/login
/logout
/getInfo
/getRouters
```

病例端接口路径和返回 JSON 结构保持不变：

```text
/biz/doctor-user/**
/biz/patient-user/**
/biz/supplier/{supplierId}/users
/biz/user-auth/**
/biz/user-profile/**
```

病例端 Controller 仍返回 `UserVO`、`UserProfileVO` 等接口对象，不直接暴露 `SysUserEntity` 的系统字段。

## 数据库与 Flyway

不新增、不修改、不删除数据库字段和 SQL。两套实体当前都映射 `sys_user`，合并只改变 Java 内部模型。

现有逻辑删除规则继续由 MyBatis-Plus 的 `@TableLogic` 处理，业务查询不额外加入删除标记条件。

## 风险与不适合合并的部分

1. `SysUserEntity` 字段更多，若病例端直接返回实体，可能暴露邮箱、密码、部门、角色等字段，因此必须保留 `UserVO` 和 `UserProfileVO` 转换层。
2. 管理端与病例端查询条件不同，不能简单删除两个 Query 对象后共用一个接口参数。
3. 用户注册、登录、资料修改、审核和管理端 CRUD 的业务规则不同，不能全部压缩到 `SysUserService`。
4. `SysUserMapper.xml` 既包含管理端关联查询，又包含基础用户查询，迁移时必须核对 resultMap 和字段选择，避免医生资料字段查询缺失。
5. `UserEntity` 删除后，所有测试中的实体、Mapper、Service 类型都必须同步迁移，尤其是登录、资料、注册和审核测试。

## 验证范围

至少覆盖：

- 管理员登录、医生登录、患者登录
- 用户注册、账号注销、资料查询、手机号修改、密码修改
- 医生/患者分页、详情和审核
- 管理端用户分页、用户详情、状态修改、删除、角色授权
- 供应商下属用户查询
- 逻辑删除过滤
- 前后端接口字段和路径兼容性

执行后端编译、定向单元测试、全量测试和前端接口契约测试。
