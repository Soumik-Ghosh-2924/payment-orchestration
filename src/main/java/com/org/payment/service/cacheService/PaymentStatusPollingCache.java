package com.org.payment.service.cacheService;

import com.org.payment.model.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStatusPollingCache {

    private static final String CACHE_NAME = "paymentStatus";
    private final CacheManager cacheManager;

    public void put(String paymentId, PaymentStatus status) {
        cache().put(paymentId, status);
        log.info("Updated payment status in Cache!");
    }

    public PaymentStatus get(String paymentId) {
        Cache.ValueWrapper wrapper = cache().get(paymentId);
        log.info("Returning status-info from Cache....");
        return wrapper != null ? (PaymentStatus) wrapper.get() : null;
    }

    public void evict(String paymentId) {
        cache().evict(paymentId);
    }

    private Cache cache() {
        return cacheManager.getCache(CACHE_NAME);
    }
}

