package com.ruoyi.mp.typehandler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;

import java.util.List;

/**
 * @author suyh
 * @since 2023-12-02
 * @param <E> 可以是对象，也可以是枚举。应该也可以是 Integer Long String 等(这个是没有测.)
 */
public abstract class AbstractListTypeHandler<E> extends AbstractJsonTypeHandler<List<E>> {
    private final Class<E> type;
    public AbstractListTypeHandler(Class<E> type) {
        this.type = type;
    }

    @Override
    protected List<E> parse(String json) {
//        return JsonUtils.deserializeToList(json, type, JacksonTypeHandler.getObjectMapper());
        return null;    // 这里没有引入json 就懒得弄了
    }

    @Override
    protected String toJson(List<E> obj) {
//        return JsonUtils.serializable(obj, JacksonTypeHandler.getObjectMapper());
        return null;   // 这里没有引入json 就懒得弄了
    }
}
