package com.medcase.system.converter;

import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.system.entity.SysDictDataEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemEntityConverterTest {

    @Test
    void copiesDictSortWithoutLosingItsValue() {
        SysDictData source = new SysDictData();
        source.setDictSort(1L);

        SysDictDataEntity target = SystemEntityConverter.toEntity(source);

        assertEquals(Long.valueOf(1L), target.getDictSort());
    }
}
