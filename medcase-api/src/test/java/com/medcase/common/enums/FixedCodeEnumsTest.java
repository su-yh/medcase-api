package com.medcase.common.enums;

import com.medcase.biz.request.SupplierSaveRequest;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.entity.SysNoticeEntity;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.entity.SysConfigEntity;
import com.medcase.common.utils.json.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedCodeEnumsTest {

    @Test
    void exposesExistingDictionaryCodes() {
        assertEquals("0", UserSexEnums.MALE.getCode());
        assertEquals("1", NoticeTypeEnums.NOTICE.getCode());
    }

    @Test
    void bindsFixedCodesFromJson() throws Exception {
        SupplierSaveRequest supplier = JsonUtils.parseObject(
                "{\"sex\":\"0\"}", SupplierSaveRequest.class);
        SysConfigEntity config = JsonUtils.parseObject(
                "{\"builtIn\":true}", SysConfigEntity.class);
        SysDictDataEntity dictData = JsonUtils.parseObject(
                "{\"isDefault\":true,\"enabled\":false}", SysDictDataEntity.class);
        SysNoticeEntity notice = JsonUtils.parseObject(
                "{\"noticeType\":\"1\",\"enabled\":true}", SysNoticeEntity.class);
        SysUserEntity user = JsonUtils.parseObject(
                "{\"sex\":\"2\",\"status\":\"1\"}", SysUserEntity.class);

        assertEquals(UserSexEnums.MALE, supplier.getSex());
        assertEquals(Boolean.TRUE, config.getBuiltIn());
        assertEquals(Boolean.TRUE, dictData.getIsDefault());
        assertEquals(Boolean.FALSE, dictData.getEnabled());
        assertEquals(NoticeTypeEnums.NOTICE, notice.getNoticeType());
        assertEquals(Boolean.TRUE, notice.getEnabled());
        assertEquals(UserSexEnums.UNKNOWN, user.getSex());
        assertEquals(UserStatusEnums.DISABLE, user.getStatus());
    }
}
