package com.medcase.system.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.storage.pojo.FileAttachment;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class SysUserEntityTest {

    @Test
    void delFlagShouldBeLogicDeleteField() throws NoSuchFieldException {
        Field field = SysUserEntity.class.getDeclaredField("delFlag");
        TableLogic tableLogic = field.getAnnotation(TableLogic.class);

        assertNotNull(tableLogic);
        assertEquals("", tableLogic.value());
        assertEquals("", tableLogic.delval());
        assertEquals(Boolean.class, field.getType());
    }

    @Test
    void containsSystemAndBusinessFields() throws NoSuchFieldException {
        assertNotNull(SysUserEntity.class.getDeclaredField("deptId"));
        assertNotNull(SysUserEntity.class.getDeclaredField("supplierId"));
    }

    @Test
    void exposesPendingReviewStatus() {
        assertEquals("3", UserStatusEnums.PENDING_REVIEW.getCode());
        assertEquals("待审核", UserStatusEnums.PENDING_REVIEW.getDesc());
    }

    @Test
    void exposesReviewFailedStatus() {
        assertEquals("4", UserStatusEnums.REVIEW_FAILED.getCode());
        assertEquals("审核失败", UserStatusEnums.REVIEW_FAILED.getDesc());
    }

    @Test
    void exposesRegisterStatus() {
        assertEquals("5", UserStatusEnums.REGISTER.getCode());
        assertEquals("注册", UserStatusEnums.REGISTER.getDesc());
    }

    @Test
    void doesNotExposeDeletedStatusBecauseDeletionUsesDelFlag() {
        assertThrows(IllegalArgumentException.class, () -> UserStatusEnums.valueOf("DELETED"));
    }

    @Test
    void exposesDoctorRegistrationProfileFields() throws NoSuchFieldException {
        assertEquals(String.class, SysUserEntity.class
                .getDeclaredField("idCardNumber").getType());
        assertEquals(String.class, SysUserEntity.class
                .getDeclaredField("title").getType());
        assertEquals(FileAttachment.class, SysUserEntity.class
                .getDeclaredField("idCardFront").getType());
        assertEquals(FileAttachment.class, SysUserEntity.class
                .getDeclaredField("idCardBack").getType());
        assertEquals(FileAttachment.class, SysUserEntity.class
                .getDeclaredField("qualificationCertificate").getType());
    }

    @Test
    void doctorAttachmentFieldsUseJacksonTypeHandler() throws NoSuchFieldException {
        assertJacksonTypeHandler("idCardFront");
        assertJacksonTypeHandler("idCardBack");
        assertJacksonTypeHandler("qualificationCertificate");
    }

    private void assertJacksonTypeHandler(String fieldName) throws NoSuchFieldException {
        TableField tableField = SysUserEntity.class
                .getDeclaredField(fieldName).getAnnotation(TableField.class);
        assertNotNull(tableField);
        assertEquals(JacksonTypeHandler.class, tableField.typeHandler());
    }
}
