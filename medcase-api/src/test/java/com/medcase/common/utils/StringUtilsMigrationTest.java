package com.medcase.common.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsMigrationTest {

    private static final Pattern LEGACY_STRING_CHECK = Pattern.compile(
            "StringUtils\\.(isEmpty|isNotEmpty|isBlank|isNotBlank|hasText)\\s*\\(");

    private static final Pattern SPRING_REPLACEABLE_METHOD = Pattern.compile(
            "(?<![\\w.])StringUtils\\.(startsWithIgnoreCase|replace|capitalize|split)\\s*\\(");

    private static final Pattern FULLY_QUALIFIED_LEGACY_SPLIT = Pattern.compile(
            "com\\.medcase\\.common\\.utils\\.StringUtils\\.split\\s*\\(");

    private static final String SPRING_STRING_UTILS_PREFIX =
            "org.springframework.util.StringUtils.";

    private static final Pattern IMPORT_SPRING_STRING_UTILS = Pattern.compile(
            "import\\s+org\\.springframework\\.util\\.StringUtils;");

    private static final Pattern IMPORT_LEGACY_STRING_UTILS = Pattern.compile(
            "import\\s+com\\.medcase\\.common\\.utils\\.StringUtils;");

    @Test
    void replacesLegacyStringChecksWithSpringStringUtils() throws IOException {

        Path sourceRoot = Paths.get("src/main/java");
        Set<Path> legacyFiles;
        try (Stream<Path> paths = Files.walk(sourceRoot)) {

            legacyFiles = paths
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> containsLegacyStringCheck(readSource(path)))
                    .map(sourceRoot::relativize)
                    .collect(Collectors.toCollection(HashSet::new));
        }

        assertThat(legacyFiles).isEmpty();
    }

    @Test
    void replacesSpringCompatibleStringUtilsMethods() throws IOException {

        Path sourceRoot = Paths.get("src/main/java");
        Set<Path> legacyFiles;
        try (Stream<Path> paths = Files.walk(sourceRoot)) {

            legacyFiles = paths
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> containsLegacySpringCompatibleMethod(readSource(path)))
                    .map(sourceRoot::relativize)
                    .collect(Collectors.toCollection(HashSet::new));
        }

        assertThat(legacyFiles).isEmpty();
    }

    @Test
    void removesProjectStringUtilsReferences() throws IOException {

        Path sourceRoot = Paths.get("src/main/java");
        Set<Path> legacyFiles;
        try (Stream<Path> paths = Files.walk(sourceRoot)) {

            legacyFiles = paths
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.endsWith("common/utils/StringUtils.java"))
                    .filter(path -> {
                        String source = readSource(path);
                        return source.contains("com.medcase.common.utils.StringUtils")
                                || IMPORT_LEGACY_STRING_UTILS.matcher(source).find();
                    })
                    .map(sourceRoot::relativize)
                    .collect(Collectors.toCollection(HashSet::new));
        }

        assertThat(legacyFiles).isEmpty();
    }

    private boolean containsLegacySpringCompatibleMethod(String source) {

        return (IMPORT_LEGACY_STRING_UTILS.matcher(source).find()
                && SPRING_REPLACEABLE_METHOD.matcher(source).find())
                || FULLY_QUALIFIED_LEGACY_SPLIT.matcher(source).find();
    }

    private boolean containsLegacyStringCheck(String source) {

        if (IMPORT_SPRING_STRING_UTILS.matcher(source).find()) {

            return false;
        }
        String sourceWithoutSpringCalls = source.replace(SPRING_STRING_UTILS_PREFIX, "");
        return IMPORT_LEGACY_STRING_UTILS.matcher(source).find()
                && Arrays.stream(sourceWithoutSpringCalls.split("\\R"))
                .anyMatch(line -> LEGACY_STRING_CHECK.matcher(line).find());
    }

    private String readSource(Path path) {

        try {

            return Files.readString(path);
        }
        catch (IOException e) {

            throw new IllegalStateException("Unable to read source file: " + path, e);
        }
    }
}
