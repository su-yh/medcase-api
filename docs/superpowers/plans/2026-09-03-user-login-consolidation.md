# 用户登录合并 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让管理端、医生和患者使用同一个登录业务服务，同时保留各自既有的行为差异。

**Architecture:** 新增 `UserLoginService` 负责统一登录流程；`SysLoginService` 和 `UserAuthService` 只保留各自 Controller 所需的薄委托或非登录职责。管理员和病例端在用户加载、状态处理、登录审计方面保留必要分支，Token 仍由现有 `TokenService` 创建。

**Tech Stack:** Spring Boot 4、Spring Security、JWT、JUnit 5、Mockito。

**Spec:** `docs/superpowers/specs/2026-09-03-user-login-consolidation-design.md`

## Global Constraints

- 不修改管理端 `/login` 与病例端 `/biz/user-auth/login` 的 API。
- 不修改 JWT 格式、过滤器、`SecurityFilterChain` 或全局异常处理。
- 密码校验统一依赖 `PasswordEncoder` Bean。
- 管理员登录审计和失败计数只在管理员类型执行。
- 普通业务代码不使用 `var`。

---

### Task 1: 为统一登录流程建立失败测试

**Files:**
- Create: `medcase-api/src/test/java/com/medcase/framework/web/service/UserLoginServiceTest.java`

**Interfaces:**
- Produces: `UserLoginService#login(String, String, String, String, UserTypeEnums)`。
- Consumes: `ISysUserService`、`UserMapper`、`PasswordEncoder`、`TokenService`、`SysPasswordService`。

- [ ] **Step 1: Write the failing test**

```java
@Test
void loginCreatesTokenForDoctor() {
    when(userMapper.selectUserByUsername("doctor01", UserTypeEnums.DOCTOR))
            .thenReturn(user("doctor01", UserTypeEnums.DOCTOR));
    when(passwordEncoder.matches("secret123", user.getPassword())).thenReturn(true);
    when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

    assertEquals("doctor-token",
            service.login("doctor01", "secret123", null, null, UserTypeEnums.DOCTOR));
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api -Dtest=UserLoginServiceTest test`

Expected: FAIL because `UserLoginService` does not exist.

- [ ] **Step 3: Write minimal implementation**

```java
public String login(String username, String password, String code, String uuid, UserTypeEnums userType) {
    validateCaptcha(username, code, uuid);
    loginPreCheck(username, password);
    LoginUser loginUser = loadLoginUser(username, password, userType);
    updateLoginInfo(loginUser.getUserId(), userType);
    return tokenService.createToken(loginUser);
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api -Dtest=UserLoginServiceTest test`

Expected: PASS.

### Task 2: 迁移两端登录入口

**Files:**
- Modify: `medcase-api/src/main/java/com/medcase/framework/web/service/SysLoginService.java`
- Modify: `medcase-api/src/main/java/com/medcase/biz/service/UserAuthService.java`
- Modify: `medcase-api/src/main/java/com/medcase/web/controller/system/SysLoginController.java`
- Modify: `medcase-api/src/test/java/com/medcase/biz/service/UserAuthServiceTest.java`

**Interfaces:**
- Consumes: `UserLoginService#login(String, String, String, String, UserTypeEnums)`。
- Produces: 保持两个 Controller 的原有 Token 返回格式。

- [ ] **Step 1: Write the failing delegation tests**

```java
verify(userLoginService).login(
        "doctor01", "secret123", null, null, UserTypeEnums.DOCTOR);
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api -Dtest=UserAuthServiceTest test`

Expected: FAIL because `UserAuthService` 仍有独立登录实现。

- [ ] **Step 3: Delegate both endpoints to the unified service**

```java
return userLoginService.login(
        loginBody.getUsername(),
        loginBody.getPassword(),
        loginBody.getCode(),
        loginBody.getUuid(),
        UserTypeEnums.ADMIN);
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api -Dtest=UserLoginServiceTest,UserAuthServiceTest test`

Expected: PASS.

### Task 3: 统一 PasswordEncoder 使用方式

**Files:**
- Modify: `medcase-api/src/main/java/com/medcase/framework/config/SecurityConfig.java`
- Modify: `medcase-api/src/main/java/com/medcase/common/utils/SecurityUtils.java`
- Modify: every current production caller of `SecurityUtils.encryptPassword` and `SecurityUtils.matchesPassword`
- Modify: affected unit tests

**Interfaces:**
- Produces: 一个应用级 `PasswordEncoder` Bean 供密码加密、匹配和 Spring Security 使用。

- [ ] **Step 1: Write a failing service test using an injected PasswordEncoder**

```java
when(passwordEncoder.matches("old-password", user.getPassword())).thenReturn(true);
```

- [ ] **Step 2: Run the focused test to verify it fails**

Run: `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api -Dtest=UserLoginServiceTest test`

Expected: FAIL while direct static BCrypt creation remains in the login flow.

- [ ] **Step 3: Replace static BCrypt calls with injected PasswordEncoder**

```java
private final PasswordEncoder passwordEncoder;

user.setPassword(passwordEncoder.encode(password));
```

- [ ] **Step 4: Run focused password and login tests**

Run: `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api -Dtest=UserLoginServiceTest,UserAuthServiceTest,UserProfileServiceTest test`

Expected: PASS.

### Task 4: 全量验证

**Files:**
- Modify: only files required by preceding tasks.

- [ ] **Step 1: Run all backend tests**

Run: `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api test`

Expected: PASS.

- [ ] **Step 2: Run application package build**

Run: `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api -DskipTests package`

Expected: PASS.
