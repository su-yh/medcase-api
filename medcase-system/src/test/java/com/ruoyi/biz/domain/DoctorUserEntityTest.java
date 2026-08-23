package com.ruoyi.biz.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.baomidou.mybatisplus.annotation.TableLogic;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class DoctorUserEntityTest {

    @Test
    void delFlagShouldBeLogicDeleteField() throws NoSuchFieldException {
        Field field = DoctorUserEntity.class.getDeclaredField("delFlag");
        TableLogic tableLogic = field.getAnnotation(TableLogic.class);

        assertNotNull(tableLogic);
        assertEquals("0", tableLogic.value());
        assertEquals("2", tableLogic.delval());
        assertEquals(Boolean.class, field.getType());
    }

    @Test
    void doesNotContainDepartmentField() {
        assertThrows(NoSuchFieldException.class,
                () -> DoctorUserEntity.class.getDeclaredField("deptId"));
    }
}
