package com.ruoyi.mp.typehandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class AbstractListTypeHandlerTest {
    private final StringListHandler handler = new StringListHandler();

    @Test
    void parseShouldDeserializeListElements() {
        assertEquals(List.of("alpha", "beta"), handler.parse("[\"alpha\",\"beta\"]"));
    }

    @Test
    void toJsonShouldSerializeListElements() {
        assertEquals("[\"alpha\",\"beta\"]", handler.toJson(List.of("alpha", "beta")));
    }

    private static final class StringListHandler extends AbstractListTypeHandler<String> {
        private StringListHandler() {
            super(String.class);
        }
    }
}
