package com.medcase.mp.mybatis;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;

class MapperBoundaryTest {

    private static final Pattern MAPPER_BUILD_PATTERN =
            Pattern.compile("\\b\\w*Mapper\\s*\\.\\s*build\\s*\\(");

    @Test
    void servicesMustNotBuildMybatisWrappers() throws IOException {
        Path sourceRoot = Path.of("src/main/java/com/medcase");
        try (Stream<Path> files = Files.walk(sourceRoot)) {
            List<Path> services = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("Service.java")
                            || path.toString().endsWith("ServiceImpl.java"))
                    .toList();

            for (Path service : services) {
                String source = Files.readString(service);
                assertFalse(MAPPER_BUILD_PATTERN.matcher(source).find(),
                        service + " must not call mapper.build()");
                assertFalse(source.contains("LambdaQueryWrapper"), service
                        + " must not construct LambdaQueryWrapper");
                assertFalse(source.contains("LambdaUpdateWrapper"), service
                        + " must construct LambdaUpdateWrapper");
                assertFalse(source.contains("QueryWrapper"), service
                        + " must construct QueryWrapper");
                assertFalse(source.contains("UpdateWrapper"), service
                        + " must construct UpdateWrapper");
            }
        }
    }
}
