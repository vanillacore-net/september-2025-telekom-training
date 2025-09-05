package com.telekom.training.day2.customercaching.fixed;

import com.telekom.training.day2.customercaching.initial.Customer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy Pattern: Proxy
 * Controls access to RealSubject and adds caching functionality
 */
public class CachedCustomerServiceProxy implements CustomerService {
    
    private final CustomerService realService;
    private final Map<String, CacheEntry> cache;
    private final long cacheTimeoutMs;
    private long cacheHits = 0;
    private long cacheMisses = 0;

    public CachedCustomerServiceProxy(CustomerService realService, long cacheTimeoutMs) {
        this.realService = realService;
        this.cache = new HashMap<>();
        this.cacheTimeoutMs = cacheTimeoutMs;
    }

    @Override
    public Customer getCustomer(String customerId) {
        String cacheKey = "basic_" + customerId;
        
        // Check cache first
        CacheEntry entry = cache.get(cacheKey);
        if (entry != null && !entry.isExpired()) {
            cacheHits++;
            System.out.println("⚡ CACHE HIT for customer: " + customerId + " (hits: " + cacheHits + ")");
            return entry.getCustomer();
        }
        
        // Cache miss - call real service
        cacheMisses++;
        System.out.println("💾 CACHE MISS for customer: " + customerId + " (misses: " + cacheMisses + ")");
        
        Customer customer = realService.getCustomer(customerId);
        cache.put(cacheKey, new CacheEntry(customer));
        
        return customer;
    }

    @Override
    public Customer getCustomerWithTariffHistory(String customerId) {
        String cacheKey = "history_" + customerId;
        
        CacheEntry entry = cache.get(cacheKey);
        if (entry != null && !entry.isExpired()) {
            cacheHits++;
            System.out.println("⚡ CACHE HIT for customer with history: " + customerId + " (hits: " + cacheHits + ")");
            return entry.getCustomer();
        }
        
        cacheMisses++;
        System.out.println("💾 CACHE MISS for customer with history: " + customerId + " (misses: " + cacheMisses + ")");
        
        Customer customer = realService.getCustomerWithTariffHistory(customerId);
        cache.put(cacheKey, new CacheEntry(customer));
        
        return customer;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        // Update invalidates cache
        invalidateCustomerCache(customer.getCustomerId());
        
        return realService.updateCustomer(customer);
    }

    private void invalidateCustomerCache(String customerId) {
        cache.remove("basic_" + customerId);
        cache.remove("history_" + customerId);
        System.out.println("🗑️  CACHE INVALIDATED for customer: " + customerId);
    }

    public void clearCache() {
        cache.clear();
        System.out.println("🗑️  CACHE CLEARED");
    }

    public CacheStats getCacheStats() {
        return new CacheStats(cacheHits, cacheMisses, cache.size());
    }

    // Inner class for cache entry
    private class CacheEntry {
        private final Customer customer;
        private final LocalDateTime timestamp;

        public CacheEntry(Customer customer) {
            this.customer = customer;
            this.timestamp = LocalDateTime.now();
        }

        public Customer getCustomer() {
            return customer;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(timestamp.plusNanos(cacheTimeoutMs * 1_000_000));
        }
    }

    // Cache statistics class
    public static class CacheStats {
        private final long hits;
        private final long misses;
        private final int size;

        public CacheStats(long hits, long misses, int size) {
            this.hits = hits;
            this.misses = misses;
            this.size = size;
        }

        public long getHits() { return hits; }
        public long getMisses() { return misses; }
        public int getSize() { return size; }
        public double getHitRatio() { 
            return hits + misses == 0 ? 0.0 : (double) hits / (hits + misses);
        }

        @Override
        public String toString() {
            return String.format("CacheStats{hits=%d, misses=%d, size=%d, hitRatio=%.2f%%}", 
                hits, misses, size, getHitRatio() * 100);
        }
    }
}