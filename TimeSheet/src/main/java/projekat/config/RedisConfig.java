package projekat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.cache.key-prefix}")
    private String redisPrefix;

    @Value("${spring.cache.redis.time-to-live}")
    private long redisTtl;

    @Bean("redisTemplate")
    @Autowired
    public RedisTemplate<String, Object> deliveryRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(true);
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig();
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        log.info("Redis Cachemanager");
        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .prefixCacheNameWith(redisPrefix)
                        .entryTtl(Duration.ofMillis(redisTtl)))
                .build();
    }

    @Bean("redisTimesheetCache")
    public Cache redisTimesheetCache(CacheManager cacheManager) {
        return cacheManager.getCache("Timesheet");
    }

}
