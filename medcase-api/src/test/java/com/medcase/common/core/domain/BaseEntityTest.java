package com.medcase.common.core.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseEntityTest {

    @Test
    void shouldNotDeclareSearchValue() {
        assertThrows(NoSuchFieldException.class, () -> BaseEntity.class.getDeclaredField("searchValue"));
    }
}
