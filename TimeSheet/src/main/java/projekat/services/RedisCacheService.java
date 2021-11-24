package projekat.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RedisCacheService {

    @Autowired
    @Qualifier("redisTimesheetCache")
    private final Cache cache;

    @Autowired
    private final CacheManager cacheManager;

    @Autowired
    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheService(Cache cache, CacheManager cacheManager, RedisTemplate<String, Object> redisTemplate) {
        this.cache = cache;
        this.cacheManager = cacheManager;
        this.redisTemplate = redisTemplate;
    }

    public <T> T getFromCache(Integer objectCacheKey, Class<T> type) {
        T storedData = null;
        try {
            storedData = cache.get(objectCacheKey, type);
        } catch (Exception e) {
            log.warn("Could not read object from cache", e);
        }
        return storedData;
    }

    public void storeInCache(Integer objectCacheKey, Object data) {
        cache.put(objectCacheKey, data);
    }

    public void removeFromCache(List<Integer> objectCacheKey) {
        objectCacheKey.forEach(cache::evictIfPresent);
    }

    public Set<String> getCachedKeys(String redisCacheKeyPrefix) {
      return redisTemplate.keys(redisCacheKeyPrefix);
    }

}
