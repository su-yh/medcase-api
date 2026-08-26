# AjaxResult To R<T> Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace every production `AjaxResult` reference except `GlobalExceptionHandler` with typed `R<T>` responses and migrate the corresponding management frontend APIs to `adminRequest`.

**Architecture:** Move the shared response envelope to `medcase-common`, so common utilities and framework handlers do not depend on the web module. Use explicit response DTOs for endpoints with multiple fields, keep `TableDataInfo` pagination and binary download/upload component protocols outside this migration, and let `adminRequest` unwrap `R<T>` while handling both body-level and HTTP-level `401`.

**Tech Stack:** Java 17, Spring Boot 4.1, Spring MVC, Maven, Vue 3, Axios, Node 22.23.2, Yarn.

---

### Task 1: Establish the shared response contract

**Files:**
- Create: `medcase-common/src/main/java/com/ruoyi/common/core/domain/R.java`
- Modify: `medcase-system/src/main/java/com/ruoyi/mvc/response/dto/R.java`
- Modify: `medcase-system/src/main/java/com/ruoyi/mvc/response/wrapper/WrapperResponseBodyAdvice.java`
- Test: `medcase-common/src/test/java/com/ruoyi/common/core/domain/RTest.java`

- [x] Write tests for success, failure, null data, and JSON property names.
- [x] Run the focused common-module test and confirm it fails because the shared class is absent.
- [x] Add the typed immutable `R<T>` to `medcase-common` with `code`, `msg`, `data`, `ofSuccess`, and `ofFail`.
- [x] Update wrapper advice imports and remove the duplicate system-module response class.
- [x] Run the focused test and common/framework compilation.

### Task 2: Migrate shared framework and utility references

**Files:**
- Modify: `medcase-common/src/main/java/com/ruoyi/common/core/controller/BaseController.java`
- Modify: `medcase-common/src/main/java/com/ruoyi/common/utils/poi/ExcelUtil.java`
- Modify: `medcase-framework/src/main/java/com/ruoyi/framework/interceptor/RepeatSubmitInterceptor.java`
- Modify: `medcase-framework/src/main/java/com/ruoyi/framework/security/handle/AuthenticationEntryPointImpl.java`
- Modify: `medcase-framework/src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java`
- Test: existing framework/common tests plus targeted response serialization tests

- [x] Change helper return types to generic `R<T>` and use `R<Void>` for operation-only responses.
- [x] Change Excel utility JSON-result methods to `R<String>` while leaving direct `HttpServletResponse` exports unchanged.
- [x] Replace security/interceptor serialization objects with `R<?>`; preserve `GlobalExceptionHandler` as `AjaxResult`.
- [x] Run `rg` to verify no non-exception production references remain in common/framework.

### Task 3: Add typed DTOs for multi-field management responses

**Files:**
- Create DTOs under `medcase-admin/src/main/java/com/ruoyi/web/controller/.../dto`
- Modify: `CaptchaController.java`
- Modify: `SysRoleController.java`
- Modify: `SysUserController.java`
- Modify: `SysProfileController.java`
- Modify: `SysNoticeController.java`
- Modify: `CacheController.java`
- Test: controller/service response-shape tests where practical

- [x] Create explicit DTOs for captcha data, role department tree, user detail, authorized roles, profile, avatar, notice top data, cache information, and cache command statistics.
- [x] Replace multi-key `AjaxResult.put` assembly with DTO construction and `R<DTO>`.
- [x] Use `R<Void>` or `R<String>` for operation and import results.
- [x] Preserve existing field names and frontend behavior in the DTO JSON.

### Task 4: Migrate remaining management controllers

**Files:**
- Modify: `SysConfigController.java`
- Modify: `SysDeptController.java`
- Modify: `SysDictDataController.java`
- Modify: `SysDictTypeController.java`
- Modify: `SysMenuController.java`
- Modify: `SysPostController.java`
- Modify: `SysRegisterController.java`
- Modify: `SysIndexController.java`
- Modify: `SysRoleController.java`
- Modify: `SysUserController.java`
- Modify: `SysProfileController.java`
- Modify: `ServerController.java`
- Modify: `CacheController.java`
- Modify: `SysLogininforController.java`
- Modify: `SysOperlogController.java`
- Modify: `SysUserOnlineController.java`
- Modify: `CaptchaController.java`

- [x] Replace imports and method signatures with `R<T>`.
- [x] Preserve `TableDataInfo` list methods, direct file exports, and existing authorization annotations.
- [x] Compile after each controller group to catch generic inference and DTO field issues early.
- [x] Verify only `GlobalExceptionHandler` still imports or uses `AjaxResult` in production Java code.

### Task 5: Migrate management frontend API modules

**Files:**
- Modify: `medcase-admin/src/utils/adminRequest.js`
- Modify: `medcase-admin/src/api/login.js`
- Modify: `medcase-admin/src/api/menu.js`
- Modify: `medcase-admin/src/api/system/*.js`
- Modify: `medcase-admin/src/api/monitor/*.js`
- Modify: affected profile, captcha, registration, unlock, and monitor views only where response nesting changes
- Test: `medcase-admin/src/utils/adminRequest.test.js` if the project test setup supports direct Axios interceptor tests

- [x] Switch API functions whose backend methods now return `R<T>` to `adminRequest`.
- [x] Map `data` payloads back to the existing view-facing shapes, including multi-field DTOs.
- [x] Keep `request.js` unchanged and keep direct upload components and binary download helpers outside this migration.
- [x] Extend `adminRequest` error handling to recognize `error.response.status === 401` as well as body `code === 401`.

### Task 6: Verify the migration

**Files:**
- No additional production files unless verification finds a concrete mismatch.

- [x] Run `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-admin -am -DskipTests package`.
- [x] Run focused backend tests, then the relevant module test suite.
- [x] Before frontend commands, run `source ~/.zshrc >/dev/null 2>&1; nvm use 22.23.2`.
- [x] Run the management frontend test/build command with Node 22.23.2 and Yarn.
- [x] Run final `rg` checks for `AjaxResult` and `R<T>` usage, and inspect `git diff`/`git status` without committing.
