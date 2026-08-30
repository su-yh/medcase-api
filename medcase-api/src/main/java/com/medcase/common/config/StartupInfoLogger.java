package com.medcase.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;

/**
 * 应用启动完成后输出构建与版本信息。
 *
 * @author medcase
 */
@Component
public class StartupInfoLogger implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(StartupInfoLogger.class);
    private static final String UNKNOWN = "unknown";
    private static final String DEFAULT_PROFILE = "default";

    private final Environment environment;
    private final ObjectProvider<BuildProperties> buildPropertiesProvider;
    private final ObjectProvider<GitProperties> gitPropertiesProvider;

    public StartupInfoLogger(
            Environment environment,
            ObjectProvider<BuildProperties> buildPropertiesProvider,
            ObjectProvider<GitProperties> gitPropertiesProvider) {
        this.environment = environment;
        this.buildPropertiesProvider = buildPropertiesProvider;
        this.gitPropertiesProvider = gitPropertiesProvider;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logStartupInfo();
    }

    void logStartupInfo() {
        BuildProperties buildProperties = buildPropertiesProvider.getIfAvailable();
        GitProperties gitProperties = gitPropertiesProvider.getIfAvailable();

        log.info("\n" +
                        "----------------------------------------------------------\n" +
                        "Application Ready\n" +
                        "Spring Boot     : {}\n" +
                        "Project Version : {}\n" +
                        "Build Time      : {}\n" +
                        "Active Profile  : {}\n" +
                        "Database Enabled : {}\n" +
                        "Database Primary : {}\n" +
                        "Database Strict  : {}\n" +
                        "Database JDBC URL: {}\n" +
                        "Database Driver  : {}\n" +
                        "Database Pool Min : {}\n" +
                        "Database Pool Max : {}\n" +
                        "Database Conn Timeout: {}\n" +
                        "Database Idle Timeout: {}\n" +
                        "Database Max Lifetime: {}\n" +
                        "Redis Host       : {}\n" +
                        "Redis Port       : {}\n" +
                        "Redis Database   : {}\n" +
                        "Redis Timeout    : {}\n" +
                        "Redis Pool Min   : {}\n" +
                        "Redis Pool Max   : {}\n" +
                        "Redis Pool Active: {}\n" +
                        "Redis Pool Wait  : {}\n" +
                        "SMS Enabled      : {}\n" +
                        "Git Branch      : {}\n" +
                        "Git Commit      : {}\n" +
                        "Git Commit Time : {}\n" +
                        "Git Dirty       : {}\n" +
                        "----------------------------------------------------------",
                valueOrUnknown(SpringBootVersion.getVersion()),
                buildProperties == null ? UNKNOWN : valueOrUnknown(buildProperties.getVersion()),
                buildProperties == null ? UNKNOWN : valueOrUnknown(buildProperties.getTime()),
                activeProfiles(),
                property("spring.datasource.dynamic.enabled"),
                property("spring.datasource.dynamic.primary"),
                property("spring.datasource.dynamic.strict"),
                sanitizedJdbcUrl(),
                property("spring.datasource.dynamic.hikari.master.driver-class-name"),
                property("spring.datasource.dynamic.hikari.master.minimum-idle"),
                property("spring.datasource.dynamic.hikari.master.maximum-pool-size"),
                property("spring.datasource.dynamic.hikari.master.connection-timeout"),
                property("spring.datasource.dynamic.hikari.master.idle-timeout"),
                property("spring.datasource.dynamic.hikari.master.max-lifetime"),
                property("spring.data.redis.host"),
                property("spring.data.redis.port"),
                property("spring.data.redis.database"),
                property("spring.data.redis.timeout"),
                property("spring.data.redis.lettuce.pool.min-idle"),
                property("spring.data.redis.lettuce.pool.max-idle"),
                property("spring.data.redis.lettuce.pool.max-active"),
                property("spring.data.redis.lettuce.pool.max-wait"),
                property("sms.aliyun.enabled"),
                gitProperties == null ? UNKNOWN : valueOrUnknown(gitProperties.getBranch()),
                gitProperties == null ? UNKNOWN : valueOrUnknown(gitProperties.getShortCommitId()),
                gitProperties == null ? UNKNOWN : valueOrUnknown(gitProperties.getCommitTime()),
                gitProperties == null ? UNKNOWN : valueOrUnknown(gitProperties.get("dirty")));
    }

    private String property(String key) {
        return valueOrUnknown(environment.getProperty(key));
    }

    private String sanitizedJdbcUrl() {
        String jdbcUrl = environment.getProperty(
                "spring.datasource.dynamic.hikari.master.jdbc-url");
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return UNKNOWN;
        }
        return jdbcUrl.replaceAll(
                "(?i)([?&](?:user(?:name)?|password)=[^&]*)", "$1***");
    }

    private String activeProfiles() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            return DEFAULT_PROFILE;
        }
        return String.join(",", Arrays.asList(activeProfiles));
    }

    private String valueOrUnknown(String value) {
        return value == null || value.isBlank() ? UNKNOWN : value;
    }

    private String valueOrUnknown(Instant value) {
        return value == null ? UNKNOWN : value.toString();
    }
}
