package com.ruoyi.common.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.env.MockEnvironment;

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class StartupInfoLoggerTest {
    @Test
    void logsBuildGitAndProfileInfo(CapturedOutput output) {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.profiles.active", "dev");
        StartupInfoLogger logger = new StartupInfoLogger(
                environment,
                provider(buildProperties()),
                provider(gitProperties()));

        logger.logStartupInfo();

        assertThat(output).contains("Project Version : 1.2.3");
        assertThat(output).contains("Active Profile  : dev");
        assertThat(output).contains("Git Branch      : main");
        assertThat(output).contains("Git Commit      : abc1234");
    }

    @Test
    void logsFallbackInfoWhenBuildAndGitPropertiesAreMissing(CapturedOutput output) {
        StartupInfoLogger logger = new StartupInfoLogger(
                new MockEnvironment(),
                provider(null),
                provider(null));

        logger.logStartupInfo();

        assertThat(output).contains("Project Version : unknown");
        assertThat(output).contains("Active Profile  : default");
        assertThat(output).contains("Git Branch      : unknown");
        assertThat(output).contains("Git Commit      : unknown");
    }

    private static BuildProperties buildProperties() {
        Properties properties = new Properties();
        properties.setProperty("version", "1.2.3");
        return new BuildProperties(properties);
    }

    private static GitProperties gitProperties() {
        Properties properties = new Properties();
        properties.setProperty("branch", "main");
        properties.setProperty("commit.id.abbrev", "abc1234");
        return new GitProperties(properties);
    }

    private static <T> ObjectProvider<T> provider(T bean) {
        return new ObjectProvider<>() {
            @Override
            public T getObject(Object... args) {
                return bean;
            }

            @Override
            public T getIfAvailable() {
                return bean;
            }

            @Override
            public T getIfUnique() {
                return bean;
            }

            @Override
            public T getObject() {
                return bean;
            }

            @Override
            public Iterator<T> iterator() {
                return bean == null ? Collections.emptyIterator() : Collections.singleton(bean).iterator();
            }

            @Override
            public Stream<T> stream() {
                return bean == null ? Stream.empty() : Stream.of(bean);
            }
        };
    }
}
