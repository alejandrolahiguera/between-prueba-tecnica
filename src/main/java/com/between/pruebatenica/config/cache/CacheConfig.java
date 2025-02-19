package com.between.pruebatenica.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${cache.users.info.ttl:5}")
    private long cacheUsersInfoTtl;

    @Value("${cache.users.info.max-size:5}")
    private long cacheUsersInfoMaxSize;

    public static final String USERS_INFO_CACHE = "USERS_INFO_CACHE";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(USERS_INFO_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(cacheUsersInfoTtl, TimeUnit.MINUTES)
                .maximumSize(cacheUsersInfoMaxSize));
        cacheManager.setAsyncCacheMode(true);
        return cacheManager;
    }

}
