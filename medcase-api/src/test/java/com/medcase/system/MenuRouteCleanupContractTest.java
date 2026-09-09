package com.medcase.system;

import com.medcase.system.entity.SysMenuEntity;
import com.medcase.system.domain.vo.RouterVo;
import com.medcase.web.controller.system.dto.MenuSaveRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MenuRouteCleanupContractTest {

    @Test
    void removedMenuRouteFieldsAreNotExposed() {
        assertThat(findField(SysMenuEntity.class, "query")).isNull();
        assertThat(findField(SysMenuEntity.class, "isFrame")).isNull();
        assertThat(findField(MenuSaveRequest.class, "query")).isNull();
        assertThat(findField(MenuSaveRequest.class, "isFrame")).isNull();
        assertThat(findField(RouterVo.class, "query")).isNull();
    }

    @Test
    void menuSqlAndServiceDoNotUseRemovedRouteFeatures() throws IOException {
        String mapperXml = Files.readString(Path.of(
                "src/main/resources/mapper/system/SysMenuMapper.xml"));
        String menuService = Files.readString(Path.of(
                "src/main/java/com/medcase/system/service/SysMenuService.java"));

        assertThat(mapperXml).doesNotContain("is_frame", "`query`", "property=\"isFrame\"", "property=\"query\"");
        assertThat(menuService).doesNotContain(
                "isMenuFrame", "isParentView", "isInnerLink", "innerLinkReplaceEach",
                "UserConstants.LAYOUT", "UserConstants.PARENT_VIEW", "UserConstants.INNER_LINK");
    }

    @Test
    void menuColumnsAreRemovedByIncrementalMigrations() throws IOException {
        String queryMigration = Files.readString(Path.of(
                "src/main/resources/db/migration/master/V01_01_00/V01_01_00_006__drop-menu-query.sql"));
        String frameMigration = Files.readString(Path.of(
                "src/main/resources/db/migration/master/V01_01_00/V01_01_00_007__drop-menu-is-frame.sql"));

        assertThat(queryMigration).contains("alter table sys_menu drop column query");
        assertThat(frameMigration).contains("alter table sys_menu drop column is_frame");
    }

    private Field findField(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            return null;
        }
    }
}
