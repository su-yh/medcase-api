# Doctor Case Workbench Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the doctor case submission and read-only case status workbench across `medcase-api` and `medcase-doctor`, while leaving the admin frontend and admin case operations untouched.

**Architecture:** Add a new `doctor_case` table and MyBatis-Plus entity/mapper in `medcase-system`. Expose doctor-scoped create, paged list, and detail endpoints from `medcase-admin`; status changes are intentionally out of scope and will be performed by future admin functionality. Replace the doctor placeholder home page with an authenticated layout, case status tabs, a text submission view, pagination, and a read-only detail/failure-reason dialog.

**Tech Stack:** Spring Boot 4, MyBatis-Plus, Lombok, Vue 3, Element Plus, Pinia, Vue Router, Axios, Vite, Yarn.

---

### Task 1: Add the doctor case persistence model

**Files:**
- Create: `sql/doctor_case.sql`
- Create: `medcase-system/src/main/java/com/ruoyi/system/enums/DoctorCaseStatusEnums.java`
- Create: `medcase-system/src/main/java/com/ruoyi/system/domain/DoctorCaseEntity.java`
- Create: `medcase-system/src/main/java/com/ruoyi/system/mapper/DoctorCaseMapper.java`

- [ ] Add the `doctor_case` table with `case_id`, `doctor_id`, `case_content`, `status`, `review_reason`, `review_time`, `settled_time`, `create_time`, `update_time`, and `del_flag`.
- [ ] Map status with an enum persisted through `@EnumValue` and expose its code/description through `@JsonValue`.
- [ ] Mark `del_flag` with `@TableLogic`; keep all table fields in the new `DoctorCaseEntity`.
- [ ] Extend `BaseMapperX<DoctorCaseEntity>` and add mapper methods for doctor-scoped page and detail queries using lambda wrappers only.

### Task 2: Add doctor case service and API contracts

**Files:**
- Create: `medcase-admin/src/main/java/com/ruoyi/web/controller/doctor/request/DoctorCaseSubmitRequest.java`
- Create: `medcase-admin/src/main/java/com/ruoyi/web/controller/doctor/response/DoctorCaseVO.java`
- Modify: `medcase-admin/src/main/java/com/ruoyi/mvc/constants/enums/ErrorCodeEnums.java`
- Create: `medcase-admin/src/main/java/com/ruoyi/web/service/DoctorCaseService.java`
- Create: `medcase-admin/src/main/java/com/ruoyi/web/controller/doctor/DoctorCaseController.java`

- [ ] Validate non-blank case text and return `ExceptionUtil.business(...)` for doctor-case failures.
- [ ] Resolve the authenticated doctor ID from `SecurityUtils`; reject non-doctor accounts before querying or inserting.
- [ ] Create submitted cases with `PENDING_REVIEW`; never expose admin review mutations from doctor endpoints.
- [ ] Return `PageResult<DoctorCaseVO>` for the list and `DoctorCaseVO` for detail.
- [ ] Add `@Anonymous` only to auth endpoints; case endpoints remain authenticated through the existing security configuration.

### Task 3: Add doctor frontend case APIs and authenticated layout

**Files:**
- Create: `medcase-doctor/src/api/doctor/cases.js`
- Create: `medcase-doctor/src/layouts/DoctorLayout.vue`
- Modify: `medcase-doctor/src/router/index.js`
- Modify: `medcase-doctor/src/main.js`

- [ ] Add API functions for create, page query, and detail query.
- [ ] Make `/home` redirect into an authenticated `/cases` route rendered inside `DoctorLayout`.
- [ ] Add sidebar navigation for case center, submit case, profile placeholder, and logout.
- [ ] Keep all case routes behind the existing auth guard.

### Task 4: Implement case list, status tabs, detail, and failure reason

**Files:**
- Create: `medcase-doctor/src/views/cases/CaseCenterView.vue`
- Create: `medcase-doctor/src/utils/doctorCase.js`

- [ ] Render tabs for all, pending review, review failed, approved/pending settlement, and settled.
- [ ] Query status and pagination from the backend; do not locally invent status transitions.
- [ ] Show only read-only actions: detail and failure reason.
- [ ] Use a detail dialog to show case content, current status, review time, settlement time, and failure reason when present.

### Task 5: Implement case submission

**Files:**
- Create: `medcase-doctor/src/views/cases/SubmitCaseView.vue`

- [ ] Provide a text area for case content and a submit action.
- [ ] On successful submission, show a success message and return to the case center.
- [ ] Preserve the doctor-only boundary: no review or settlement controls appear on this page.

### Task 6: Verify both repositories

**Files:**
- Modify only files required by the preceding tasks.

- [ ] Run `mvn -pl medcase-admin -am -DskipTests compile`.
- [ ] Run doctor frontend unit tests with `yarn test`.
- [ ] Run `yarn build:prod`.
- [ ] Run `git diff --check` in both repositories.
- [ ] Confirm no admin frontend files or admin review endpoints were modified.
