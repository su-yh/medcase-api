# System Entity Direct Use Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Use the new MyBatis-Plus system entities directly inside persistence and service flows while keeping Controller request and response DTOs independent from database entities.

**Architecture:** Association-table writes and reads will use `SysUserPostEntity`, `SysUserRoleEntity`, `SysRoleDeptEntity`, and `SysRoleMenuEntity` directly. Login and operation logs will use entity objects for persistence, with dedicated monitor request/response DTOs for query fields and API output. Notice-read writes will use `SysNoticeReadEntity`; joined notice/read-user results remain dedicated query results.

**Tech Stack:** Java 17, Spring Boot 4, MyBatis-Plus, Lombok, PageHelper, Vue 3 admin client.

**Spec:** User request in the current conversation.

## Global Constraints

- Do not perform Git commits, resets, checkout, or other write operations through Git.
- Keep Java opening braces at the end of the declaration line.
- Do not use `var` in functional code.
- Do not expose persistence entities as Controller request or response contracts.
- Preserve existing endpoint JSON field names unless the frontend is changed in the same task.

---

### Task 1: Replace association-table domain objects in internal flows

**Files:**
- Modify: `medcase-api/src/main/java/com/medcase/system/service/impl/SysUserServiceImpl.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/service/impl/SysRoleServiceImpl.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/service/ISysRoleService.java`
- Modify: `medcase-api/src/main/java/com/medcase/web/controller/system/SysRoleController.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/converter/SystemEntityConverter.java`

- [ ] Use the four `*Entity` types directly when constructing and passing association records.
- [ ] Use a dedicated Controller request DTO for role authorization cancellation rather than exposing an entity.
- [ ] Remove converter methods that are no longer referenced and verify no association domain type remains in functional code.

### Task 2: Use log entities in services and create monitor DTO contracts

**Files:**
- Create: `medcase-api/src/main/java/com/medcase/web/controller/monitor/dto/LogininforQueryRequest.java`
- Create: `medcase-api/src/main/java/com/medcase/web/controller/monitor/dto/LogininforResponse.java`
- Create: `medcase-api/src/main/java/com/medcase/web/controller/monitor/dto/OperLogQueryRequest.java`
- Create: `medcase-api/src/main/java/com/medcase/web/controller/monitor/dto/OperLogResponse.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/service/ISysLogininforService.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/service/impl/SysLogininforServiceImpl.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/service/ISysOperLogService.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/service/impl/SysOperLogServiceImpl.java`
- Modify: `medcase-api/src/main/java/com/medcase/web/controller/monitor/SysLogininforController.java`
- Modify: `medcase-api/src/main/java/com/medcase/web/controller/monitor/SysOperlogController.java`
- Modify: `medcase-api/src/main/java/com/medcase/framework/aspectj/LogAspect.java`
- Modify: `medcase-api/src/main/java/com/medcase/framework/manager/factory/AsyncFactory.java`

- [ ] Keep query-only fields in request DTOs and map only the fields used by the existing monitor pages.
- [ ] Return response DTOs with the existing frontend property names.
- [ ] Use `SysLogininforEntity` and `SysOperLogEntity` directly for inserts and mapper results.
- [ ] Preserve the operation-log query's `businessTypes` filter as a request-only field.

### Task 3: Use notice-read entity for writes without exposing it

**Files:**
- Modify: `medcase-api/src/main/java/com/medcase/system/service/impl/SysNoticeReadServiceImpl.java`
- Modify: `medcase-api/src/main/java/com/medcase/system/converter/SystemEntityConverter.java`

- [ ] Construct `SysNoticeReadEntity` directly for single and batch mark-read operations.
- [ ] Keep joined notice and read-user result contracts separate from `SysNoticeReadEntity`.
- [ ] Remove the notice-read domain conversion methods if unused.

### Task 4: Verify API and persistence behavior

**Files:**
- Test: existing `medcase-api/src/test`

- [ ] Compile `medcase-api` with its reactor dependencies.
- [ ] Run the module tests.
- [ ] Search for remaining unnecessary conversions and Controller signatures exposing the seven persistence entities.

