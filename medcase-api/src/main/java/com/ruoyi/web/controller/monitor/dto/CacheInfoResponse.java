package com.ruoyi.web.controller.monitor.dto;

import java.util.List;
import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Redis 缓存监控响应数据。
 */
@Getter
@AllArgsConstructor
public class CacheInfoResponse {

    private Properties info;
    private Object dbSize;
    private List<CacheCommandStat> commandStats;
}
