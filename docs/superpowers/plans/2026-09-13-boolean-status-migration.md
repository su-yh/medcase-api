# Boolean Status Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the NormalDisableEnums, NoticeStatusEnums, and YesNoEnums contracts with Boolean properties backed by tinyint columns using 1=true and 0=false across medcase-api and medcase-admin.

**Architecture:** Use positive semantic property names for enabled state (`enabled`) and built-in configuration (`builtIn`) while preserving `isDefault` for the existing default flag. Update the original schema/seed SQL and the historical menu migration so a fresh Flyway chain reaches one consistent schema without adding a new migration. Publish the API and admin changes together because the JSON request and response contracts change.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, Flyway, Vue 3, Element Plus, Maven, Yarn.

**Spec:** User-approved Boolean migration: `NormalDisableEnums`, `NoticeStatusEnums`, and `YesNoEnums` become Boolean; database columns use `tinyint` with `1=true` and `0=false`; API and admin change together; edit original SQL rather than add an incremental migration; account for historical incremental SQL.

## Global Constraints

* Use `Boolean` wrapper types for nullable entity, DTO, VO, and query fields.
* Do not change `UserStatusEnums`, `SupplierStatusEnums`, `CaseStatusEnums`, login status, or other unrelated status contracts.
* `enabled=true` means normal/open/enabled; `enabled=false` means disabled/closed.
* `builtIn=true` means system-built-in; `isDefault=true` means the dictionary item is the default.
* Database status and yes/no columns in scope are `tinyint`, with `1=true` and `0=false`.
* Modify the existing schema and migration SQL files; do not add a new migration file.
* Keep each SQL statement to one operation and preserve existing migration ordering.

---

### Task 1: Backend Boolean Contract and Historical SQL

**Files:**
* Modify: `medcase-api/src/main/java/com/medcase/system/entity/*` for the eight affected system entities
* Modify: `medcase-api/src/main/java/com/medcase/web/controller/system/dto/*` for affected requests/responses
* Modify: affected system services, mappers, controllers, XML mapper files, and tests
* Modify: `medcase-api/src/main/resources/db/migration/master/V01_00_00/V01_00_00_001__system.sql`
* Modify: `medcase-api/src/main/resources/db/migration/master/V01_00_00/V01_00_00_002__biz-menu.sql`
* Modify: `medcase-api/src/main/resources/db/migration/master/V01_00_00/V01_00_00_004__supplier.sql`
* Modify: `medcase-api/src/main/resources/db/migration/master/V01_01_00/V01_01_00_008__rename-menu-columns.sql`
* Test: `medcase-api/src/test/java/com/medcase/common/enums/FixedCodeEnumsTest.java` and affected service/cache tests

**Interfaces:**
* Produces `enabled: Boolean` on department, post, role, menu, dictionary type, dictionary data, and notice entity/API contracts.
* Produces `builtIn: Boolean` on configuration entity/API contracts.
* Produces `isDefault: Boolean` on dictionary data entity/API contracts.
* Leaves user, supplier, case, and login `status` fields unchanged.

### Task 2: Admin Boolean Contract

**Files:**
* Modify: `medcase-admin/src/constants/system.js`
* Modify: affected system views, notice header detail, dictionary detail, and role API module
* Test: existing admin API/component tests plus targeted source contract tests where available

**Interfaces:**
* Sends and receives `enabled`, `builtIn`, and `isDefault` as JSON booleans.
* Uses `undefined` for an unselected query value and `true`/`false` for selected options.

### Task 3: Verification

**Files:**
* No additional files.

**Checks:**
* Search for stale affected enum names and old API field names in the in-scope code.
* Run focused Maven tests and the full backend test suite if feasible.
* Run admin test and production build commands available in the repository.
* Inspect the final diff and both repository statuses.
