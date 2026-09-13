package com.medcase.common.core.domain;

import com.medcase.system.entity.SysDeptEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TreeSelectTest {

    @Test
    void exposesEnabledAsNullableBoolean() throws NoSuchFieldException {
        Field enabled = TreeSelect.class.getDeclaredField("enabled");
        assertEquals(Boolean.class, enabled.getType());
        assertFalse(hasField("disabled"));
    }

    @Test
    void copiesDepartmentEnabledStateToTreeNode() {
        SysDeptEntity dept = new SysDeptEntity();
        dept.setDeptId(1L);
        dept.setDeptName("研发部门");
        dept.setEnabled(Boolean.FALSE);
        dept.setChildren(List.of());

        TreeSelect treeSelect = new TreeSelect(dept);

        assertEquals(Boolean.FALSE, treeSelect.getEnabled());
    }

    private boolean hasField(String name) {
        try {
            TreeSelect.class.getDeclaredField(name);
            return true;
        }
        catch (NoSuchFieldException e) {
            return false;
        }
    }
}
