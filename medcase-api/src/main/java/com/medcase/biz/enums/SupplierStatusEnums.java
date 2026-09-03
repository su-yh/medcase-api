package com.medcase.biz.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.medcase.common.enums.BaseEnum;
import lombok.Getter;

/**
 * 供应商状态。
 */
@Getter
public enum SupplierStatusEnums implements BaseEnum {
    NORMAL("0", "正常"),
    DISABLE("1", "停用");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    SupplierStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
