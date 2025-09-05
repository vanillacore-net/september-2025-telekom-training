package com.telekom.training.day2;

import com.telekom.training.day2.customercaching.fixed.*;
import com.telekom.training.day2.customercaching.initial.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test demonstrating performance improvement with Proxy pattern caching
 */
class CustomerCachingTest {

    @Test
    @DisplayName("Initial: No caching - every call hits database")
    void testNoCaching() {
        com.telekom.training.day2.customercaching.initial.DatabaseCustomerService service = 
            new com.telekom.training.day2.customercaching.initial.DatabaseCustomerService();
        
        long startTime = System.currentTimeMillis();
        
        // Multiple calls for same customer - all hit database
        service.getCustomer("CUST001");
        service.getCustomer("CUST001");
        service.getCustomer("CUST001");
        
        long duration = System.currentTimeMillis() - startTime;
        long dbCalls = service.getDbCallCount();
        
        System.out.println("📊 No Caching - Duration: " + duration + "ms, DB Calls: " + dbCalls);
        
        assertEquals(3, dbCalls, "Should make 3 database calls without caching");
        assertTrue(duration >= 300, "Should take at least 300ms (3 * 100ms) without caching");
    }

    @Test
    @DisplayName("Fixed: Proxy caching improves performance significantly")
    void testProxyCaching() {
        DatabaseCustomerService realService = new DatabaseCustomerService();
        CachedCustomerServiceProxy proxy = new CachedCustomerServiceProxy(realService, 5000); // 5 second cache
        
        long startTime = System.currentTimeMillis();
        
        // First call - cache miss
        Customer customer1 = proxy.getCustomer("CUST001");
        
        // Second and third calls - cache hits
        Customer customer2 = proxy.getCustomer("CUST001");
        Customer customer3 = proxy.getCustomer("CUST001");
        
        long duration = System.currentTimeMillis() - startTime;
        CachedCustomerServiceProxy.CacheStats stats = proxy.getCacheStats();
        
        System.out.println("📊 With Caching - Duration: " + duration + "ms");
        System.out.println("📊 Cache Stats: " + stats);
        
        // Verify same customer returned
        assertEquals(customer1.getCustomerId(), customer2.getCustomerId());
        assertEquals(customer2.getCustomerId(), customer3.getCustomerId());
        
        // Performance improvement
        assertEquals(1, stats.getMisses(), "Should have 1 cache miss");
        assertEquals(2, stats.getHits(), "Should have 2 cache hits");
        assertTrue(duration < 200, "Should be much faster with caching (< 200ms vs 300ms+)");
        assertTrue(stats.getHitRatio() > 0.5, "Hit ratio should be > 50%");
    }

    @Test
    @DisplayName("Cache invalidation on updates")
    void testCacheInvalidation() {
        DatabaseCustomerService realService = new DatabaseCustomerService();
        CachedCustomerServiceProxy proxy = new CachedCustomerServiceProxy(realService, 5000);
        
        // Load customer into cache
        Customer original = proxy.getCustomer("CUST001");
        
        // Verify it's cached
        proxy.getCustomer("CUST001");
        assertEquals(1, proxy.getCacheStats().getHits());
        
        // Update customer - should invalidate cache
        proxy.updateCustomer(original);
        
        // Next call should be cache miss (cache invalidated)
        proxy.getCustomer("CUST001");
        
        CachedCustomerServiceProxy.CacheStats finalStats = proxy.getCacheStats();
        assertEquals(2, finalStats.getMisses(), "Update should have invalidated cache");
    }

    @Test
    @DisplayName("Cache timeout works correctly")
    void testCacheTimeout() throws InterruptedException {
        DatabaseCustomerService realService = new DatabaseCustomerService();
        CachedCustomerServiceProxy proxy = new CachedCustomerServiceProxy(realService, 100); // Very short timeout
        
        // Load into cache
        proxy.getCustomer("CUST001");
        
        // Wait for cache to expire
        Thread.sleep(150);
        
        // Should be cache miss due to timeout
        proxy.getCustomer("CUST001");
        
        assertEquals(2, proxy.getCacheStats().getMisses(), "Should have 2 misses due to timeout");
    }

    @Test
    @DisplayName("Performance comparison with complex queries")
    void testComplexQueryPerformance() {
        // Test with expensive tariff history queries
        DatabaseCustomerService realService = new DatabaseCustomerService();
        CachedCustomerServiceProxy proxy = new CachedCustomerServiceProxy(realService, 5000);
        
        long startUncached = System.currentTimeMillis();
        
        // Without proxy (direct calls)
        realService.getCustomerWithTariffHistory("CUST001");
        realService.getCustomerWithTariffHistory("CUST001");
        realService.getCustomerWithTariffHistory("CUST001");
        
        long uncachedDuration = System.currentTimeMillis() - startUncached;
        
        long startCached = System.currentTimeMillis();
        
        // With proxy (caching)
        proxy.getCustomerWithTariffHistory("CUST002");
        proxy.getCustomerWithTariffHistory("CUST002");
        proxy.getCustomerWithTariffHistory("CUST002");
        
        long cachedDuration = System.currentTimeMillis() - startCached;
        
        System.out.println("📊 Complex Query - Uncached: " + uncachedDuration + "ms, Cached: " + cachedDuration + "ms");
        System.out.println("📊 Performance improvement: " + (uncachedDuration / (double) cachedDuration) + "x faster");
        
        assertTrue(cachedDuration < uncachedDuration / 2, "Caching should provide significant speedup for complex queries");
    }

    @Test
    @DisplayName("Different cache keys for different query types")
    void testCacheKeyDifferentiation() {
        DatabaseCustomerService realService = new DatabaseCustomerService();
        CachedCustomerServiceProxy proxy = new CachedCustomerServiceProxy(realService, 5000);
        
        // Different query types should use different cache entries
        proxy.getCustomer("CUST001");
        proxy.getCustomerWithTariffHistory("CUST001");
        
        // Both should be cache misses (different keys)
        CachedCustomerServiceProxy.CacheStats stats = proxy.getCacheStats();
        assertEquals(2, stats.getMisses(), "Different query types should use different cache keys");
        assertEquals(2, stats.getSize(), "Should have 2 different cache entries");
    }
}