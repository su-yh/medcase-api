package com.medcase.biz.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.medcase.common.enums.BaseEnum;
import lombok.Getter;

/**
 * 医生病例状态
 *
 * @author suyh
 */
@Getter
public enum CaseStatusEnums implements BaseEnum {
    DRAFT("draft", "草稿"),
    PENDING_REVIEW("pending_review", "待审核"),
    REVIEW_FAILED("review_failed", "审核失败"),
    APPROVED_PENDING_SETTLEMENT("approved_pending_settlement", "审核通过 / 待结算"),
    SETTLED("settled", "已结算");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    CaseStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", desc, code);
    }
}
