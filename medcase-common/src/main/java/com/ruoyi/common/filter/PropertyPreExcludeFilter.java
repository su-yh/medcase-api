package com.ruoyi.common.filter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 排除JSON敏感属性
 * 
 * @author ruoyi
 */
public class PropertyPreExcludeFilter
{
    private final Set<String> excludes = new LinkedHashSet<>();

    public PropertyPreExcludeFilter()
    {
    }

    public PropertyPreExcludeFilter addExcludes(String... filters)
    {
        for (int i = 0; i < filters.length; i++)
        {
            this.excludes.add(filters[i]);
        }
        return this;
    }

    public Set<String> getExcludes()
    {
        return excludes;
    }
}
