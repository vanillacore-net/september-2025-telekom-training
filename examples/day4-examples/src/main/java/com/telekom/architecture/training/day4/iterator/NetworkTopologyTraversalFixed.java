package com.telekom.architecture.training.day4.iterator;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * FIXED IMPLEMENTATION using Iterator Pattern
 * 
 * Benefits:
 * - Safe iteration even during concurrent modifications
 * - Multiple traversal strategies (DFS, BFS, filtered, parallel)
 * - Type-safe iteration with generics
 * - Memory-efficient streaming for large topologies
 * - Clean separation between iteration logic and business logic
 * - Support for custom filtering and transformation during iteration
 * 
 * Enterprise Features:
 * - Thread-safe iteration with ReadWriteLock
 * - Streaming API integration for parallel processing
 * - Multiple iterator types for different use cases
 * - Fail-safe iteration with snapshot-based approach
 * 
 * Trainer Notes:
 * - Show how Iterator pattern eliminates ConcurrentModificationException
 * - Demonstrate different traversal strategies
 * - Point out performance benefits of lazy evaluation
 * - Show how business logic becomes cleaner
 */
public class NetworkTopologyTraversalFixed {
    
    public static class NetworkNode {
        private final String id;
        private final String type;
        private final Map<String, Object> properties = new HashMap<>();
        private final List<NetworkNode> connections = new CopyOnWriteArrayList<>();
        private volatile String status = "ACTIVE";
        
        public NetworkNode(String id, String type) {
            this.id = id;
            this.type = type;
        }
        
        public void addConnection(NetworkNode node) {
            connections.add(node);
        }
        
        public void removeConnection(NetworkNode node) {
            connections.remove(node);
        }
        
        public void addProperty(String key, Object value) {
            synchronized (properties) {
                properties.put(key, value);
            }
        }
        
        // Iterator pattern: provide safe iteration over connections
        public Iterator<NetworkNode> connectionIterator() {
            return new ArrayList<>(connections).iterator(); // Snapshot-based iterator
        }
        
        // Stream API support
        public Stream<NetworkNode> connections() {
            return new ArrayList<>(connections).stream(); // Thread-safe stream
        }
        
        public Iterator<Map.Entry<String, Object>> propertyIterator() {
            synchronized (properties) {
                return new HashMap<>(properties).entrySet().iterator(); // Snapshot-based
            }
        }
        
        public Stream<Map.Entry<String, Object>> properties() {
            synchronized (properties) {
                return new HashMap<>(properties).entrySet().stream();
            }
        }
        
        public int getConnectionCount() { return connections.size(); }
        public String getId() { return id; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    // Iterator interfaces for different traversal strategies
    public interface TopologyIterator extends Iterator<NetworkNode> {
        void reset();
        boolean hasNext();
        NetworkNode next();
    }
    
    // Concrete iterator implementations
    public static class DepthFirstIterator implements TopologyIterator {
        private final NetworkTopology topology;
        private final Stack<NetworkNode> stack = new Stack<>();
        private final Set<String> visited = new HashSet<>();
        private final String startNodeId;
        
        public DepthFirstIterator(NetworkTopology topology, String startNodeId) {
            this.topology = topology;
            this.startNodeId = startNodeId;
            reset();
        }
        
        @Override
        public void reset() {
            stack.clear();
            visited.clear();
            NetworkNode startNode = topology.getNode(startNodeId);
            if (startNode != null) {
                stack.push(startNode);
            }
        }
        
        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }
        
        @Override
        public NetworkNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more nodes in depth-first traversal");
            }
            
            NetworkNode current = stack.pop();
            if (visited.contains(current.getId())) {
                return hasNext() ? next() : null; // Skip already visited, try next
            }
            
            visited.add(current.getId());
            
            // Add connections to stack (in reverse order for proper DFS)
            List<NetworkNode> connections = new ArrayList<>();
            Iterator<NetworkNode> connectionIter = current.connectionIterator();
            while (connectionIter.hasNext()) {
                connections.add(connectionIter.next());
            }
            Collections.reverse(connections);
            
            for (NetworkNode connection : connections) {
                if (!visited.contains(connection.getId())) {
                    stack.push(connection);
                }
            }
            
            return current;
        }
    }
    
    public static class BreadthFirstIterator implements TopologyIterator {
        private final NetworkTopology topology;
        private final Queue<NetworkNode> queue = new LinkedList<>();
        private final Set<String> visited = new HashSet<>();
        private final String startNodeId;
        
        public BreadthFirstIterator(NetworkTopology topology, String startNodeId) {
            this.topology = topology;
            this.startNodeId = startNodeId;
            reset();
        }
        
        @Override
        public void reset() {
            queue.clear();
            visited.clear();
            NetworkNode startNode = topology.getNode(startNodeId);
            if (startNode != null) {
                queue.offer(startNode);
            }
        }
        
        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }
        
        @Override
        public NetworkNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more nodes in breadth-first traversal");
            }
            
            NetworkNode current = queue.poll();
            if (visited.contains(current.getId())) {
                return hasNext() ? next() : null; // Skip already visited
            }
            
            visited.add(current.getId());
            
            // Add connections to queue
            Iterator<NetworkNode> connectionIter = current.connectionIterator();
            while (connectionIter.hasNext()) {
                NetworkNode connection = connectionIter.next();
                if (!visited.contains(connection.getId())) {
                    queue.offer(connection);
                }
            }
            
            return current;
        }
    }
    
    public static class FilteredIterator implements TopologyIterator {
        private final TopologyIterator baseIterator;
        private final Predicate<NetworkNode> filter;
        private NetworkNode nextNode = null;
        private boolean nextComputed = false;
        
        public FilteredIterator(TopologyIterator baseIterator, Predicate<NetworkNode> filter) {
            this.baseIterator = baseIterator;
            this.filter = filter;
        }
        
        @Override
        public void reset() {
            baseIterator.reset();
            nextNode = null;
            nextComputed = false;
        }
        
        @Override
        public boolean hasNext() {
            if (!nextComputed) {
                computeNext();
            }
            return nextNode != null;
        }
        
        @Override
        public NetworkNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more filtered nodes");
            }
            
            NetworkNode result = nextNode;
            nextNode = null;
            nextComputed = false;
            return result;
        }
        
        private void computeNext() {
            while (baseIterator.hasNext()) {
                NetworkNode candidate = baseIterator.next();
                if (candidate != null && filter.test(candidate)) {
                    nextNode = candidate;
                    nextComputed = true;
                    return;
                }
            }
            nextNode = null;
            nextComputed = true;
        }
    }
    
    // Main topology class with iterator support
    public static class NetworkTopology {
        private final List<NetworkNode> nodes = new CopyOnWriteArrayList<>();
        private final Map<String, NetworkNode> nodeIndex = new HashMap<>();
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        
        public void addNode(NetworkNode node) {
            lock.writeLock().lock();
            try {
                nodes.add(node);
                nodeIndex.put(node.getId(), node);
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        public void removeNode(String nodeId) {
            lock.writeLock().lock();
            try {
                NetworkNode node = nodeIndex.remove(nodeId);
                if (node != null) {
                    nodes.remove(node);
                    // Remove connections from other nodes
                    for (NetworkNode n : nodes) {
                        n.removeConnection(node);
                    }
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        // Iterator pattern: Safe iteration methods
        public Iterator<NetworkNode> iterator() {
            return new ArrayList<>(nodes).iterator(); // Snapshot-based iterator
        }
        
        public TopologyIterator depthFirstIterator(String startNodeId) {
            return new DepthFirstIterator(this, startNodeId);
        }
        
        public TopologyIterator breadthFirstIterator(String startNodeId) {
            return new BreadthFirstIterator(this, startNodeId);
        }
        
        public TopologyIterator filteredIterator(String startNodeId, Predicate<NetworkNode> filter) {
            return new FilteredIterator(depthFirstIterator(startNodeId), filter);
        }
        
        public TopologyIterator typeFilteredIterator(String startNodeId, String nodeType) {
            return filteredIterator(startNodeId, node -> nodeType.equals(node.getType()));
        }
        
        // Stream API support for modern processing
        public Stream<NetworkNode> stream() {
            lock.readLock().lock();
            try {
                return new ArrayList<>(nodes).stream(); // Thread-safe stream from snapshot
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public Stream<NetworkNode> parallelStream() {
            lock.readLock().lock();
            try {
                return new ArrayList<>(nodes).parallelStream(); // Parallel processing support
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public NetworkNode getNode(String id) {
            lock.readLock().lock();
            try {
                return nodeIndex.get(id);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public int getNodeCount() {
            return nodes.size();
        }
        
        public List<String> getAllNodeIds() {
            lock.readLock().lock();
            try {
                return nodes.stream().map(NetworkNode::getId).toList();
            } finally {
                lock.readLock().unlock();
            }
        }
    }
    
    // Clean report generation using Iterator pattern
    public static class NetworkTopologyReportGenerator {
        
        public String generateConnectivityReport(NetworkTopology topology) {
            StringBuilder report = new StringBuilder();
            report.append("=== Network Connectivity Report ===\n");
            
            // Safe iteration using iterator pattern
            Iterator<NetworkNode> nodeIterator = topology.iterator();
            while (nodeIterator.hasNext()) {
                NetworkNode node = nodeIterator.next();
                report.append("\nNode: ").append(node.getId())
                      .append(" (").append(node.getType()).append(") - Status: ").append(node.getStatus()).append("\n");
                
                // Safe iteration over connections
                Iterator<NetworkNode> connectionIter = node.connectionIterator();
                while (connectionIter.hasNext()) {
                    NetworkNode connection = connectionIter.next();
                    report.append("  -> Connected to: ").append(connection.getId()).append("\n");
                }
                
                // Safe iteration over properties
                Iterator<Map.Entry<String, Object>> propertyIter = node.propertyIterator();
                while (propertyIter.hasNext()) {
                    Map.Entry<String, Object> property = propertyIter.next();
                    report.append("  Property: ").append(property.getKey())
                          .append(" = ").append(property.getValue()).append("\n");
                }
            }
            
            return report.toString();
        }
        
        public List<NetworkNode> findNodesByType(NetworkTopology topology, String nodeType) {
            // Clean, functional approach using streams
            return topology.stream()
                         .filter(node -> nodeType.equals(node.getType()))
                         .toList();
        }
        
        public Map<String, Integer> generateTypeStatistics(NetworkTopology topology) {
            Map<String, Integer> stats = new HashMap<>();
            
            // Stream-based processing - clean and efficient
            topology.stream().forEach(node -> {
                String type = node.getType();
                stats.merge(type, 1, Integer::sum);
                
                // Count connections per type
                node.connections().forEach(connection -> {
                    String connectionType = connection.getType();
                    String key = type + "_to_" + connectionType;
                    stats.merge(key, 1, Integer::sum);
                });
            });
            
            return stats;
        }
        
        public List<String> depthFirstTraversal(NetworkTopology topology, String startNodeId) {
            List<String> traversalOrder = new ArrayList<>();
            
            // Clean traversal using iterator
            TopologyIterator iterator = topology.depthFirstIterator(startNodeId);
            while (iterator.hasNext()) {
                NetworkNode node = iterator.next();
                if (node != null) {
                    traversalOrder.add(node.getId());
                }
            }
            
            return traversalOrder;
        }
        
        public List<String> breadthFirstTraversal(NetworkTopology topology, String startNodeId) {
            List<String> traversalOrder = new ArrayList<>();
            
            TopologyIterator iterator = topology.breadthFirstIterator(startNodeId);
            while (iterator.hasNext()) {
                NetworkNode node = iterator.next();
                if (node != null) {
                    traversalOrder.add(node.getId());
                }
            }
            
            return traversalOrder;
        }
        
        public List<String> findRoutersInSubnet(NetworkTopology topology, String startNodeId) {
            List<String> routers = new ArrayList<>();
            
            // Filtered iteration - only routers
            TopologyIterator iterator = topology.typeFilteredIterator(startNodeId, "ROUTER");
            while (iterator.hasNext()) {
                NetworkNode router = iterator.next();
                if (router != null) {
                    routers.add(router.getId());
                }
            }
            
            return routers;
        }
    }
    
    // Reliable monitoring service using safe iteration
    public static class NetworkMonitoringService {
        private final NetworkTopology topology;
        private volatile boolean monitoring = true;
        
        public NetworkMonitoringService(NetworkTopology topology) {
            this.topology = topology;
        }
        
        public void startContinuousMonitoring() {
            Thread monitoringThread = new Thread(() -> {
                while (monitoring) {
                    try {
                        performHealthCheck();
                        performDetailedAnalysis();
                        Thread.sleep(5000); // Check every 5 seconds
                    } catch (Exception e) {
                        System.err.println("Monitoring error (non-fatal): " + e.getMessage());
                        // Continues running even if individual checks fail
                    }
                }
            });
            monitoringThread.start();
        }
        
        private void performHealthCheck() {
            // Safe health checking using streams
            List<NetworkNode> snapshot = topology.stream().toList();
            
            long totalNodes = snapshot.size();
            long activeNodes = snapshot.stream()
                                     .mapToLong(node -> "ACTIVE".equals(node.getStatus()) ? 1 : 0)
                                     .sum();
            
            if (totalNodes > 0) {
                double healthPercentage = (double) activeNodes / totalNodes * 100;
                System.out.printf("Network Health: %.1f%% (%d/%d nodes active)\n", 
                                healthPercentage, activeNodes, totalNodes);
            }
        }
        
        private void performDetailedAnalysis() {
            // Detailed analysis using parallel streams for performance
            Map<String, Long> typeDistribution = topology.parallelStream()
                .collect(java.util.stream.Collectors.groupingBy(
                    NetworkNode::getType,
                    java.util.stream.Collectors.counting()
                ));
            
            System.out.println("Node Type Distribution: " + typeDistribution);
            
            // Check for isolated nodes
            long isolatedNodes = topology.parallelStream()
                .mapToLong(node -> node.getConnectionCount() == 0 ? 1 : 0)
                .sum();
            
            if (isolatedNodes > 0) {
                System.out.println("⚠️ Warning: " + isolatedNodes + " isolated nodes detected");
            }
        }
        
        public void stopMonitoring() {
            monitoring = false;
        }
    }
}