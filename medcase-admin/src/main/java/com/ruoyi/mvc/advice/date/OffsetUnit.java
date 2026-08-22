package com.ruoyi.mvc.advice.date;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @author suyh
 * @since 2026-08-22
 */
@Getter
public enum OffsetUnit {
    DAY(TimeUnit.DAYS),
    HOUR(TimeUnit.HOURS),
    MINUTE(TimeUnit.MINUTES),
    ;

    private final TimeUnit timeUnit;

    OffsetUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long toMillis(int amount) {
        return timeUnit.toMillis(amount);
    }
}
