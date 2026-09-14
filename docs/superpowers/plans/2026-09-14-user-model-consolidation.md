# 用户实体、Mapper 与 Service 合并实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 `sys_user` 表的重复 Java 实体、Mapper 和用户管理 Service 合并为 `SysUserEntity`、`SysUserMapper`、`SysUserService`，保持现有前后端接口路径和 JSON 结构不变。

**Architecture:** 保留系统模块的 `SysUserEntity`、`SysUserMapper`、`SysUserService` 作为唯一用户数据访问和管理实现。病例端继续保留 `UserVO`、`UserProfileVO` 作为响应模型，认证、资料、短信等业务 Service 保持独立，只改用统一用户实体和 Mapper。

**Tech Stack:** Java 17、Spring Boot、MyBatis-Plus、MyBatis XML、JUnit 5、Mockito、Vue 3 接口契约测试。

**Spec:** `docs/superpowers/specs/2026-09-14-user-model-consolidation-design.md`

## Global Constraints

- 不修改 `sys_user` 表和 Flyway SQL。
- 不修改管理端和病例端现有 HTTP 路径、请求字段和响应字段。
- 同一张 `sys_user` 表只保留 `SysUserEntity` 一个 Java 实体。
- 同一张 `sys_user` 表只保留 `SysUserMapper` 一个 Mapper。
- 编码字段继续使用 `UserTypeEnums`、`UserStatusEnums`、`UserSexEnums`。
- `Boolean` 字段不使用 `equals` 判断；枚举判断使用枚举常量比较。
- Service 不直接获取当前登录用户。
- 不新增抽象层；不提交 Git。

### Task 1: 统一病例端返回模型和查询对象

**Files:**
- Modify: `medcase-api/src/main/java/com/medcase/biz/request/UserQuery.java`
- Modify: `medcase-api/src/main/java/com/medcase/biz/response/UserVO.java`
- Modify: `medcase-api/src/main/java/com/medcase/biz/response/UserProfileVO.java`
- Test: `medcase-api/src/test/java/com/medcase/biz/domain/UserEntityTest.java`

**Interfaces:**
- `UserQuery#status` becomes `UserStatusEnums`.
- `UserVO.fromEntity(SysUserEntity user)` remains the public converter signature.
- `UserProfileVO.fromEntity(SysUserEntity user)` remains the public converter signature.

- [ ] **Step 1: Write the failing type-contract tests**

Add assertions that `UserQuery.status` is `UserStatusEnums` and both converter methods accept `SysUserEntity`; retain tests for attachment annotations and logical-delete behavior against `SysUserEntity`.

- [ ] **Step 2: Run the focused tests to verify the expected failure**

Run:

```bash
mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository \
  -pl medcase-api \
  -Dtest=UserEntityTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: compilation or reflection assertions fail because the old `UserEntity` type is still used.

- [ ] **Step 3: Modify the query and converter types**

Change `UserQuery.status` from `String` to `UserStatusEnums`. Change both response converter imports and parameters from `UserEntity` to `SysUserEntity`; copy the same fields so the JSON output remains unchanged.

- [ ] **Step 4: Run the focused model tests**

Run the same Maven command and expect all model tests to pass.

### Task 2: Merge `UserMapper` capabilities into `SysUserMapper`

**Files:**
- Modify: `medcase-api/src/main/java/com/medcase/system/mapper/SysUserMapper.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/mapper/SysUserMapper.xml`
- Modify: `medcase-api/src/main/java/com/medcase/system/service/SysUserService.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/service/SysDeptService.java`
- Modify: `medcase-api/src/main/java/com/medcase/framework/web/service/UserLoginService.java`
- Test: `medcase-api/src/test/java/com/medcase/biz/mapper/UserMapperTest.java`
- Test: `medcase-api/src/test/java/com/medcase/system/mapper/SysUserMapperPageTest.java`
- Test: `medcase-api/src/test/java/com/medcase/system/service/SysUserServicePageTest.java`

**Interfaces:**
- `SysUserMapper` gains:

```java
boolean usernameExists(String username, UserTypeEnums userType);
boolean phoneExists(String phone, UserTypeEnums userType);
boolean phoneExists(String phone);
SysUserEntity selectUserById(Long userId, UserTypeEnums userType);
SysUserEntity selectUserByUserName(String userName, UserTypeEnums userType);
PageResult<SysUserEntity> selectUserPage(
        PageParam pageParam, UserQuery query);
PageResult<SysUserEntity> selectUserPage(
        PageParam pageParam, UserQuery query, UserTypeEnums userType);
```

- Existing system username lookup methods use `UserTypeEnums` instead of `String`.
- `SysUserMapper.xml` continues to use `SysUserEntity` result maps and retains role-page queries.

- [ ] **Step 1: Add failing mapper contract assertions**

Move the meaningful `UserMapperTest` assertions to `SysUserMapperTest`, assert `BaseMapperX<SysUserEntity>` compatibility, and assert the typed duplicate-phone and user-page methods. Add a source assertion that no production class imports `com.medcase.biz.mapper.UserMapper`.

- [ ] **Step 2: Run mapper tests to verify the expected failure**

Run:

```bash
mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository \
  -pl medcase-api \
  -Dtest=SysUserMapperTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: failure because the new methods and test target do not yet exist.

- [ ] **Step 3: Implement the merged Mapper methods**

Copy the `UserMapper` lambda-query implementations into `SysUserMapper`, replace `UserEntity` method references with `SysUserEntity`, use `UserStatusEnums` for `UserQuery.status`, and make all user-type predicates use `UserTypeEnums`.

- [ ] **Step 4: Update existing system call sites**

Update `SysUserService`, `SysDeptService`, `SysIndexController`, and `UserLoginService` to call the typed `SysUserMapper`/`SysUserService` methods. Keep the existing admin page methods and XML role-page methods unchanged except for the unified result type.

- [ ] **Step 5: Run mapper and system service tests**

Run:

```bash
mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository \
  -pl medcase-api \
  -Dtest=SysUserMapperTest,SysUserMapperPageTest,SysUserServicePageTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: all selected tests pass.

### Task 3: Move business user management into `SysUserService`

**Files:**
- Modify: `medcase-api/src/main/java/com/medcase/system/service/SysUserService.java`
- Modify: `medcase-api/src/main/java/com/medcase/biz/controller/DoctorUserAdminController.java`
- Modify: `medcase-api/src/main/java/com/medcase/biz/controller/PatientUserAdminController.java`
- Modify: `medcase-api/src/main/java/com/medcase/biz/controller/SupplierAdminController.java`
- Modify: `medcase-api/src/main/java/com/medcase/biz/service/UserAuthService.java`
- Modify: `medcase-api/src/main/java/com/medcase/biz/service/UserProfileService.java`
- Modify: `medcase-api/src/main/java/com/medcase/framework/web/service/UserLoginService.java`
- Test: `medcase-api/src/test/java/com/medcase/system/service/SysUserServiceBizTest.java`
- Test: `medcase-api/src/test/java/com/medcase/biz/service/UserServiceTest.java`

**Interfaces:**
- Add to `SysUserService`:

```java
PageResult<SysUserEntity> selectBizPage(
        PageParam pageParam, UserQuery query, UserTypeEnums userType);
SysUserEntity selectBizUserById(Long userId, UserTypeEnums userType);
SysUserEntity selectAnyUserById(Long userId);
void reviewUser(Long userId, UserReviewRequest request, UserTypeEnums userType);
```

- Controllers map returned `SysUserEntity` values through `UserVO.fromEntity`.
- `UserAuthService`, `UserProfileService`, and `UserLoginService` use `SysUserEntity` without conversion.

- [ ] **Step 1: Add failing business-service contract tests**

Add tests for doctor/ patient page filtering, typed detail lookup, review approval/rejection, and `UserVO.fromEntity(SysUserEntity)`. Add tests that `UserAuthService` and `UserProfileService` fields use `SysUserMapper` and `SysUserEntity`.

- [ ] **Step 2: Run the focused tests to verify the expected failure**

Run:

```bash
mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository \
  -pl medcase-api \
  -Dtest=SysUserServiceBizTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: failure because the new `SysUserService` methods do not yet exist.

- [ ] **Step 3: Implement business user methods in `SysUserService`**

Move the existing `UserService` page, detail, any-user, and review logic into `SysUserService`, replacing `UserEntity` with `SysUserEntity` and `UserMapper` with `SysUserMapper`.

- [ ] **Step 4: Update business Controllers**

Inject `SysUserService` into doctor, patient, and supplier Controllers. Convert service entities to `UserVO` in the Controller and preserve all existing endpoint paths, permissions, and response fields.

- [ ] **Step 5: Update authentication and profile services**

Change registration, account deletion, profile lookup, phone update, and password update to use `SysUserEntity` and `SysUserMapper`. Change `UserLoginService` portal login to load `SysUserEntity` directly and remove `toSysUser`.

- [ ] **Step 6: Run business-service and controller tests**

Run:

```bash
mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository \
  -pl medcase-api \
  -Dtest=SysUserServiceBizTest,UserAuthServiceTest,UserProfileServiceTest,UserLoginServiceTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: all selected tests pass.

### Task 4: Remove duplicate entity, Mapper, and Service

**Files:**
- Delete: `medcase-api/src/main/java/com/medcase/biz/domain/UserEntity.java`
- Delete: `medcase-api/src/main/java/com/medcase/biz/mapper/UserMapper.java`
- Delete: `medcase-api/src/main/java/com/medcase/biz/service/UserService.java`
- Delete: `medcase-api/src/test/java/com/medcase/biz/domain/UserEntityTest.java`
- Delete: `medcase-api/src/test/java/com/medcase/biz/mapper/UserMapperTest.java`
- Delete: `medcase-api/src/test/java/com/medcase/biz/service/UserServiceTest.java`

**Interfaces:**
- No production class imports or references `UserEntity`, `UserMapper`, or `UserService`.
- All `sys_user` persistence uses `SysUserEntity` and `SysUserMapper`.

- [ ] **Step 1: Add the duplicate-removal contract test**

Add a source scan test that checks production Java sources do not contain the old class names or imports, while allowing the design document and migration plan to mention historical names.

- [ ] **Step 2: Run the contract test to verify the expected failure**

Run:

```bash
mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository \
  -pl medcase-api \
  -Dtest=UserModelConsolidationContractTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: failure while the duplicate files or references remain.

- [ ] **Step 3: Delete the duplicate implementation and obsolete tests**

Delete the three duplicate production types and their tests after all call sites use the system types.

- [ ] **Step 4: Run the duplicate-removal contract test**

Run the same Maven command and expect it to pass.

### Task 5: Verify API compatibility and complete regression testing

**Files:**
- Modify: `medcase-api/src/test/java/com/medcase/common/core/domain/model/LoginUserJsonSerializationTest.java`
- Modify: `medcase-api/src/test/java/com/medcase/system/LoginRecordMigrationContractTest.java`
- Modify: `medcase-api/src/test/java/com/medcase/system/BooleanStatusMigrationContractTest.java`
- Modify: `medcase-api/src/test/java/com/medcase/web/controller/system/SysUserControllerTest.java`
- Modify: `medcase-api/src/test/java/com/medcase/web/controller/system/SysRoleControllerPageTest.java`
- Modify: `medcase-admin/test/*.test.js` only if existing API contract tests require type-compatible updates

**Interfaces:**
- Management endpoints continue returning `SysUserEntity` where they already did.
- Business endpoints continue returning `UserVO` and `UserProfileVO` with unchanged JSON property names.
- Login and JWT payloads continue using `SysUserEntity`.

- [ ] **Step 1: Run backend compilation and targeted contract tests**

Run:

```bash
mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository \
  -pl medcase-api \
  -Dtest=LoginUserJsonSerializationTest,LoginRecordMigrationContractTest,BooleanStatusMigrationContractTest,SysUserControllerTest,SysRoleControllerPageTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: all selected tests pass.

- [ ] **Step 2: Run the complete backend test suite**

Run:

```bash
mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository \
  -pl medcase-api \
  test
```

Expected: compilation succeeds and any pre-existing unrelated failure is recorded separately.

- [ ] **Step 3: Run frontend tests**

Run from `/Users/suyunhong/suyh-develop/github/medcase-admin`:

```bash
source ~/.zshrc >/dev/null 2>&1
nvm use 22.23.2 >/dev/null
yarn test
```

Expected: existing frontend API and route tests pass; no frontend source change is expected because JSON field names and endpoint paths remain unchanged.

- [ ] **Step 4: Run final static checks**

Run:

```bash
git diff --check
rg -n "UserEntity|UserMapper|com\.medcase\.biz\.service\.UserService" \
  medcase-api/src/main/java medcase-api/src/test/java
git status --short
```

Expected: no old production references remain, whitespace validation passes, and only planned files are changed.
