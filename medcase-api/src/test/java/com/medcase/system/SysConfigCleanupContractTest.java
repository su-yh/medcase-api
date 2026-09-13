package com.medcase.system;

import com.medcase.web.controller.system.dto.LoginUserInfoResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SysConfigCleanupContractTest {

    private static final Path SYSTEM_DATA = Path.of(
            "src/main/resources/db/migration/master/V01_00_00/"
                    + "V01_00_00_003__system-data.sql");
    private static final Path LOGIN_CONTROLLER = Path.of(
            "src/main/java/com/medcase/web/controller/system/SysLoginController.java");

    @Test
    void removedConfigRowsAreAbsentFromSystemBaseline() throws IOException {
        String systemData = Files.readString(SYSTEM_DATA);

        assertThat(systemData).doesNotContain(
                "sys.index.skinName",
                "sys.user.initPassword",
                "sys.index.sideTheme",
                "sys.account.initPasswordModify",
                "sys.account.passwordValidateDays",
                "sys.account.chrtype");
        assertThat(systemData).contains(
                "sys.account.captchaEnabled",
                "sys.account.registerUser",
                "sys.login.blackIPList");
    }

    @Test
    void loginInfoResponseOnlyContainsUserAndPermissionData() throws Exception {
        assertThat(Arrays.stream(LoginUserInfoResponse.class.getDeclaredFields())
                .map(Field::getName)
                .toList())
                .containsExactlyInAnyOrder("user", "roles", "permissions");

        String loginController = Files.readString(LOGIN_CONTROLLER);
        assertThat(loginController).doesNotContain(
                "SysConfigService",
                "getSysAccountChrtype",
                "initPasswordIsModify",
                "passwordIsExpiration",
                "pwdChrtype",
                "defaultModifyPwd",
                "passwordExpired");
    }
}
