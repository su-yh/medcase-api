# Config Module Rename Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rename the split configuration files to neutral module names and extract `spring.autoconfigure.exclude` into its own module without changing external config precedence.

**Architecture:** Keep all default module YAML files on the classpath under `medcase-admin/src/main/resources/` so they continue to load as packaged defaults. Preserve `config/application-suyh.yaml` as the external override layer and keep the `spring.config.import` list as the single entry point.

**Tech Stack:** Spring Boot 4 config data, YAML, Maven.

---

### Task 1: Rename and split config modules

**Files:**
- Modify: `medcase-admin/src/main/resources/application-spring-datasource.yaml`
- Create: `medcase-admin/src/main/resources/application-autoconfigure.yaml`
- Rename: `medcase-admin/src/main/resources/application-spring-messages.yaml`
- Rename: `medcase-admin/src/main/resources/application-spring-jackson.yaml`
- Rename: `medcase-admin/src/main/resources/application-spring-redis.yaml`
- Rename: `medcase-admin/src/main/resources/application-spring-servlet.yaml`
- Rename: `medcase-admin/src/main/resources/application-mybatis-plus.yaml`

- [ ] **Step 1: Move `spring.autoconfigure.exclude` into its own YAML module**

Keep this content in the new file:

```yaml
spring:
  autoconfigure:
    exclude:
      - org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
```

Remove that block from the datasource file so datasource stays focused on `spring.flyway` and `spring.datasource.dynamic`.

- [ ] **Step 2: Rename the split module files**

Use module names without the `spring-` prefix:

```text
application-spring-messages.yaml -> application-messages.yaml
application-spring-jackson.yaml  -> application-jackson.yaml
application-spring-redis.yaml    -> application-redis.yaml
application-spring-servlet.yaml  -> application-servlet.yaml
application-mybatis-plus.yaml    -> application-mp.yaml
```

Keep their YAML contents unchanged.

### Task 2: Update config imports

**Files:**
- Modify: `medcase-admin/src/main/resources/application.yml`

- [ ] **Step 1: Rewrite the import list**

Use the renamed module names and add the new autoconfigure module:

```yaml
spring:
  config:
    import:
      - classpath:application-ruoyi.yaml
      - classpath:application-server.yaml
      - classpath:application-logging.yaml
      - classpath:application-user.yaml
      - classpath:application-token.yaml
      - classpath:application-autoconfigure.yaml
      - classpath:application-spring-messages.yaml
      - classpath:application-spring-datasource.yaml
      - classpath:application-spring-servlet.yaml
      - classpath:application-spring-jackson.yaml
      - classpath:application-spring-redis.yaml
      - classpath:application-mybatis-plus.yaml
      - classpath:application-pagehelper.yaml
      - classpath:application-referer.yaml
      - classpath:application-xss.yaml
```

- [ ] **Step 2: Confirm `application.yml` remains the only top-level entry point**

Do not add any new external location settings. Keep packaged defaults and external overrides separate.

### Task 3: Verify the config graph

**Files:**
- Validate: `medcase-admin/src/main/resources/*.yaml`

- [ ] **Step 1: Check the renamed files exist and the old names are gone**

Run:

```bash
rg --files medcase-admin/src/main/resources | rg 'application-(spring-|mybatis-plus|autoconfigure|messages|jackson|redis|servlet|mp)'
```

Expected: only the new filenames remain.

- [ ] **Step 2: Build the backend module**

Run:

```bash
mvn -o -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-admin -am -DskipTests package
```

Expected: `BUILD SUCCESS`
