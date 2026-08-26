# medcase-api Module Consolidation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将四个现有后端 Maven 模块合并为一个包路径和运行行为不变的 `medcase-api` 应用模块。

**Architecture:** 根工程保留为 Maven 父工程并改用 `medcase-api-parent` 坐标；新的 `medcase-api` 子模块承载全部 Java 源码、资源和测试，直接声明合并后的依赖。所有 `com.ruoyi...` 包路径保持不变。

**Tech Stack:** Java 17, Spring Boot 4.1.0, Maven, MyBatis-Plus, Dynamic Datasource, Flyway, Redis, JUnit 5.

---

### Task 1: Create the consolidated module layout

**Files:**
- Create: `medcase-api/pom.xml`
- Create: `medcase-api/src/main/java/`
- Create: `medcase-api/src/main/resources/`
- Create: `medcase-api/src/test/java/`
- Create: `medcase-api/src/test/resources/`
- Move: all source and resource files from the four existing modules into the matching new directories

- [ ] Move each existing module source tree into `medcase-api/src`, preserving relative paths.
- [ ] Merge test resources with identical files only when their contents match.
- [ ] Confirm no source or resource file is silently overwritten.

### Task 2: Rewrite Maven coordinates and dependencies

**Files:**
- Modify: `pom.xml`
- Create: `medcase-api/pom.xml`
- Delete: `medcase-common/pom.xml`
- Delete: `medcase-system/pom.xml`
- Delete: `medcase-framework/pom.xml`
- Delete: `medcase-admin/pom.xml`

- [ ] Change the root artifact to `medcase-api-parent` and keep packaging `pom`.
- [ ] Replace the four modules with one `<module>medcase-api</module>`.
- [ ] Add all direct dependencies required by the merged source tree to the new module.
- [ ] Keep Spring Boot repackaging and resource inclusion on the new application module.
- [ ] Remove dependency-management entries that only reference deleted sibling modules.

### Task 3: Verify source and resource invariants

**Files:**
- Verify: `medcase-api/src/main/java/**/*.java`
- Verify: `medcase-api/src/main/resources/**/*`
- Verify: `medcase-api/src/test/**/*`

- [ ] Confirm every original Java package declaration is unchanged.
- [ ] Confirm Mapper XML locations and application configuration imports remain unchanged.
- [ ] Confirm `com.ruoyi.RuoYiApplication` remains the executable entry point.

### Task 4: Build and test

- [ ] Run `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository clean test`.
- [ ] Run `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api -am package`.
- [ ] Report any warnings separately from failures.
