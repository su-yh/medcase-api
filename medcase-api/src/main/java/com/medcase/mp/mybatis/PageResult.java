package com.medcase.mp.mybatis;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public final class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 4844284098660612408L;

    private List<T> list;

    private Integer total;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = toInteger(total);
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = toInteger(total);
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }

    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<>(total);
    }

    private static Integer toInteger(Long total) {
        return total == null ? null : Math.toIntExact(total);
    }

}
