package com.telekom.architecture.training.day4.iterator;

import java.util.*;
import java.util.concurrent.ConcurrentModificationException;

/**
 * INITIAL IMPLEMENTATION - Iterator Anti-Pattern
 * 
 * Problems demonstrated:
 * - Direct access to internal collections exposes implementation details
 * - ConcurrentModificationException when network changes during iteration
 * - No type safety when traversing different node types
 * - Complex traversal logic mixed with business logic
 * - Memory leaks from retained references to large collections
 * - No support for different traversal strategies (DFS, BFS, filtered)
 * 
 * Business Impact:
 * - Network topology reports crash during network changes
 * - Performance monitoring fails when devices are added/removed
 * - Backup processes fail due to concurrent modifications
 * - No way to traverse only specific device types efficiently
 * 
 * Trainer Notes:
 * - Demonstrate ConcurrentModificationException in action
 * - Show memory issues with large network topologies
 * - Point out tight coupling between iteration and data structure
 */
public class NetworkTopologyTraversalInitial {
    
    public static class NetworkNode {
        private final String id;
        private final String type;
        private final Map<String, Object> properties;
        private final List<NetworkNode> connections;
        private String status = "ACTIVE";
        
        public NetworkNode(String id, String type) {
            this.id = id;
            this.type = type;
            this.properties = new HashMap<>();
            this.connections = new ArrayList<>();
        }
        
        public void addConnection(NetworkNode node) {
            connections.add(node);
        }
        
        // PROBLEM: Exposing internal collections directly
        public List<NetworkNode> getConnections() {
            return connections; // Direct access to mutable collection!
        }
        
        public void addProperty(String key, Object value) {
            properties.put(key, value);
        }
        
        // PROBLEM: Exposing internal map directly
        public Map<String, Object> getProperties() {
            return properties; // Direct access to mutable map!
        }
        
        public String getId() { return id; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public static class NetworkTopology {
        // PROBLEM: Direct list access leads to concurrent modification issues
        private final List<NetworkNode> nodes = new ArrayList<>();
        private final Map<String, NetworkNode> nodeIndex = new HashMap<>();
        
        public void addNode(NetworkNode node) {
            nodes.add(node);
            nodeIndex.put(node.getId(), node);
        }
        
        public void removeNode(String nodeId) {
            NetworkNode node = nodeIndex.remove(nodeId);
            if (node != null) {
                nodes.remove(node);
                // Remove connections from other nodes (expensive operation)
                for (NetworkNode n : nodes) {
                    n.getConnections().remove(node);
                }
            }
        }
        
        // PROBLEM: Exposing internal list directly
        public List<NetworkNode> getAllNodes() {
            return nodes; // This will cause ConcurrentModificationException!
        }
        
        public NetworkNode getNode(String id) {
            return nodeIndex.get(id);
        }
        
        public int getNodeCount() {
            return nodes.size();
        }
    }
    
    // PROBLEMATIC: Report generation that fails during network changes
    public static class NetworkTopologyReportGenerator {
        
        public String generateConnectivityReport(NetworkTopology topology) {
            StringBuilder report = new StringBuilder();
            report.append("=== Network Connectivity Report ===\n");
            
            try {
                // PROBLEM: Direct iteration over exposed collection
                List<NetworkNode> allNodes = topology.getAllNodes();
                
                for (NetworkNode node : allNodes) { // ConcurrentModificationException waiting to happen!
                    report.append("\nNode: ").append(node.getId())
                          .append(" (").append(node.getType()).append(") - Status: ").append(node.getStatus()).append("\n");
                    
                    // PROBLEM: Nested iteration over exposed collections
                    for (NetworkNode connection : node.getConnections()) { // Another CME risk!
                        report.append("  -> Connected to: ").append(connection.getId()).append("\n");
                    }
                    
                    // PROBLEM: Direct access to properties map
                    for (Map.Entry<String, Object> property : node.getProperties().entrySet()) { // Yet another CME risk!
                        report.append("  Property: ").append(property.getKey())
                              .append(" = ").append(property.getValue()).append("\n");
                    }
                }
                
            } catch (ConcurrentModificationException e) {
                // HORROR: Report generation fails if network changes during execution
                return "ERROR: Network topology changed during report generation. Please try again.\n" +
                       "This happens frequently in production networks where devices are constantly added/removed.";
            }
            
            return report.toString();
        }
        
        public List<NetworkNode> findNodesByType(NetworkTopology topology, String nodeType) {
            List<NetworkNode> matchingNodes = new ArrayList<>();
            
            try {
                // PROBLEM: Must iterate through entire topology for simple filter
                for (NetworkNode node : topology.getAllNodes()) { // CME risk again!
                    if (nodeType.equals(node.getType())) {
                        matchingNodes.add(node);
                    }
                }
            } catch (ConcurrentModificationException e) {
                // Operations teams hate this error message...
                throw new RuntimeException("Network topology changed during search. " +
                                         "Unable to find nodes of type: " + nodeType);
            }
            
            return matchingNodes;
        }
        
        public Map<String, Integer> generateTypeStatistics(NetworkTopology topology) {
            Map<String, Integer> stats = new HashMap<>();
            
            try {
                // PROBLEM: Complex traversal logic mixed with business logic
                for (NetworkNode node : topology.getAllNodes()) { // CME risk!
                    String type = node.getType();
                    stats.put(type, stats.getOrDefault(type, 0) + 1);
                    
                    // Count connections per type as well
                    for (NetworkNode connection : node.getConnections()) { // Nested CME risk!
                        String connectionType = connection.getType();
                        String key = type + "_to_" + connectionType;
                        stats.put(key, stats.getOrDefault(key, 0) + 1);
                    }
                }
            } catch (ConcurrentModificationException e) {
                throw new RuntimeException("Statistics generation failed due to concurrent network modifications");
            }
            
            return stats;
        }
        
        // NIGHTMARE: Depth-first traversal with manual stack management
        public List<String> depthFirstTraversal(NetworkTopology topology, String startNodeId) {
            List<String> traversalOrder = new ArrayList<>();
            Set<String> visited = new HashSet<>();
            Stack<NetworkNode> stack = new Stack<>();
            
            NetworkNode startNode = topology.getNode(startNodeId);
            if (startNode == null) return traversalOrder;
            
            stack.push(startNode);
            
            try {
                while (!stack.isEmpty()) {
                    NetworkNode current = stack.pop();
                    if (visited.contains(current.getId())) continue;
                    
                    visited.add(current.getId());
                    traversalOrder.add(current.getId());
                    
                    // PROBLEM: Manual traversal logic for every algorithm
                    for (NetworkNode connection : current.getConnections()) { // CME risk in nested iteration!
                        if (!visited.contains(connection.getId())) {
                            stack.push(connection);
                        }
                    }
                }
            } catch (ConcurrentModificationException e) {
                throw new RuntimeException("Network traversal failed due to topology changes during processing");
            }
            
            return traversalOrder;
        }
    }
    
    // PROBLEMATIC: Background monitoring that constantly breaks
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
                        Thread.sleep(5000); // Check every 5 seconds
                    } catch (Exception e) {
                        System.err.println("Monitoring failed: " + e.getMessage());
                        // In production, this means blind spots in network monitoring!
                    }
                }
            });
            monitoringThread.start();
        }
        
        private void performHealthCheck() {
            // PROBLEM: Health checks fail when network changes
            try {
                int totalNodes = 0;
                int activeNodes = 0;
                
                for (NetworkNode node : topology.getAllNodes()) { // CME guaranteed in continuous monitoring!
                    totalNodes++;
                    if ("ACTIVE".equals(node.getStatus())) {
                        activeNodes++;
                    }
                }
                
                double healthPercentage = (double) activeNodes / totalNodes * 100;
                System.out.printf("Network Health: %.1f%% (%d/%d nodes active)\n", 
                                healthPercentage, activeNodes, totalNodes);
                
            } catch (ConcurrentModificationException e) {
                System.err.println("❌ Health check failed due to network topology changes");
            }
        }
        
        public void stopMonitoring() {
            monitoring = false;
        }
    }
}