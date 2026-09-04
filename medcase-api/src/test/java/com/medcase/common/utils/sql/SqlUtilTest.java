package com.medcase.common.utils.sql;

import com.medcase.mvc.exception.AbstractBusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqlUtilTest {
    @Test
    void rejectsInvalidOrderByWithBusinessException() {
        assertThatThrownBy(() -> SqlUtil.escapeOrderBySql("id;"))
                .isInstanceOf(AbstractBusinessException.class)
                .satisfies(exception -> assertThat(((AbstractBusinessException) exception)
                        .getEc().getCode()).isEqualTo("error.code.sql.order.by.invalid"));
    }

    @Test
    void rejectsSqlKeywordWithBusinessException() {
        assertThatThrownBy(() -> SqlUtil.filterKeyword("user()"))
                .isInstanceOf(AbstractBusinessException.class)
                .satisfies(exception -> assertThat(((AbstractBusinessException) exception)
                        .getEc().getCode()).isEqualTo("error.code.sql.keyword.invalid"));
    }
}
