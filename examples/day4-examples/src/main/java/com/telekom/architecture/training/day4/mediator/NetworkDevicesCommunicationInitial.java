package com.telekom.architecture.training.day4.mediator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INITIAL IMPLEMENTATION - Communication Chaos Anti-Pattern
 * 
 * Problems demonstrated:
 * - Direct coupling between all network components (O(n²) complexity)
 * - Each device knows about all other devices
 * - Changes cascade through entire system
 * - Difficult to test individual components
 * - Adding new device types requires changes everywhere
 * 
 * Business Impact:
 * - Network changes become exponentially complex
 * - Single device update can trigger thousands of notifications
 * - System becomes fragile and hard to maintain
 * - Operations team struggles with debugging cascading failures
 * 
 * Trainer Notes:
 * - Demonstrate O(n²) complexity with realistic numbers
 * - Show how adding one new device type affects everything
 * - Point out the tight coupling nightmare
 */
public class NetworkDevicesCommunicationInitial {
    
    public static class NetworkDevice {
        protected String id;
        protected String type;
        protected String status = "ONLINE";
        
        // PROBLEM: Every device knows about every other type
        private List<Router> routers = new ArrayList<>();
        private List<Switch> switches = new ArrayList<>();  
        private List<FirewallDevice> firewalls = new ArrayList<>();
        private List<MonitoringSystem> monitors = new ArrayList<>();
        private List<LoadBalancer> loadBalancers = new ArrayList<>();
        
        public NetworkDevice(String id, String type) {
            this.id = id;
            this.type = type;
        }
        
        // HORROR: Direct coupling registration methods
        public void addRouter(Router router) { routers.add(router); }
        public void addSwitch(Switch switchDevice) { switches.add(switchDevice); }
        public void addFirewall(FirewallDevice firewall) { firewalls.add(firewall); }
        public void addMonitor(MonitoringSystem monitor) { monitors.add(monitor); }
        public void addLoadBalancer(LoadBalancer lb) { loadBalancers.add(lb); }
        
        // CATASTROPHIC: Status change triggers cascade
        public void updateStatus(String newStatus) {
            this.status = newStatus;
            System.out.println(LocalDateTime.now() + " [" + id + "] Status changed to: " + newStatus);
            
            // NIGHTMARE: O(n²) notification cascade
            // In real Telekom networks: 50 routers × 200 switches × 30 firewalls = 300,000 notifications!
            for (Router r : routers) {
                r.onDeviceStatusChanged(this, newStatus);
                for (Switch s : switches) {
                    s.onRouterAndDeviceStatusChanged(r, this, newStatus);
                    for (FirewallDevice f : firewalls) {
                        f.onNetworkTopologyChanged(r, s, this, newStatus);
                        for (MonitoringSystem m : monitors) {
                            m.onComplexNetworkEvent(r, s, f, this, newStatus);
                            for (LoadBalancer lb : loadBalancers) {
                                lb.onFullNetworkStateChanged(r, s, f, m, this, newStatus);
                                // This is where Operations teams start crying...
                            }
                        }
                    }
                }
            }
        }
        
        public String getId() { return id; }
        public String getType() { return type; }
        public String getStatus() { return status; }
    }
    
    public static class Router extends NetworkDevice {
        private int routingTableSize = 0;
        private AtomicInteger updateCount = new AtomicInteger(0);
        
        public Router(String id) {
            super(id, "ROUTER");
        }
        
        public void onDeviceStatusChanged(NetworkDevice device, String status) {
            updateCount.incrementAndGet();
            System.out.println("  → Router [" + id + "] recalculating routes due to " + device.getId());
            routingTableSize += status.equals("ONLINE") ? 10 : -5;
        }
        
        public int getUpdateCount() { return updateCount.get(); }
    }
    
    public static class Switch extends NetworkDevice {
        private int vlanCount = 0;
        private AtomicInteger reconfigurationCount = new AtomicInteger(0);
        
        public Switch(String id) {
            super(id, "SWITCH");
        }
        
        public void onRouterAndDeviceStatusChanged(Router router, NetworkDevice device, String status) {
            reconfigurationCount.incrementAndGet();
            System.out.println("    → Switch [" + id + "] updating VLANs for " + device.getId() + " via " + router.getId());
            vlanCount += status.equals("ONLINE") ? 2 : -1;
        }
        
        public int getReconfigurationCount() { return reconfigurationCount.get(); }
    }
    
    public static class FirewallDevice extends NetworkDevice {
        private int ruleCount = 100;
        private AtomicInteger policyUpdateCount = new AtomicInteger(0);
        
        public FirewallDevice(String id) {
            super(id, "FIREWALL");
        }
        
        public void onNetworkTopologyChanged(Router router, Switch switchDevice, NetworkDevice device, String status) {
            policyUpdateCount.incrementAndGet();
            System.out.println("      → Firewall [" + id + "] updating policies for topology change");
            ruleCount += status.equals("ONLINE") ? 5 : -3;
        }
        
        public int getPolicyUpdateCount() { return policyUpdateCount.get(); }
    }
    
    public static class MonitoringSystem extends NetworkDevice {
        private int alertsGenerated = 0;
        private AtomicInteger eventProcessedCount = new AtomicInteger(0);
        
        public MonitoringSystem(String id) {
            super(id, "MONITOR");
        }
        
        public void onComplexNetworkEvent(Router router, Switch switchDevice, FirewallDevice firewall, 
                                         NetworkDevice device, String status) {
            eventProcessedCount.incrementAndGet();
            System.out.println("        → Monitor [" + id + "] processing complex event from " + device.getId());
            alertsGenerated += status.equals("OFFLINE") ? 3 : 0;
        }
        
        public int getEventProcessedCount() { return eventProcessedCount.get(); }
    }
    
    public static class LoadBalancer extends NetworkDevice {
        private double cpuUtilization = 45.0;
        private AtomicInteger rebalanceCount = new AtomicInteger(0);
        
        public LoadBalancer(String id) {
            super(id, "LOAD_BALANCER");
        }
        
        public void onFullNetworkStateChanged(Router router, Switch switchDevice, FirewallDevice firewall,
                                             MonitoringSystem monitor, NetworkDevice device, String status) {
            rebalanceCount.incrementAndGet();
            System.out.println("          → LoadBalancer [" + id + "] rebalancing due to " + device.getId());
            cpuUtilization += status.equals("ONLINE") ? 2.5 : -1.5;
        }
        
        public int getRebalanceCount() { return rebalanceCount.get(); }
    }
}