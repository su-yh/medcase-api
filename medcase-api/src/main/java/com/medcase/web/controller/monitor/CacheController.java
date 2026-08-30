package com.medcase.web.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.constant.CacheConstants;
import com.medcase.common.utils.StringUtils;
import com.medcase.system.domain.SysCache;
import com.medcase.web.controller.monitor.dto.CacheCommandStat;
import com.medcase.web.controller.monitor.dto.CacheInfoResponse;

/**
 * 缓存监控
 * 
 */
@RestController
@RequestMapping("/monitor/cache")
public class CacheController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final static List<SysCache> caches = new ArrayList<SysCache>(); {

        caches.add(new SysCache(CacheConstants.LOGIN_TOKEN_KEY, "用户信息"));
        caches.add(new SysCache(CacheConstants.SYS_CONFIG_KEY, "配置信息"));
        caches.add(new SysCache(CacheConstants.SYS_DICT_KEY, "数据字典"));
        caches.add(new SysCache(CacheConstants.CAPTCHA_CODE_KEY, "验证码"));
        caches.add(new SysCache(CacheConstants.REPEAT_SUBMIT_KEY, "防重提交"));
        caches.add(new SysCache(CacheConstants.RATE_LIMIT_KEY, "限流处理"));
        caches.add(new SysCache(CacheConstants.PWD_ERR_CNT_KEY, "密码错误次数"));
    }

    @SuppressWarnings("deprecation")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping()
    public CacheInfoResponse getInfo() throws Exception {

        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info());
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize());

        List<CacheCommandStat> pieList = new ArrayList<>();
        commandStats.stringPropertyNames().forEach(key -> {
            String property = commandStats.getProperty(key);
            pieList.add(new CacheCommandStat(
                    StringUtils.removeStart(key, "cmdstat_"),
                    StringUtils.substringBetween(property, "calls=", ",usec")));
        });
        return new CacheInfoResponse(info, dbSize, pieList);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getNames")
    public List<SysCache> cache() {

        return caches;
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getKeys/{cacheName}")
    public Set<String> getCacheKeys(@PathVariable String cacheName) {

        Set<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        return new TreeSet<>(cacheKeys);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getValue/{cacheName}/{cacheKey}")
    public SysCache getCacheValue(@PathVariable String cacheName, @PathVariable String cacheKey) {

        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        SysCache sysCache = new SysCache(cacheName, cacheKey, cacheValue);
        return sysCache;
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheName/{cacheName}")
    public void clearCacheName(@PathVariable String cacheName) {

        Collection<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        redisTemplate.delete(cacheKeys);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheKey/{cacheKey}")
    public void clearCacheKey(@PathVariable String cacheKey) {

        redisTemplate.delete(cacheKey);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheAll")
    public void clearCacheAll() {

        Collection<String> cacheKeys = redisTemplate.keys("*");
        redisTemplate.delete(cacheKeys);
    }
}
