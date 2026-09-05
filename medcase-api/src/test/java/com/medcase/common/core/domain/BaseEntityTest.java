package com.medcase.common.core.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseEntityTest {

    @Test
    void shouldNotDeclareSearchValue() {
        assertThrows(NoSuchFieldException.class, () -> BaseEntity.class.getDeclaredField("searchValue"));
    }

    @Test
    void shouldNotDeclareParams() {
        assertThrows(NoSuchFieldException.class, () -> BaseEntity.class.getDeclaredField("params"));
        assertThrows(NoSuchMethodException.class, () -> BaseEntity.class.getDeclaredMethod("getParams"));
        assertThrows(NoSuchMethodException.class, () -> BaseEntity.class.getDeclaredMethod("setParams", java.util.Map.class));
    }
}
