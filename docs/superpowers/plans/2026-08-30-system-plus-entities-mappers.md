# System Plus Entities And Mappers Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add MyBatis-Plus persistence entities and `BaseMapperX` mappers for every database-table domain object currently under `com.medcase.system.domain`.

**Architecture:** Keep the existing XML-based `com.medcase.system.domain` and `com.medcase.system.mapper` contracts unchanged. Add pure persistence models under `com.medcase.system.plus.entity` and empty MyBatis mapper interfaces under `com.medcase.system.plus.mapper`; mapper scanning already covers the new package.

**Tech Stack:** Java 17, Spring Boot 4.1.0, MyBatis-Plus 3.5.17, Lombok.

**Spec:** Current user request: migrate database-table entities under `com.medcase.system.domain` into `com.medcase.system.plus.entity`, with one `XxxEntity` and one `XxxMapper` per table; each mapper extends `BaseMapperX`.

## Global Constraints

- Do not modify or remove existing XML mappers and service usage.
- Do not create entities for runtime-only `SysCache` or `SysUserOnline`.
- Persistence entities contain database columns only; query-only fields are excluded.
- Composite-key association tables do not receive a fabricated single `@TableId`.
- Keep Java braces at end of lines.
- Do not perform Git commits.

### Task 1: Add System Persistence Entities

**Files:**
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysConfigEntity.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysLogininforEntity.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysNoticeEntity.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysNoticeReadEntity.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysOperLogEntity.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysPostEntity.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysRoleDeptEntity.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysRoleMenuEntity.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysUserPostEntity.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/entity/SysUserRoleEntity.java`

- [ ] Add `@Data` and `@TableName` to each entity.
- [ ] Add `@TableId(type = IdType.AUTO)` only to auto-increment primary-key tables.
- [ ] Preserve database field names through MyBatis-Plus camel-case mapping.
- [ ] Retain audit columns (`createBy`, `updateBy`, `remark`) where the table has them.
- [ ] Exclude query-only fields `isRead`, `flag`, and `businessTypes`.

### Task 2: Add BaseMapperX Interfaces

**Files:**
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysConfigMapper.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysLogininforMapper.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysNoticeMapper.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysNoticeReadMapper.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysOperLogMapper.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysPostMapper.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysRoleDeptMapper.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysRoleMenuMapper.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysUserPostMapper.java`
- Create: `medcase-api/src/main/java/com/medcase/system/plus/mapper/SysUserRoleMapper.java`

- [ ] Annotate each interface with `@Mapper`.
- [ ] Extend `BaseMapperX<MatchingEntity>`.
- [ ] Keep each interface empty until a new-architecture service needs custom methods.

### Task 3: Verify Compilation

**Files:**
- Verify: all files listed above.

- [ ] Compile the `medcase-api` module with Maven.
- [ ] Confirm mapper scanning includes `com.medcase.system.plus.mapper`.
- [ ] Confirm no old package references were changed.
- [ ] Run `git diff --check`.
