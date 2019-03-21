package com.lifeshs.product.service.common;

import com.lifeshs.product.common.constants.base.CacheType;
import net.sf.ehcache.Ehcache;

/**
 * @author Yue.Li
 * @date 3/14/17.
 */
public interface ICacheService {
    void saveKeyValue(CacheType cacheType, String key, Object value);
    boolean delCacheValue(CacheType cacheType, String key);
    Object getCacheValue(CacheType cacheType, String key);
    Ehcache getCache(String cacheName);
}

