package com.ruoyi.biz.doctor.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertEquals("1", tableLogic.delval());
    }
}
