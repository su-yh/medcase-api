# MyBatis-Plus Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Introduce MyBatis-Plus with its pagination interceptor while preserving existing PageHelper-based pagination for legacy RuoYi code.

**Architecture:** MyBatis-Plus auto-configuration will own the `SqlSessionFactory` via `MybatisSqlSessionFactoryBean`. PageHelper remains on the classpath and attaches its interceptor to the same factory, but old code must continue using `startPage()` and new MP code must use `IPage` without calling PageHelper for the same query.

**Tech Stack:** Spring Boot 4.1.0, MyBatis-Plus Spring Boot4 starter, PageHelper Spring Boot starter, MySQL.

---

### Task 1: Replace MyBatis starter management with MyBatis-Plus

**Files:**
- Modify: `/Users/suyunhong/suyh-develop/github/medcase-api/pom.xml`
- Modify: `/Users/suyunhong/suyh-develop/github/medcase-api/medcase-framework/pom.xml`

- [ ] **Step 1: Update root dependency management**

Add:

```xml
<mybatis-plus.version>3.5.17</mybatis-plus.version>
```

Replace managed `org.mybatis.spring.boot:mybatis-spring-boot-starter` with:

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot4-starter</artifactId>
    <version>${mybatis-plus.version}</version>
</dependency>
```

- [ ] **Step 2: Add actual framework module dependency**

Add this dependency to `medcase-framework/pom.xml`:

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot4-starter</artifactId>
</dependency>
```

### Task 2: Switch configuration properties to MyBatis-Plus

**Files:**
- Modify: `/Users/suyunhong/suyh-develop/github/medcase-api/medcase-admin/src/main/resources/application.yml`

- [ ] **Step 1: Exclude old MyBatis auto-configuration**

Add under `spring:`:

```yaml
  autoconfigure:
    exclude:
      - org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
```

- [ ] **Step 2: Replace the MyBatis properties block**

Replace:

```yaml
mybatis:
  typeAliasesPackage: com.ruoyi.**.domain
  mapperLocations: classpath*:mapper/**/*Mapper.xml
  configLocation: classpath:mybatis/mybatis-config.xml
```

with:

```yaml
mybatis-plus:
  type-aliases-package: com.ruoyi.common.core.domain,com.ruoyi.framework.web.domain,com.ruoyi.system.domain
  mapper-locations: classpath*:mapper/**/*Mapper.xml
  config-location: classpath:mybatis/mybatis-config.xml
  check-config-location: true
```

### Task 3: Replace custom SqlSessionFactory with MyBatis-Plus plugin configuration

**Files:**
- Delete: `/Users/suyunhong/suyh-develop/github/medcase-api/medcase-framework/src/main/java/com/ruoyi/framework/config/MyBatisConfig.java`
- Create: `/Users/suyunhong/suyh-develop/github/medcase-api/medcase-framework/src/main/java/com/ruoyi/framework/config/MybatisPlusConfig.java`

- [ ] **Step 1: Delete the old custom factory**

Remove the class that creates `SqlSessionFactoryBean` manually.

- [ ] **Step 2: Add the MyBatis-Plus pagination interceptor**

Create:

```java
package com.ruoyi.framework.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig
{
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor()
    {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### Task 4: Verify migration boundaries

**Files:**
- Read-only verification across `/Users/suyunhong/suyh-develop/github/medcase-api`

- [ ] **Step 1: Confirm old factory is gone**

Run:

```bash
rg -n "SqlSessionFactoryBean|MyBatisConfig|mybatis:" /Users/suyunhong/suyh-develop/github/medcase-api --glob '!target/**'
```

Expected: no custom `SqlSessionFactoryBean`; no active `mybatis:` block in `application.yml`.

- [ ] **Step 2: Confirm MyBatis-Plus config is present**

Run:

```bash
rg -n "mybatis-plus|MybatisPlusInterceptor|PaginationInnerInterceptor|mybatis-plus-spring-boot4-starter" /Users/suyunhong/suyh-develop/github/medcase-api --glob '!target/**'
```

Expected: root dependency management, framework dependency, YAML properties, and config class are present.

- [ ] **Step 3: Validate XML files**

Run:

```bash
xmllint --noout /Users/suyunhong/suyh-develop/github/medcase-api/pom.xml /Users/suyunhong/suyh-develop/github/medcase-api/medcase-framework/pom.xml
```

Expected: exit code 0.

- [ ] **Step 4: Build if Maven is available**

Run:

```bash
cd /Users/suyunhong/suyh-develop/github/medcase-api && mvn -pl medcase-admin -am -DskipTests package
```

Expected: exit code 0. If `mvn` is unavailable, report that build verification could not be executed.
