package com.medcase.common.enums;

import com.medcase.biz.request.SupplierSaveRequest;
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
        assertEquals("1", NormalDisableEnums.DISABLE.getCode());
        assertEquals("Y", YesNoEnums.YES.getCode());
        assertEquals("1", NoticeTypeEnums.NOTICE.getCode());
        assertEquals("1", NoticeStatusEnums.CLOSED.getCode());
    }

    @Test
    void bindsFixedCodesFromJson() throws Exception {
        SupplierSaveRequest supplier = JsonUtils.parseObject(
                "{\"sex\":\"0\"}", SupplierSaveRequest.class);
        SysConfigEntity config = JsonUtils.parseObject(
                "{\"configType\":\"Y\"}", SysConfigEntity.class);
        SysNoticeEntity notice = JsonUtils.parseObject(
                "{\"noticeType\":\"1\",\"status\":\"0\"}", SysNoticeEntity.class);
        SysUserEntity user = JsonUtils.parseObject(
                "{\"sex\":\"2\",\"status\":\"1\"}", SysUserEntity.class);

        assertEquals(UserSexEnums.MALE, supplier.getSex());
        assertEquals(YesNoEnums.YES, config.getConfigType());
        assertEquals(NoticeTypeEnums.NOTICE, notice.getNoticeType());
        assertEquals(NoticeStatusEnums.NORMAL, notice.getStatus());
        assertEquals(UserSexEnums.UNKNOWN, user.getSex());
        assertEquals(UserStatusEnums.DISABLE, user.getStatus());
    }
}
