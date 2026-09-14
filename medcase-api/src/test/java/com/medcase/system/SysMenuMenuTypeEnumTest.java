package com.medcase.system;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.medcase.common.enums.MenuTypeEnums;
import com.medcase.system.entity.SysMenuEntity;
import com.medcase.web.controller.system.dto.MenuSaveRequest;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class SysMenuMenuTypeEnumTest {

    @Test
    void menuTypeUsesEnumAcrossEntityAndRequest() throws NoSuchFieldException {
        assertThat(SysMenuEntity.class.getDeclaredField("menuType").getType())
                .isEqualTo(MenuTypeEnums.class);
        assertThat(MenuSaveRequest.class.getDeclaredField("menuType").getType())
                .isEqualTo(MenuTypeEnums.class);
        assertThat(MenuSaveRequest.class.getDeclaredField("menuType")
                .isAnnotationPresent(NotNull.class)).isTrue();
    }

    @Test
    void menuTypeEnumKeepsExistingDatabaseAndJsonCodes() throws NoSuchFieldException {
        Field codeField = MenuTypeEnums.class.getDeclaredField("code");

        assertThat(codeField.isAnnotationPresent(EnumValue.class)).isTrue();
        assertThat(codeField.isAnnotationPresent(JsonValue.class)).isTrue();
        assertThat(MenuTypeEnums.DIRECTORY.getCode()).isEqualTo("M");
        assertThat(MenuTypeEnums.MENU.getCode()).isEqualTo("C");
        assertThat(MenuTypeEnums.BUTTON.getCode()).isEqualTo("F");
    }
}
