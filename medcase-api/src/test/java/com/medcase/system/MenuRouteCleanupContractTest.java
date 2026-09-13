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
    void menuBaselineUsesFinalColumnsAndData() throws IOException {
        String schema = Files.readString(Path.of(
                "src/main/resources/db/migration/master/V01_00_00/"
                        + "V01_00_00_001__system-schema.sql"));
        String systemData = Files.readString(Path.of(
                "src/main/resources/db/migration/master/V01_00_00/"
                        + "V01_00_00_003__system-data.sql"));
        String businessData = Files.readString(Path.of(
                "src/main/resources/db/migration/master/V01_00_00/"
                        + "V01_00_00_004__business-data.sql"));

        assertThat(schema).contains(
                "id                  bigint",
                "route_path",
                "vue_component_path",
                "visible             tinyint",
                "create unique index uk_sys_menu_route_name");
        assertThat(schema).doesNotContain(
                "menu_id           bigint        not null auto_increment",
                "  path ",
                "  component ",
                "  query ",
                "  is_frame ",
                "  is_cache ");
        assertThat(systemData).contains(
                "SystemUser", "SystemRole", "SystemMenu", "MonitorOperlog");
        assertThat(businessData).contains(
                "BizCaseDoctor", "BizDoctor", "BizPatient", "BizCasePatient", "Supplier");
        assertThat(systemData).doesNotContain(
                "(1040,", "(1042,", "(1043,", "(1045,", "(501,");
        assertThat(systemData).doesNotContain("alter table", "update sys_menu", "delete from sys_menu");
        assertThat(businessData).doesNotContain("alter table", "update sys_menu", "delete from sys_menu");
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
