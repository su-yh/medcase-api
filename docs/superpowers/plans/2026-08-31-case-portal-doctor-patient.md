# Case Portal Doctor Patient Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将现有医生端改造成支持医生和患者两种独立用户的病例端，并在管理端分别管理医生、患者和两类病例。

**Architecture:** 在现有 `sys_user` 基础上增加患者用户类型，病例数据改为使用通用用户所有权字段。病例端共用认证、注册和病例页面，通过 `userType` 编码区分医生与患者；管理端通过固定用户类型分别查询医生和患者，所有当前用户接口参数使用 `@CurrLoginUser` 注入。

**Tech Stack:** Java 17, Spring Boot 4, Spring Security, MyBatis-Plus, Flyway, Vue 3, Vite, Element Plus, Pinia, Axios, Vitest.

**Spec:** 已在当前对话中确认的“病例端医生/患者”方案。

## Global Constraints

- `UserTypeEnums` 使用 `ADMIN("00", "后台用户")`、`DOCTOR("01", "医生")`、`PATIENT("02", "患者")`。
- 前端请求中的 `userType` 使用 `01`、`02` 编码，不使用枚举名称。
- 需要当前用户的接口参数必须使用 `@CurrLoginUser`，不使用静态当前用户方法。
- 医生和患者互相独立，只能管理自己的病例。
- 邀请人前端文案统一为“邀请人”，技术字段暂时保留 `inviteCode`，固定值为 `9999`，不实现动态邀请人逻辑。
- 数据库只修改初始 SQL，不生成增量 SQL。
- Java 左大括号放在行尾，功能代码不使用 `var`。
- 不执行 Git commit、reset、checkout 或其他 Git 写操作。
- 前端命令执行前必须使用 `nvm use 22.23.2`。

### Task 1: Extend user types and generic case-user authentication

**Files:**
- Modify: `medcase-api/src/main/java/com/medcase/common/enums/UserTypeEnums.java`
- Rename/modify: `medcase-api/src/main/java/com/medcase/biz/domain/DoctorUserEntity.java`
- Rename/modify: `medcase-api/src/main/java/com/medcase/biz/mapper/DoctorUserMapper.java`
- Rename/modify: `medcase-api/src/main/java/com/medcase/biz/request/DoctorLoginRequest.java`
- Rename/modify: `medcase-api/src/main/java/com/medcase/biz/request/DoctorRegisterRequest.java`
- Rename/modify: `medcase-api/src/main/java/com/medcase/biz/service/DoctorAuthService.java`
- Rename/modify: `medcase-api/src/main/java/com/medcase/biz/controller/DoctorAuthPortalController.java`
- Modify: related `ErrorCodeEnums`, cache constants, and SMS service references
- Test: `medcase-api/src/test/java/com/medcase/biz/service/DoctorAuthServiceTest.java`

- [ ] Add `PATIENT("02", "患者")` and preserve code-based JSON/query conversion.
- [ ] Replace doctor-only authentication request handling with case authentication that requires `UserTypeEnums`.
- [ ] Query `sys_user` by username and requested user type.
- [ ] Apply common registration validation to both types.
- [ ] Require identity number and both identity-card attachments for both types.
- [ ] Require title and qualification certificate only for doctors.
- [ ] Require fixed `inviteCode=9999` for both types without adding inviter persistence.
- [ ] Keep registration SMS verification behavior and rename doctor-specific cache/business names to case registration names.
- [ ] Inject `LoginUser` with `@CurrLoginUser` in logout and account deletion methods.
- [ ] Add focused tests for doctor/patient registration and login isolation before implementation, verify red, implement, and verify green.

### Task 2: Generalize case data ownership and portal authorization

**Files:**
- Modify initial SQL: `medcase-api/src/main/resources/db/migration/master/V01_00_00/V01_00_00_003__medcase.sql`
- Rename/modify: `medcase-api/src/main/java/com/medcase/biz/domain/DoctorCaseEntity.java`
- Rename/modify: `medcase-api/src/main/java/com/medcase/biz/mapper/DoctorCaseMapper.java`
- Rename/modify: case request/response/service/controller classes under `medcase-api/src/main/java/com/medcase/biz`
- Modify: case-related tests under `medcase-api/src/test/java/com/medcase/biz`

- [ ] Rename `medcase_doctor_case` to `medcase_case`.
- [ ] Rename `doctor_id`/`doctor_nickname` to `user_id`/`user_nickname`.
- [ ] Preserve status, review, settlement, attachment, and content behavior.
- [ ] Make portal list/detail/update/delete/submit methods derive ownership from `@CurrLoginUser LoginUser`.
- [ ] Reject or ignore client-provided owner identifiers for portal operations.
- [ ] Ensure a doctor cannot access patient cases and a patient cannot access doctor cases.
- [ ] Keep admin case review able to view all records, with explicit user-type filters.
- [ ] Add tests proving ownership isolation for both user types.

### Task 3: Add separate admin patient management

**Files:**
- Create/modify: backend patient administration controller/service/request/response classes under `medcase-api/src/main/java/com/medcase/biz`
- Modify: existing doctor administration classes to use the shared service
- Modify: `medcase-api/src/main/resources/db/migration/master/V01_00_00/V01_00_00_002__biz-menu.sql`
- Modify: `medcase-admin/src/api/biz/doctor.js`
- Create: `medcase-admin/src/api/biz/patient.js`
- Modify/create: `medcase-admin/src/views/biz/doctor/index.vue`
- Create: `medcase-admin/src/views/biz/patient/index.vue`

- [ ] Keep doctor management fixed to `user_type=01`.
- [ ] Add patient management fixed to `user_type=02`.
- [ ] Reuse common list/detail/review behavior while keeping separate controller endpoints and permissions.
- [ ] Display identity-card attachments for both, and doctor-only title/qualification fields only for doctors.
- [ ] Support approve/reject/rejection reason for both.
- [ ] Add initial SQL for the patient menu and permissions.

### Task 4: Split admin case menus into doctors and patients

**Files:**
- Modify: backend admin case controller/service/query classes under `medcase-api/src/main/java/com/medcase/biz`
- Modify: `medcase-api/src/main/resources/db/migration/master/V01_00_00/V01_00_00_002__biz-menu.sql`
- Modify/create: `medcase-admin/src/api/biz/caseReview.js`
- Modify/create: admin case review views under `medcase-admin/src/views/biz`

- [ ] Rename the existing menu from `病例审核` to `医生病例`.
- [ ] Add the sibling menu `患者病例`.
- [ ] Use fixed type filters `01` and `02` in the respective admin endpoints.
- [ ] Keep menu labels free of the word `审核`, while retaining review actions inside each page.
- [ ] Display the submitter as a generic user name.
- [ ] Keep query, attachment preview, review, rejection reason, and settlement behavior.

### Task 5: Convert the doctor frontend into the shared case portal

**Files:**
- Modify: `/Users/suyunhong/suyh-develop/github/medcase-doctor/package.json`
- Modify: `/Users/suyunhong/suyh-develop/github/medcase-doctor/src/views/auth/LoginView.vue`
- Modify: `/Users/suyunhong/suyh-develop/github/medcase-doctor/src/views/auth/RegisterView.vue`
- Modify: `/Users/suyunhong/suyh-develop/github/medcase-doctor/src/api/doctor/auth.js`
- Modify: `/Users/suyunhong/suyh-develop/github/medcase-doctor/src/stores/user.js`
- Modify: `/Users/suyunhong/suyh-develop/github/medcase-doctor/src/router/access.js`
- Modify: route/layout/profile/case labels and tests under `/Users/suyunhong/suyh-develop/github/medcase-doctor/src`

- [ ] Change visible product text from doctor portal to case portal.
- [ ] Add doctor/patient selection to login and registration.
- [ ] Submit `01` or `02` as `userType`.
- [ ] Make registration fields conditional and validate common/doctor-specific fields correctly.
- [ ] Rename registration upload business to case registration.
- [ ] Allow both user types through shared route guards once approved.
- [ ] Preserve the physical `medcase-doctor` directory and existing deployment base path.
- [ ] Add tests for type selection, conditional fields, and login payloads.
- [ ] Run `nvm use 22.23.2 && yarn test` and `nvm use 22.23.2 && yarn build:stage`.

### Task 6: Align initial database SQL and verify all contracts

**Files:**
- Modify: `medcase-api/src/main/resources/db/migration/master/V01_00_00/V01_00_00_001__system.sql`
- Modify: `medcase-api/src/main/resources/db/migration/master/V01_00_00/V01_00_00_002__biz-menu.sql`
- Modify: `medcase-api/src/main/resources/db/migration/master/V01_00_00/V01_00_00_003__medcase.sql`
- Modify: backend and frontend tests affected by renamed contracts

- [ ] Update `sys_user.user_type` comments to describe backend user, doctor, and patient.
- [ ] Keep doctor-only columns nullable for patients.
- [ ] Add patient management menu and permissions.
- [ ] Replace the single case review menu with `医生病例` and `患者病例`.
- [ ] Ensure role-menu initialization grants the intended management permissions.
- [ ] Scan for stale `doctor`-only paths, labels, constants, and type filters.
- [ ] Run backend compile and tests.
- [ ] Run frontend tests and staging builds after selecting Node 22.23.2.
- [ ] Run `git diff --check` and report any unrelated existing changes without reverting them.
