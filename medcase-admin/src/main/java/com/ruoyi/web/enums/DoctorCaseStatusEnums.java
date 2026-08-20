package com.ruoyi.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ruoyi.common.enums.BaseEnums;
import lombok.Getter;

/**
 * 医生病例状态
 *
 * @author suyh
 */
@Getter
public enum DoctorCaseStatusEnums implements BaseEnums {
    PENDING_REVIEW("pending_review", "待审核"),
    REVIEW_FAILED("review_failed", "审核失败"),
    APPROVED_PENDING_SETTLEMENT("approved_pending_settlement", "审核通过 / 待结算"),
    SETTLED("settled", "已结算");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    DoctorCaseStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DoctorCaseStatusEnums fromCode(String code) {
        for (DoctorCaseStatusEnums value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", desc, code);
    }
}
