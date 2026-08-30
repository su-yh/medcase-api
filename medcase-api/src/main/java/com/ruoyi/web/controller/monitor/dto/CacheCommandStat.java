package com.ruoyi.web.controller.monitor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Redis 命令统计。
 */
@Getter
@AllArgsConstructor
public class CacheCommandStat {

    private String name;
    private String value;
}
