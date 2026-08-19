package com.ruoyi.mp.mybatis;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 分页参数
@NoArgsConstructor
@Data
public class PageParam implements Serializable {
    private static final long serialVersionUID = -4369470413351363288L;

    public static final Integer PAGE_NO = 1;
    public static final Integer PAGE_SIZE = 10;

    // 页码最小值为 1
    private Integer pageNo = PAGE_NO;

    // 每页条数不能为空
    // 每页条数最小值为 1
    private Integer pageSize = PAGE_SIZE;

    /**
     * 是否进行 count 查询
     */
    @JsonIgnore
    protected boolean searchCount = true;

    @JsonIgnore
    public int getPageStart() {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 手动分页，将所有数据得到，然后按分页参数要求，进行分页，返回当前指定的分页数据。
     */
    @NonNull
    public static <T> List<T> doPageList(PageParam pageParam, List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }

        int startIndex = pageParam.getPageStart();

        if (startIndex >= list.size()) {
            return new ArrayList<>();
        }

        int lastIndex = pageParam.getPageStart() + pageParam.getPageSize();
        if (lastIndex >= list.size()) {
            lastIndex = list.size();
        }

        return list.subList(startIndex, lastIndex);
    }

    public <T> Page<T> toPage() {
        return MyBatisUtils.buildPage(this);
    }
}
