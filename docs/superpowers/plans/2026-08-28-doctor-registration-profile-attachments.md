# Doctor Registration Profile Attachments Implementation Plan

> **For agentic workers:** This plan is being executed inline in the current workspace. No Git write operations are permitted.

**Goal:** Extend doctor registration with identity and qualification information, persist the new fields in `sys_user`, and expose them in admin doctor management.

**Architecture:** Reuse `sys_user.nick_name` for the doctor's name and add only the missing identity, title, and certificate fields. Registration accepts multipart form data, creates the doctor first to obtain `userId`, uploads three MinIO attachments through the existing storage service, then updates the same user record with JSON-backed `FileAttachment` values. Admin APIs return the new fields and the existing attachment preview component renders the images.

**Tech Stack:** Spring Boot 4, MyBatis-Plus, JacksonTypeHandler, MinIO, JUnit 5/Mockito, Vue 3, Element Plus, Axios, Vitest.

**Spec:** Confirmed in the conversation on 2026-08-28.

## Global Constraints

- Do not execute Git commit, add, push, reset, checkout, or history-changing commands.
- Do not execute the incremental SQL; only create and list it for manual execution.
- Java changes use opening braces at end of the line.
- POJOs use Lombok `@Data` where applicable.
- Attachment fields are stored as JSON with MyBatis-Plus `JacksonTypeHandler`.
- Frontend commands must run after `nvm use 22.23.2`.

### Task 1: Backend Registration Contract and Persistence

**Files:**
- Modify: `medcase-api/src/main/java/com/ruoyi/biz/domain/DoctorUserEntity.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/biz/request/DoctorRegisterRequest.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/biz/response/DoctorUserVO.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/biz/response/DoctorProfileVO.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/biz/request/DoctorProfileSubmitRequest.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/biz/service/DoctorProfileService.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/biz/service/DoctorAuthService.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/biz/controller/DoctorAuthPortalController.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/biz/mapper/DoctorUserMapper.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/storage/enums/FileBusinessEnums.java`
- Modify: `medcase-api/src/main/java/com/ruoyi/mvc/constants/enums/ErrorCodeEnums.java`
- Modify: `medcase-api/src/main/resources/i18n/messages.properties`
- Create: `sql/V01_00_00_006__doctor.sql`
- Test: `medcase-api/src/test/java/com/ruoyi/biz/service/DoctorAuthServiceTest.java`
- Test: `medcase-api/src/test/java/com/ruoyi/biz/controller/DoctorAuthPortalControllerTest.java`
- Test: `medcase-api/src/test/java/com/ruoyi/biz/domain/DoctorUserEntityTest.java`

**Interfaces:**
- Registration accepts `multipart/form-data` fields `username`, `password`, `phone`, `code`, `name`, `idCardNumber`, `title`, `idCardFront`, `idCardBack`, and `qualificationCertificate`.
- `DoctorAuthService.register(DoctorRegisterRequest)` remains the service entry point and uploads the three attachments after insert.
- `FileBusinessEnums` adds `ID_CARD_FRONT`, `ID_CARD_BACK`, and `QUALIFICATION_CERTIFICATE` without user-type restrictions.
- `DoctorUserEntity` exposes `idCardNumber`, `idCardFront`, `idCardBack`, `title`, and `qualificationCertificate`; image properties use `FileAttachment`.

### Task 2: Doctor Frontend Registration and Profile Resubmission

**Files:**
- Modify: `medcase-doctor/src/views/auth/RegisterView.vue`
- Modify: `medcase-doctor/src/api/doctor/auth.js`
- Modify: `medcase-doctor/src/stores/user.js`
- Modify: `medcase-doctor/src/views/profile/DoctorProfileView.vue`
- Modify: `medcase-doctor/src/api/doctor/profile.js`
- Test: existing doctor auth/profile tests, adding multipart field assertions.

**Interfaces:**
- Registration builds `FormData` and sends one file per certificate field through the existing doctor request client.
- Profile data includes identity number, title, and the three attachment metadata values so rejected doctors can resubmit complete information.

### Task 3: Admin Doctor Management Display

**Files:**
- Modify: `medcase-admin/src/views/biz/doctor/index.vue`
- Modify: `medcase-admin/src/api/biz/doctor.js`
- Test: `medcase-admin/src/views/biz/doctor/mock.test.js` or a focused doctor view test.

**Interfaces:**
- Admin list/detail/review responses expose name, identity number, title, and certificate attachments.
- Identity number is masked in list/detail text display.
- Certificate attachments use the existing `AttachmentPreviewDialog` and preview utilities.

### Task 4: Verification

- Run backend focused tests and compile with Maven.
- Run doctor frontend tests and production build after `nvm use 22.23.2`.
- Run admin frontend tests and production build after `nvm use 22.23.2`.
- Inspect diffs and verify the SQL file is present but not executed.
