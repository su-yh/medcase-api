# Doctor/Case/User Naming Cleanup Implementation Plan

> **For agentic workers:** This plan is being executed inline in the current task. Git commits are intentionally omitted because the project owner requested read-only Git usage.

**Goal:** Remove stale `Doctor` naming from shared case-portal user and case-business code while preserving names that represent doctor-only administration.

**Architecture:** Shared case-portal authentication, profile, and user data code will use `User` names. Case creation, querying, review, and status code will use `Case` names. Doctor-only administration remains under `Doctor` names, while all references, frontend imports, tests, logs, and comments are updated consistently.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, Vue 3, Vite, Element Plus, Yarn.

**Spec:** Approved naming boundary from the conversation on 2026-09-02.

## Global Constraints

- Do not modify database table or column names.
- Do not change existing API URLs unless the approved naming boundary explicitly requires a stale path to become a user/case path.
- Keep doctor-only administration names such as `DoctorUserAdminController` and `DoctorCaseReviewAdminController`.
- Shared portal user code uses `User`; shared case business code uses `Case`.
- Java opening braces stay at end of lines.
- Do not use Git commit or other Git write operations.
- Run frontend commands only after `nvm use 22.23.2`.

### Task 1: Inventory and classification

**Files:**
- Read all `Doctor`/`doctor` references under `medcase-api`, `medcase-admin`, and `medcase-doctor`.

**Interfaces:**
- Produces the exact shared-user and shared-case rename set, plus a protected doctor-only set.

- [x] Scan Java, resources, admin frontend, and case frontend references.
- [x] Classify names as shared user, shared case business, doctor-only administration, or historical test/mock text.

### Task 2: Rename shared backend user code

**Files:**
- Rename shared authentication, registration SMS, and profile classes from `Doctor...` to `User...`.
- Update imports, constructor fields, method parameters, logs, comments, and tests.

**Interfaces:**
- Produces `UserAuthPortalController`, `UserAuthService`, `UserRegisterSmsCodeService`, `UserProfilePortalController`, `UserProfileService`, `UserProfileVO`, `UserProfileSubmitRequest`, `UserLoginRequest`, `UserRegisterRequest`, and `UserRegisterSmsCodeRequest`.

- [ ] Rename files and classes, including shared `DoctorUser...` types to `User...`.
- [ ] Update all references and tests.
- [ ] Compile the backend.

### Task 3: Rename shared backend case code

**Files:**
- Rename shared case entities, requests, responses, services, mapper, enums, and portal/review controllers from `DoctorCase...` to `Case...`.
- Update XML mapper references and all Java consumers.

**Interfaces:**
- Produces `CaseEntity`, `CaseMapper`, `CaseService`, `CaseReviewService`, `CasePortalController`, `CaseVO`, `CaseReviewVO`, `CasePageRequest`, `CaseSubmitRequest`, `CaseReviewRequest`, `CaseReviewQuery`, and `CaseStatusEnums`. Doctor-specific admin controllers remain named `DoctorCaseReviewAdminController`.

- [ ] Rename files and classes.
- [ ] Update mapper XML and all references.
- [ ] Compile the backend.

### Task 4: Update case-portal frontend naming

**Files:**
- Rename `src/api/doctor` to `src/api/user` for authentication/profile and `src/api/case` for case business.
- Rename shared doctor-named layout, profile view, utilities, store identifiers, token key, imports, tests, and logs.

**Interfaces:**
- Produces user-oriented auth/profile APIs and case-oriented case APIs. Auth/profile URLs are renamed from `/biz/case-auth` and `/biz/case-profile` to `/biz/user-auth` and `/biz/user-profile`; the case URL remains `/biz/cases`.

- [ ] Rename shared frontend files and identifiers.
- [ ] Update frontend tests and imports.
- [ ] Run tests and `yarn build:stage` after `nvm use 22.23.2`.

### Task 5: Protected doctor-only verification and residual scan

**Files:**
- No intentional changes to doctor-only admin files unless a reference points to a renamed shared type.

- [ ] Confirm doctor administration remains named `Doctor`.
- [ ] Scan for stale shared `Doctor` names and classify any remaining result.
- [ ] Run backend compile and frontend build verification.
