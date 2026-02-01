package com.org.payment.service.cacheService;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.org.payment.model.dto.cache.CachedIdempotentRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IdempotencyCache {

    private final Logger logger= LogManager.getLogger(IdempotencyCache.class);

    private final Cache<String, CachedIdempotentRecord> cache =
            Caffeine.newBuilder()
                    .expireAfterWrite(15, TimeUnit.MINUTES)
                    .maximumSize(100_000)
                    .build();

    public CachedIdempotentRecord get(String key) {
        return cache.getIfPresent(key);
    }

    public void put(String key, CachedIdempotentRecord record) {
        if(cache.getIfPresent(key)==null){
            logger.info("Adding to Idempotency_Key-cache .");
            cache.put(key, record);
        }else {
            logger.info("Updating to Idempotency_Key-cache .");
            cache.put(key, record);
        }


    }
}

