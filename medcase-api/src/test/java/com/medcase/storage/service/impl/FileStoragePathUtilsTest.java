package com.medcase.storage.service.impl;

import com.medcase.common.enums.UserTypeEnums;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class FileStoragePathUtilsTest {
    private static final Pattern UUID_WITHOUT_HYPHENS =
            Pattern.compile("[0-9a-f]{32}");

    @Test
    void shouldUseBusinessDateUserAndFilenamePrefix() {
        String path = FileStoragePathUtils.createPath(
                "case", UserTypeEnums.DOCTOR, 12L, "病例报告.pdf");

        String[] parts = path.split("/", 2);
        assertThat(parts).hasSize(2);
        assertThat(parts[0])
                .isEqualTo("case");
        assertThat(parts[1]).matches(
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                        + "-01-12/" + UUID_WITHOUT_HYPHENS.pattern()
                        + "-病例报告\\.pdf");
    }

    @Test
    void shouldRemoveSpecialCharactersLimitPrefixAndStopAtFirstDot() {
        String path = FileStoragePathUtils.createPath(
                "common", UserTypeEnums.ADMIN, 1L, "病例 报告@2026.v1.final.pdf");

        assertThat(path).matches("common/"
                + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-00-1/" + UUID_WITHOUT_HYPHENS.pattern()
                + "-病例报告2026\\.pdf");
    }

    @Test
    void shouldLimitFilenamePrefixToTenCharacters() {
        String path = FileStoragePathUtils.createPath(
                "case", UserTypeEnums.DOCTOR, 12L, "abcdefghijklmnop.txt");

        assertThat(path).endsWith("-abcdefghij.txt");
    }
}
