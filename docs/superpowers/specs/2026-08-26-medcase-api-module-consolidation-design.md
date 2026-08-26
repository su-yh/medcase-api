# medcase-api Module Consolidation Design

## Goal

将当前 `medcase-common`、`medcase-system`、`medcase-framework` 和 `medcase-admin` 四个后端 Maven 模块合并为一个新的 `medcase-api` 应用模块，同时保持所有 Java 包路径、资源路径和运行行为不变。

## Final Structure

根工程继续作为 Maven 父工程，坐标调整为 `com.medcase:medcase-api-parent`，新增唯一子模块：

```text
medcase-api/
├── pom.xml
└── medcase-api/
    ├── pom.xml
    └── src/
        ├── main/java/
        ├── main/resources/
        └── test/
```

新模块坐标为 `com.medcase:medcase-api`，保留 `com.ruoyi.RuoYiApplication` 作为 Spring Boot 启动类。原四个模块目录移除，前端工程不改动。

## Migration Rules

- 仅移动文件，不修改 Java `package` 声明、类名、接口名和资源相对路径。
- 合并四个模块的 `src/main/java`、`src/main/resources`、`src/test/java` 和 `src/test/resources`。
- 保留配置文件、Mapper XML、日志配置、`META-INF` 自动配置文件和测试扩展配置。
- 新模块直接声明原四个模块所需的依赖，删除模块间依赖。
- 根工程仅保留 `medcase-api` 子模块。
- 不执行 Git 提交。

## Validation

迁移完成后执行：

- `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository clean test`
- `mvn -Dmaven.repo.local=/Users/suyunhong/.m2/repository -pl medcase-api -am package`
- 检查所有 Java 包路径和关键资源文件仍然存在。
