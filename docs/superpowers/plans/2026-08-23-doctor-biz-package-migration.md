# Doctor Business Package Migration Implementation Plan

> **For agentic workers:** Execute this plan inline in the current workspace. Do not commit changes; the user will commit manually.

**Goal:** Move doctor authentication/user management into `com.ruoyi.biz.doctor`, move case business into `com.ruoyi.biz.caseinfo`, add `/biz` to doctor REST APIs, and synchronize the doctor frontend.

**Architecture:** Package boundaries follow business ownership rather than client role. Doctor user/auth code remains under `biz.doctor`; case submission, case querying, and case review code share `biz.caseinfo`. Existing management routes remain stable except for the doctor-facing routes changing from `/doctor/**` to `/biz/doctor/**`.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, Maven, Vue 3, Axios, Vitest, Yarn.

---

### Task 1: Lock the new package and route contracts with tests

**Files:**
- Modify: `medcase-admin/src/test/java/com/ruoyi/web/controller/doctor/DoctorAuthControllerTest.java`
- Create or move: `medcase-admin/src/test/java/com/ruoyi/biz/doctor/controller/DoctorAuthControllerTest.java`
- Create: `medcase-admin/src/test/java/com/ruoyi/biz/caseinfo/controller/DoctorCaseControllerTest.java`
- Modify: `medcase-doctor/src/api/doctor/__tests__/cases.spec.js`
- Modify: `medcase-doctor/src/api/doctor/__tests__/auth.spec.js` if present

- [ ] Assert the migrated controller classes are in the new packages and retain anonymous auth annotations.
- [ ] Assert the case controller request mappings use `/biz/doctor/cases`.
- [ ] Assert frontend auth and case API calls use `/biz/doctor/**`.
- [ ] Run the focused tests and confirm failures identify the old package/path contract.

### Task 2: Migrate doctor authentication and doctor-user management

**Files:**
- Move: `medcase-admin/src/main/java/com/ruoyi/web/controller/doctor/DoctorAuthController.java` to `com/ruoyi/biz/doctor/api/controller`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/service/DoctorAuthService.java` to `com/ruoyi/biz/doctor/service`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/controller/doctor/request/DoctorLoginRequest.java` to `com/ruoyi/biz/doctor/request`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/controller/doctor/request/DoctorRegisterRequest.java` to `com/ruoyi/biz/doctor/request`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/controller/biz/doctor/DoctorUserController.java` to `com/ruoyi/biz/doctor/controller`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/service/biz/doctor/DoctorUserService.java` to `com/ruoyi/biz/doctor/service`
- Update: `medcase-system/src/main/java/com/ruoyi/biz/doctor/domain/DoctorUserEntity.java`
- Update: `medcase-system/src/main/java/com/ruoyi/biz/doctor/mapper/DoctorUserMapper.java`
- Update: corresponding Java tests and all imports

- [ ] Change packages/imports without changing authentication behavior.
- [ ] Change `DoctorAuthController` mapping from `/doctor/auth` to `/biz/doctor/auth`.
- [ ] Keep management doctor list/detail routes at `/biz/doctor`.
- [ ] Run focused doctor auth/user tests and verify they pass.

### Task 3: Migrate case business

**Files:**
- Move: `medcase-admin/src/main/java/com/ruoyi/web/controller/doctor/DoctorCaseController.java` to `com/ruoyi/biz/caseinfo/api/controller`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/service/DoctorCaseService.java` to `com/ruoyi/biz/caseinfo/service`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/domain/DoctorCaseEntity.java` to `com/ruoyi/biz/caseinfo/domain`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/domain/FileAttachment.java` to `com/ruoyi/biz/caseinfo/domain`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/enums/DoctorCaseStatusEnums.java` to `com/ruoyi/biz/caseinfo/enums`
- Move: `medcase-admin/src/main/java/com/ruoyi/web/mapper/DoctorCaseMapper.java` to `com/ruoyi/biz/caseinfo/mapper`
- Move: doctor case request/response classes to `com/ruoyi/biz/caseinfo/request` and `com/ruoyi/biz/caseinfo/response`
- Move: existing review controller/service/mapper/request/response to matching `com/ruoyi/biz/caseinfo` packages
- Update: corresponding Java tests and mapper XML references

- [ ] Change `DoctorCaseController` mapping from `/doctor` to `/biz/doctor`.
- [ ] Preserve all case submission, draft, pagination, detail, delete, and review behavior.
- [ ] Ensure no case production code remains under `com.ruoyi.web` except unrelated web infrastructure.
- [ ] Run focused case tests and verify they pass.

### Task 4: Synchronize the doctor frontend

**Files:**
- Modify: `medcase-doctor/src/api/doctor/auth.js`
- Modify: `medcase-doctor/src/api/doctor/cases.js`
- Modify: `medcase-doctor/src/api/doctor/__tests__/auth.spec.js`
- Modify: `medcase-doctor/src/api/doctor/__tests__/cases.spec.js`
- Modify: `medcase-doctor/src/utils/__tests__/response.spec.js`

- [ ] Change auth calls to `/biz/doctor/auth/**`.
- [ ] Change case calls to `/biz/doctor/cases/**`.
- [ ] Keep common upload and captcha endpoints unchanged.
- [ ] Update response/error fixtures containing the old doctor paths.

### Task 5: Verify the complete migration

**Files:**
- Inspect: all changed Java and frontend files

- [ ] Run focused Java tests.
- [ ] Run `mvn -pl medcase-admin -am -DskipTests package`.
- [ ] Run doctor frontend tests and `yarn build:prod`.
- [ ] Run `git diff --check`.
- [ ] Confirm no Git add, commit, reset, checkout, or history-changing command was executed.
