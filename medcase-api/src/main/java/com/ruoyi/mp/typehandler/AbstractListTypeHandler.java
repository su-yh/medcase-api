package com.ruoyi.mp.typehandler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.ruoyi.common.utils.json.JsonUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author suyh
 * @since 2023-12-02
 * @param <E> 可以是对象，也可以是枚举。应该也可以是 Integer Long String 等(这个是没有测.)
 */
public abstract class AbstractListTypeHandler<E> extends AbstractJsonTypeHandler<List<E>> {
    private final Class<E> elementType;

    public AbstractListTypeHandler(Class<E> elementType) {
        super(List.class);
        this.elementType = Objects.requireNonNull(elementType, "elementType cannot be null");
    }

    @Override
    public List<E> parse(String json) {
        return JsonUtils.parseArray(json, elementType);
    }

    @Override
    public String toJson(List<E> value) {
        return JsonUtils.toJSONString(value);
    }
}
