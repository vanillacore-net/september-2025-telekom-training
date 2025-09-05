package com.telekom.architecture.training.day4.iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import com.telekom.architecture.training.day4.iterator.NetworkTopologyTraversalFixed.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Test suite for Iterator Pattern implementation
 * 
 * Tests demonstrate:
 * - Safe iteration even during concurrent modifications
 * - Multiple traversal strategies (DFS, BFS, filtered)
 * - Thread-safe iteration with streaming
 * - Memory-efficient processing for large topologies
 */
class IteratorPatternTest {
    
    private NetworkTopology topology;
    private NetworkNode router1, router2, switch1, switch2, firewall1;
    
    @BeforeEach
    void setUp() {
        topology = new NetworkTopology();
        
        // Create test network topology
        router1 = new NetworkNode("R001", "ROUTER");
        router2 = new NetworkNode("R002", "ROUTER");
        switch1 = new NetworkNode("SW001", "SWITCH");
        switch2 = new NetworkNode("SW002", "SWITCH");
        firewall1 = new NetworkNode("FW001", "FIREWALL");
        
        // Add nodes to topology
        topology.addNode(router1);
        topology.addNode(router2);
        topology.addNode(switch1);
        topology.addNode(switch2);
        topology.addNode(firewall1);
        
        // Create connections: R001 -> SW001 -> SW002 -> FW001
        //                          -> R002
        router1.addConnection(switch1);
        switch1.addConnection(switch2);
        switch1.addConnection(router2);
        switch2.addConnection(firewall1);
        
        // Add some properties for testing
        router1.addProperty("cpu_usage", "25%");
        router1.addProperty("memory_usage", "60%");
        switch1.addProperty("port_count", "24");
        firewall1.addProperty("rules_count", "150");
    }
    
    @Test
    @DisplayName("Basic topology iterator should traverse all nodes")
    void shouldTraverseAllNodes() {
        int nodeCount = 0;
        var iterator = topology.iterator();
        
        while (iterator.hasNext()) {
            NetworkNode node = iterator.next();
            assertThat(node).isNotNull();
            nodeCount++;
        }
        
        assertThat(nodeCount).isEqualTo(5);
    }
    
    @Test
    @DisplayName("Depth-first iterator should traverse in correct order")
    void shouldTraverseDepthFirst() {
        TopologyIterator iterator = topology.depthFirstIterator("R001");
        List<String> traversalOrder = new java.util.ArrayList<>();
        
        while (iterator.hasNext()) {
            NetworkNode node = iterator.next();
            if (node != null) {
                traversalOrder.add(node.getId());
            }
        }
        
        assertThat(traversalOrder).isNotEmpty();
        assertThat(traversalOrder.get(0)).isEqualTo("R001"); // Should start with R001
        assertThat(traversalOrder).contains("SW001", "SW002", "FW001", "R002");
    }
    
    @Test
    @DisplayName("Breadth-first iterator should traverse level by level")
    void shouldTraverseBreadthFirst() {
        TopologyIterator iterator = topology.breadthFirstIterator("R001");
        List<String> traversalOrder = new java.util.ArrayList<>();
        
        while (iterator.hasNext()) {
            NetworkNode node = iterator.next();
            if (node != null) {
                traversalOrder.add(node.getId());
            }
        }
        
        assertThat(traversalOrder).isNotEmpty();
        assertThat(traversalOrder.get(0)).isEqualTo("R001"); // Should start with R001
        // In BFS, SW001 should come before its connections
        int sw001Index = traversalOrder.indexOf("SW001");
        int sw002Index = traversalOrder.indexOf("SW002");
        int r002Index = traversalOrder.indexOf("R002");
        
        assertThat(sw001Index).isLessThan(sw002Index);
        assertThat(sw001Index).isLessThan(r002Index);
    }
    
    @Test
    @DisplayName("Filtered iterator should only return matching nodes")
    void shouldFilterNodesByType() {
        TopologyIterator iterator = topology.typeFilteredIterator("R001", "ROUTER");
        List<String> routerIds = new java.util.ArrayList<>();
        
        while (iterator.hasNext()) {
            NetworkNode node = iterator.next();
            if (node != null) {
                assertThat(node.getType()).isEqualTo("ROUTER");
                routerIds.add(node.getId());
            }
        }
        
        assertThat(routerIds).containsExactlyInAnyOrder("R001", "R002");
    }
    
    @Test
    @DisplayName("Custom filtered iterator should apply predicate correctly")
    void shouldApplyCustomFilter() {
        // Filter nodes that have properties
        TopologyIterator baseIterator = topology.depthFirstIterator("R001");
        TopologyIterator filteredIterator = new FilteredIterator(
            baseIterator, 
            node -> node.propertyIterator().hasNext()
        );
        
        List<String> nodesWithProperties = new java.util.ArrayList<>();
        
        while (filteredIterator.hasNext()) {
            NetworkNode node = filteredIterator.next();
            if (node != null) {
                assertThat(node.propertyIterator().hasNext()).isTrue();
                nodesWithProperties.add(node.getId());
            }
        }
        
        assertThat(nodesWithProperties).contains("R001", "SW001", "FW001");
    }
    
    @Test
    @DisplayName("Iterator reset should allow re-traversal")
    void shouldResetIteratorCorrectly() {
        TopologyIterator iterator = topology.depthFirstIterator("R001");
        
        // First traversal
        List<String> firstTraversal = new java.util.ArrayList<>();
        while (iterator.hasNext()) {
            NetworkNode node = iterator.next();
            if (node != null) {
                firstTraversal.add(node.getId());
            }
        }
        
        // Reset and traverse again
        iterator.reset();
        List<String> secondTraversal = new java.util.ArrayList<>();
        while (iterator.hasNext()) {
            NetworkNode node = iterator.next();
            if (node != null) {
                secondTraversal.add(node.getId());
            }
        }
        
        assertThat(firstTraversal).isEqualTo(secondTraversal);
    }
    
    @Test
    @DisplayName("Iterator should handle empty starting node gracefully")
    void shouldHandleInvalidStartNode() {
        TopologyIterator iterator = topology.depthFirstIterator("NONEXISTENT");
        
        assertThat(iterator.hasNext()).isFalse();
        
        assertThatThrownBy(iterator::next)
            .isInstanceOf(NoSuchElementException.class);
    }
    
    @Test
    @DisplayName("Concurrent modification should not affect iterator")
    void shouldHandleConcurrentModification() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        List<String> traversedNodes = new java.util.concurrent.CopyOnWriteArrayList<>();
        Exception[] exceptions = new Exception[1];
        
        // Start iterator in one thread
        executor.submit(() -> {
            try {
                startLatch.await();
                var iterator = topology.iterator();
                while (iterator.hasNext()) {
                    NetworkNode node = iterator.next();
                    traversedNodes.add(node.getId());
                    Thread.sleep(10); // Slow down to allow modifications
                }
            } catch (Exception e) {
                exceptions[0] = e;
            } finally {
                doneLatch.countDown();
            }
        });
        
        // Modify topology in another thread
        executor.submit(() -> {
            try {
                startLatch.await();
                Thread.sleep(5); // Let iterator start
                
                // Add new node during iteration
                NetworkNode newNode = new NetworkNode("NEW001", "ROUTER");
                topology.addNode(newNode);
                
                // Remove a node during iteration
                topology.removeNode("SW002");
                
            } catch (Exception e) {
                exceptions[0] = e;
            } finally {
                doneLatch.countDown();
            }
        });
        
        startLatch.countDown();
        doneLatch.await();
        
        assertThat(exceptions[0]).isNull(); // No ConcurrentModificationException
        assertThat(traversedNodes).isNotEmpty(); // Iterator completed successfully
    }
    
    @Test
    @DisplayName("Stream API should work correctly with topology")
    void shouldWorkWithStreamAPI() {
        // Add some nodes with status for filtering
        router1.setStatus("ACTIVE");
        router2.setStatus("MAINTENANCE");
        switch1.setStatus("ACTIVE");
        switch2.setStatus("ACTIVE");
        firewall1.setStatus("MAINTENANCE");
        
        List<NetworkNode> activeNodes = topology.stream()
            .filter(node -> "ACTIVE".equals(node.getStatus()))
            .toList();
        
        assertThat(activeNodes).hasSize(3);
        assertThat(activeNodes).extracting(NetworkNode::getId)
            .containsExactlyInAnyOrder("R001", "SW001", "SW002");
    }
    
    @Test
    @DisplayName("Parallel stream should process nodes concurrently")
    void shouldWorkWithParallelStream() {
        // Process nodes in parallel
        long routerCount = topology.parallelStream()
            .filter(node -> "ROUTER".equals(node.getType()))
            .count();
        
        assertThat(routerCount).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Node connection iterator should be safe")
    void shouldProvideThreadSafeConnectionIterator() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        List<String> connectionIds = new java.util.concurrent.CopyOnWriteArrayList<>();
        Exception[] exceptions = new Exception[1];
        
        // Iterate over connections in one thread
        executor.submit(() -> {
            try {
                startLatch.await();
                var connectionIterator = router1.connectionIterator();
                while (connectionIterator.hasNext()) {
                    NetworkNode connection = connectionIterator.next();
                    connectionIds.add(connection.getId());
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                exceptions[0] = e;
            } finally {
                doneLatch.countDown();
            }
        });
        
        // Modify connections in another thread
        executor.submit(() -> {
            try {
                startLatch.await();
                Thread.sleep(5);
                
                // Add new connection during iteration
                router1.addConnection(firewall1);
                
                // Remove connection during iteration
                router1.removeConnection(switch1);
                
            } catch (Exception e) {
                exceptions[0] = e;
            } finally {
                doneLatch.countDown();
            }
        });
        
        startLatch.countDown();
        doneLatch.await();
        
        assertThat(exceptions[0]).isNull(); // No ConcurrentModificationException
        // Iterator should complete without error
    }
    
    @Test
    @DisplayName("Property iterator should be thread-safe")
    void shouldProvideThreadSafePropertyIterator() {
        var propertyIterator = router1.propertyIterator();
        int propertyCount = 0;
        
        while (propertyIterator.hasNext()) {
            var property = propertyIterator.next();
            assertThat(property.getKey()).isNotNull();
            assertThat(property.getValue()).isNotNull();
            propertyCount++;
        }
        
        assertThat(propertyCount).isEqualTo(2); // cpu_usage and memory_usage
    }
    
    @Test
    @DisplayName("Network monitoring should work with safe iteration")
    void shouldSupportNetworkMonitoring() {
        NetworkMonitoringService monitoringService = new NetworkMonitoringService(topology);
        
        // This should not throw any exceptions
        assertThatCode(() -> {
            // Simulate one monitoring cycle
            List<NetworkNode> snapshot = topology.stream().toList();
            
            // Process health check
            long totalNodes = snapshot.size();
            long activeNodes = snapshot.stream()
                .mapToLong(node -> "ACTIVE".equals(node.getStatus()) ? 1 : 0)
                .sum();
                
            assertThat(totalNodes).isEqualTo(5);
        }).doesNotThrowAnyException();
    }
}