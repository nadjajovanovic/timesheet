package projekat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisCacheService {

    @Autowired
    private Cache cache;

    @Autowired
    private CacheManager cacheManager;

    public <T> T getFromCache(Integer objectCacheKey, Class<T> type) {
        T storedData = null;
        try {
            storedData = cache.get(objectCacheKey, type);
        } catch (Exception e) {
            //log.warn("Could not read object from cache", e);
        }
        return storedData;
    }

    public void storeInCache(Integer objectCacheKey, Object data) {
        cache.put(objectCacheKey, data);
    }

    public void removeFromCache(List<Integer> objectCacheKey) {
        for (var key:objectCacheKey) {
            cache.evict(key);
        }
    }

    public void getCachedKeys(String redisCacheKeyPrefix) {
        // TODO implement
    }

}
