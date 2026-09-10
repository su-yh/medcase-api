package com.medcase.system;

import com.medcase.system.entity.SysMenuEntity;
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
        assertThat(Files.exists(Path.of("src/main/java/com/medcase/system/domain/vo/RouterVo.java"))).isFalse();
    }

    @Test
    void menuUsesNewColumnNamesAndVisibleBoolean() throws NoSuchFieldException {
        assertThat(findField(SysMenuEntity.class, "id")).isNotNull();
        assertThat(findField(SysMenuEntity.class, "routePath")).isNotNull();
        assertThat(findField(SysMenuEntity.class, "vueComponentPath")).isNotNull();
        assertThat(SysMenuEntity.class.getDeclaredField("visible").getType()).isEqualTo(Boolean.class);
        assertThat(findField(SysMenuEntity.class, "children")).isNull();
        assertThat(findField(SysMenuEntity.class, "parentName")).isNull();

        assertThat(findField(MenuSaveRequest.class, "id")).isNotNull();
        assertThat(findField(MenuSaveRequest.class, "routePath")).isNotNull();
        assertThat(findField(MenuSaveRequest.class, "vueComponentPath")).isNotNull();
        assertThat(MenuSaveRequest.class.getDeclaredField("visible").getType()).isEqualTo(Boolean.class);
    }

    @Test
    void menuEndpointsReturnFlatMenuEntities() throws IOException {
        String loginController = Files.readString(Path.of(
                "src/main/java/com/medcase/web/controller/system/SysLoginController.java"));
        String menuController = Files.readString(Path.of(
                "src/main/java/com/medcase/web/controller/system/SysMenuController.java"));

        assertThat(loginController).contains("public List<SysMenuEntity> getRouters");
        assertThat(menuController).contains("public List<SysMenuEntity> treeselect");
    }

    @Test
    void menuSqlAndServiceDoNotUseRemovedRouteFeatures() throws IOException {
        String mapperXml = Files.readString(Path.of(
                "src/main/resources/mapper/system/SysMenuMapper.xml"));
        String menuService = Files.readString(Path.of(
                "src/main/java/com/medcase/system/service/SysMenuService.java"));

        assertThat(mapperXml).doesNotContain("is_frame", "`query`", "property=\"isFrame\"", "property=\"query\"");
        assertThat(mapperXml).doesNotContain("property=\"parentName\"");
        assertThat(menuService).doesNotContain(
                "isMenuFrame", "isParentView", "isInnerLink", "innerLinkReplaceEach",
                "UserConstants.LAYOUT", "UserConstants.PARENT_VIEW", "UserConstants.INNER_LINK",
                "RouterVo", "TreeSelect", "MetaVo", "buildMenus", "buildMenuTreeSelect");
    }

    @Test
    void menuTreeConversionIsNotKeptInBackendService() throws IOException {
        String menuService = Files.readString(Path.of(
                "src/main/java/com/medcase/system/service/SysMenuService.java"));
        assertThat(menuService).doesNotContain("buildMenus", "buildMenuTreeSelect", "RouterVo", "TreeSelect");
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

    @Test
    void newMenuContractIsMigratedIncrementally() throws IOException {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/master/V01_01_00/V01_01_00_008__rename-menu-columns.sql"));

        assertThat(migration).contains(
                "rename column menu_id to id",
                "rename column path to route_path",
                "rename column component to vue_component_path",
                "modify column visible tinyint",
                "update sys_menu set visible",
                "create unique index uk_sys_menu_route_name");
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
