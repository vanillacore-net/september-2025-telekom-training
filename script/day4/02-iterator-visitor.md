# Tag 4.2: Iterator & Visitor Pattern - Collection-Traversal und flexible Operations

## Lernziele
- Iterator Pattern für sichere Collection-Navigation
- Visitor Pattern für Operations auf komplexen Strukturen  
- Pattern-Kombination für flexible Datenverarbeitung
- Report-Generation und Tree-Processing in Telekom-Kontexten

## Problem: Complex Data Structures Navigation

### Der Report-Generation Horror
```java
// ANTI-PATTERN: Tight gekoppelte Report-Logik
class NetworkTopologyReport {
    
    public String generateReport(NetworkTopology topology) {
        StringBuilder report = new StringBuilder();
        
        // Verschiedene Report-Formate = verschiedene Code-Pfade
        if (reportType == ReportType.XML) {
            report.append("<?xml version='1.0'?>");
            for (NetworkNode node : topology.getNodes()) {
                report.append("<node id='" + node.getId() + "'>");
                if (node instanceof Router) {
                    Router r = (Router) node;
                    report.append("<routes>" + r.getRoutes().size() + "</routes>");
                    // ... 100 Zeilen Router-spezifischer XML Code
                } else if (node instanceof Switch) {
                    Switch s = (Switch) node;
                    report.append("<ports>" + s.getPorts().size() + "</ports>");
                    // ... 150 Zeilen Switch-spezifischer XML Code
                }
                report.append("</node>");
            }
        } else if (reportType == ReportType.JSON) {
            // Komplett andere Code-Pfade für JSON...
            // ... weitere 300 Zeilen duplicated Logic
        } else if (reportType == ReportType.PDF) {
            // Und nochmal alles für PDF...
        }
        
        return report.toString();
    }
}
```

### Probleme des gekoppelten Ansatzes
- **Neues Format?** → Komplette Klasse ändern
- **Neue Device-Art?** → Alle Report-Typen erweitern
- **Testing?** → Jeden Report-Type × jeden Device-Type testen
- **Maintenance?** → Änderung an Router-Logic requires PDF, XML und JSON Knowledge

## Lösung: Iterator + Visitor Kombination

### Iterator Pattern: Sichere Navigation
```java
// Generic Iterator für Network-Strukturen
public interface NetworkIterator<T extends NetworkElement> {
    boolean hasNext();
    T next();
    void reset();
    
    // Safe Removal während Iteration
    void remove(); // Optional operation
}

// Breadth-First Iterator für Topologie-Analyse
public class TopologyBreadthFirstIterator implements NetworkIterator<NetworkNode> {
    
    private final Queue<NetworkNode> queue = new LinkedList<>();
    private final Set<NetworkNode> visited = new HashSet<>();
    private final NetworkTopology topology;
    private NetworkNode current;
    
    public TopologyBreadthFirstIterator(NetworkTopology topology, NetworkNode startNode) {
        this.topology = topology;
        this.queue.offer(startNode);
    }
    
    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }
    
    @Override
    public NetworkNode next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        current = queue.poll();
        
        if (!visited.contains(current)) {
            visited.add(current);
            
            // Add connected nodes to queue
            topology.getConnectedNodes(current)
                .stream()
                .filter(node -> !visited.contains(node))
                .forEach(queue::offer);
        }
        
        return current;
    }
    
    @Override
    public void reset() {
        queue.clear();
        visited.clear();
        // Re-add start node
        queue.offer(topology.getStartNode());
    }
}
```

### Visitor Pattern: Flexible Operations
```java
// Visitor Interface für verschiedene Operations
public interface NetworkNodeVisitor<T> {
    T visitRouter(Router router);
    T visitSwitch(Switch switch_);
    T visitFirewall(FirewallDevice firewall);
    T visitLoadBalancer(LoadBalancer loadBalancer);
    
    // Default implementation für unknown types
    default T visitUnknown(NetworkNode node) {
        throw new UnsupportedOperationException(
            "Visitor doesn't support " + node.getClass().getSimpleName()
        );
    }
}

// Concrete Visitor für XML Report Generation
public class XmlReportVisitor implements NetworkNodeVisitor<String> {
    
    private final StringBuilder xmlBuilder = new StringBuilder();
    private int indentLevel = 0;
    
    @Override
    public String visitRouter(Router router) {
        indent();
        xmlBuilder.append("<router id='").append(router.getId()).append("'>\n");
        
        indentLevel++;
        indent();
        xmlBuilder.append("<routes count='").append(router.getRoutes().size()).append("'>\n");
        
        for (Route route : router.getRoutes()) {
            indentLevel++;
            indent();
            xmlBuilder.append("<route destination='").append(route.getDestination())
                     .append("' gateway='").append(route.getGateway()).append("'/>\n");
            indentLevel--;
        }
        
        indent();
        xmlBuilder.append("</routes>\n");
        
        indentLevel--;
        indent();
        xmlBuilder.append("</router>\n");
        
        return xmlBuilder.toString();
    }
    
    @Override
    public String visitSwitch(Switch switch_) {
        indent();
        xmlBuilder.append("<switch id='").append(switch_.getId()).append("'>\n");
        
        indentLevel++;
        for (SwitchPort port : switch_.getPorts()) {
            indent();
            xmlBuilder.append("<port number='").append(port.getNumber())
                     .append("' status='").append(port.getStatus())
                     .append("' speed='").append(port.getSpeed()).append("'/>\n");
        }
        indentLevel--;
        
        indent();
        xmlBuilder.append("</switch>\n");
        
        return xmlBuilder.toString();
    }
    
    // Weitere visit-Methoden für andere Device-Types...
    
    private void indent() {
        xmlBuilder.append("  ".repeat(indentLevel));
    }
    
    public String getCompleteXml() {
        return "<?xml version='1.0' encoding='UTF-8'?>\n<network>\n" + 
               xmlBuilder.toString() + 
               "</network>";
    }
}
```

### JSON Report Visitor
```java
public class JsonReportVisitor implements NetworkNodeVisitor<JsonNode> {
    
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectNode rootNode = mapper.createObjectNode();
    private final ArrayNode devicesArray = mapper.createArrayNode();
    
    @Override
    public JsonNode visitRouter(Router router) {
        ObjectNode routerNode = mapper.createObjectNode();
        routerNode.put("type", "router");
        routerNode.put("id", router.getId());
        routerNode.put("hostname", router.getHostname());
        
        ArrayNode routesArray = mapper.createArrayNode();
        for (Route route : router.getRoutes()) {
            ObjectNode routeNode = mapper.createObjectNode();
            routeNode.put("destination", route.getDestination());
            routeNode.put("gateway", route.getGateway());
            routeNode.put("metric", route.getMetric());
            routesArray.add(routeNode);
        }
        routerNode.set("routes", routesArray);
        
        devicesArray.add(routerNode);
        return routerNode;
    }
    
    @Override  
    public JsonNode visitSwitch(Switch switch_) {
        ObjectNode switchNode = mapper.createObjectNode();
        switchNode.put("type", "switch");
        switchNode.put("id", switch_.getId());
        
        ArrayNode portsArray = mapper.createArrayNode();
        for (SwitchPort port : switch_.getPorts()) {
            ObjectNode portNode = mapper.createObjectNode();
            portNode.put("number", port.getNumber());
            portNode.put("status", port.getStatus().name());
            portNode.put("speed", port.getSpeed());
            portNode.put("duplex", port.getDuplex().name());
            portsArray.add(portNode);
        }
        switchNode.set("ports", portsArray);
        
        devicesArray.add(switchNode);
        return switchNode;
    }
    
    public JsonNode getCompleteJson() {
        rootNode.set("network_devices", devicesArray);
        rootNode.put("generated_at", Instant.now().toString());
        rootNode.put("device_count", devicesArray.size());
        return rootNode;
    }
}
```

## Pattern-Kombination: Iterator + Visitor

### Flexible Report Engine
```java
@Service
public class NetworkReportEngine {
    
    public <T> T generateReport(NetworkTopology topology, 
                               NetworkNodeVisitor<T> visitor,
                               IterationStrategy strategy) {
        
        NetworkIterator<NetworkNode> iterator = createIterator(topology, strategy);
        
        while (iterator.hasNext()) {
            NetworkNode node = iterator.next();
            
            // Visitor Pattern für type-safe Operations
            T result = node.accept(visitor);
            
            // Optional: Collector pattern für Aggregation
            if (visitor instanceof CollectingVisitor) {
                ((CollectingVisitor<T>) visitor).collect(result);
            }
        }
        
        return visitor instanceof CompletableVisitor 
            ? ((CompletableVisitor<T>) visitor).getResult()
            : null;
    }
    
    private NetworkIterator<NetworkNode> createIterator(NetworkTopology topology, 
                                                       IterationStrategy strategy) {
        switch (strategy) {
            case BREADTH_FIRST:
                return new TopologyBreadthFirstIterator(topology, topology.getStartNode());
            case DEPTH_FIRST:
                return new TopologyDepthFirstIterator(topology, topology.getStartNode());
            case BY_DEVICE_TYPE:
                return new TypeGroupedIterator(topology);
            case BY_CRITICALITY:
                return new CriticalityOrderedIterator(topology);
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategy);
        }
    }
}
```

### Usage Examples
```java
@RestController
public class NetworkReportController {
    
    @Autowired
    private NetworkReportEngine reportEngine;
    
    @GetMapping("/network/report/xml")
    public ResponseEntity<String> getXmlReport(@RequestParam NetworkId networkId) {
        
        NetworkTopology topology = topologyService.getTopology(networkId);
        XmlReportVisitor xmlVisitor = new XmlReportVisitor();
        
        reportEngine.generateReport(topology, xmlVisitor, IterationStrategy.BREADTH_FIRST);
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_XML)
            .body(xmlVisitor.getCompleteXml());
    }
    
    @GetMapping("/network/report/json")
    public ResponseEntity<JsonNode> getJsonReport(@RequestParam NetworkId networkId) {
        
        NetworkTopology topology = topologyService.getTopology(networkId);
        JsonReportVisitor jsonVisitor = new JsonReportVisitor();
        
        reportEngine.generateReport(topology, jsonVisitor, IterationStrategy.BY_DEVICE_TYPE);
        
        return ResponseEntity.ok(jsonVisitor.getCompleteJson());
    }
}
```

## Advanced Patterns: Parallel Processing

### Parallel Visitor für Performance
```java
public class ParallelProcessingVisitor implements NetworkNodeVisitor<CompletableFuture<ProcessingResult>> {
    
    private final ExecutorService executor = Executors.newFixedThreadPool(8);
    private final List<CompletableFuture<ProcessingResult>> futures = new ArrayList<>();
    
    @Override
    public CompletableFuture<ProcessingResult> visitRouter(Router router) {
        CompletableFuture<ProcessingResult> future = CompletableFuture
            .supplyAsync(() -> {
                // Heavy processing für Router
                return processRouterAnalytics(router);
            }, executor)
            .exceptionally(throwable -> {
                log.error("Router processing failed", throwable);
                return ProcessingResult.failed(router.getId(), throwable);
            });
        
        futures.add(future);
        return future;
    }
    
    @Override
    public CompletableFuture<ProcessingResult> visitSwitch(Switch switch_) {
        CompletableFuture<ProcessingResult> future = CompletableFuture
            .supplyAsync(() -> {
                return processSwitchAnalytics(switch_);
            }, executor);
        
        futures.add(future);
        return future;
    }
    
    public CompletableFuture<List<ProcessingResult>> getAllResults() {
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        return allOf.thenApply(v -> 
            futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        );
    }
}
```

### Stream-basierte Iterator
```java
public class StreamableNetworkTopology implements Iterable<NetworkNode> {
    
    private final NetworkTopology topology;
    
    @Override
    public Iterator<NetworkNode> iterator() {
        return new TopologyBreadthFirstIterator(topology, topology.getStartNode());
    }
    
    public Stream<NetworkNode> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    
    public Stream<NetworkNode> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
    
    // Functional Operations
    public <T> Stream<T> streamAndVisit(NetworkNodeVisitor<T> visitor) {
        return stream().map(node -> node.accept(visitor));
    }
    
    public <T> CompletableFuture<List<T>> processInParallel(NetworkNodeVisitor<T> visitor) {
        List<CompletableFuture<T>> futures = stream()
            .map(node -> CompletableFuture.supplyAsync(() -> node.accept(visitor)))
            .collect(Collectors.toList());
        
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        return allOf.thenApply(v ->
            futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        );
    }
}
```

## Configuration Management mit Visitor

### Configuration Validation Visitor
```java
public class ConfigValidationVisitor implements NetworkNodeVisitor<List<ValidationError>> {
    
    private final ConfigurationRules rules;
    private final List<ValidationError> allErrors = new ArrayList<>();
    
    @Override
    public List<ValidationError> visitRouter(Router router) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Routing-spezifische Validierungen
        if (router.getRoutes().isEmpty()) {
            errors.add(ValidationError.warning(router.getId(), 
                "Router has no routes configured"));
        }
        
        // BGP Configuration Check
        if (router.isBgpEnabled() && router.getBgpPeers().isEmpty()) {
            errors.add(ValidationError.error(router.getId(),
                "BGP enabled but no peers configured"));
        }
        
        // Security Rules
        if (!router.hasManagementVrf()) {
            errors.add(ValidationError.critical(router.getId(),
                "Management VRF not configured - security risk"));
        }
        
        allErrors.addAll(errors);
        return errors;
    }
    
    @Override
    public List<ValidationError> visitSwitch(Switch switch_) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Port Security Checks
        long unsecuredPorts = switch_.getPorts().stream()
            .filter(port -> !port.hasPortSecurity())
            .count();
        
        if (unsecuredPorts > 0) {
            errors.add(ValidationError.warning(switch_.getId(),
                String.format("%d ports without port security", unsecuredPorts)));
        }
        
        // VLAN Configuration
        if (switch_.getVlans().stream().anyMatch(vlan -> vlan.getId() == 1)) {
            errors.add(ValidationError.info(switch_.getId(),
                "Default VLAN 1 still in use - consider changing"));
        }
        
        allErrors.addAll(errors);
        return errors;
    }
    
    public ValidationReport getValidationReport() {
        Map<ValidationLevel, List<ValidationError>> groupedErrors = 
            allErrors.stream()
                .collect(Collectors.groupingBy(ValidationError::getLevel));
        
        return ValidationReport.builder()
            .totalDevices(getDeviceCount())
            .criticalErrors(groupedErrors.getOrDefault(CRITICAL, Collections.emptyList()))
            .errors(groupedErrors.getOrDefault(ERROR, Collections.emptyList()))
            .warnings(groupedErrors.getOrDefault(WARNING, Collections.emptyList()))
            .infos(groupedErrors.getOrDefault(INFO, Collections.emptyList()))
            .build();
    }
}
```

## Anti-Patterns und Performance-Fallen

### ⚠️ Iterator Mutation während Traversal
```java
// ANTI-PATTERN: Modification während Iteration
NetworkIterator<NetworkNode> iterator = topology.iterator();
while (iterator.hasNext()) {
    NetworkNode node = iterator.next();
    
    if (node.needsRemoval()) {
        topology.removeNode(node); // EXCEPTION! ConcurrentModificationException
    }
}
```

**Lösung**: Safe Removal Pattern
```java
NetworkIterator<NetworkNode> iterator = topology.iterator();
while (iterator.hasNext()) {
    NetworkNode node = iterator.next();
    
    if (node.needsRemoval()) {
        iterator.remove(); // Safe removal via iterator
    }
}
```

### ⚠️ Visitor Explosion Anti-Pattern
```java
// ANTI-PATTERN: Visitor für jeden kleinen Use Case
class GetRouterCountVisitor implements NetworkNodeVisitor<Integer> { /*...*/ }
class GetSwitchCountVisitor implements NetworkNodeVisitor<Integer> { /*...*/ }
class GetFirewallCountVisitor implements NetworkNodeVisitor<Integer> { /*...*/ }
// ... 50 weitere micro-visitors
```

**Lösung**: Generic Counting Visitor
```java
public class CountingVisitor<T extends NetworkNode> implements NetworkNodeVisitor<Integer> {
    
    private final Class<T> targetType;
    private int count = 0;
    
    public CountingVisitor(Class<T> targetType) {
        this.targetType = targetType;
    }
    
    @Override
    public Integer visitRouter(Router router) {
        return targetType.isInstance(router) ? ++count : count;
    }
    
    @Override
    public Integer visitSwitch(Switch switch_) {
        return targetType.isInstance(switch_) ? ++count : count;
    }
    
    // Universal counting logic
}
```

## Hands-On: Telekom Report Engine

### Aufgabe 1: Custom Iterator
Implementiert einen `CriticalityIterator` der Network Nodes nach ihrer Kritikalität sortiert traversiert.

### Aufgabe 2: Configuration Visitor
Schreibt einen `SecurityAuditVisitor` der potenzielle Sicherheitsprobleme in der Netzwerk-Konfiguration findet.

### Aufgabe 3: Performance Testing
Benchmarked die Performance von Sequential vs. Parallel Visitor Processing für große Topologien (1000+ Nodes).

## Team-Adoption: Refactoring Strategy

### 1. Legacy Report Migration
```java
// Schritt 1: Wrapper für existierende Reports
@Deprecated
public class LegacyReportService {
    
    private final NetworkReportEngine newEngine;
    
    public String generateXmlReport(NetworkTopology topology) {
        // Legacy API verwendet neue Engine
        XmlReportVisitor visitor = new XmlReportVisitor();
        newEngine.generateReport(topology, visitor, IterationStrategy.BREADTH_FIRST);
        return visitor.getCompleteXml();
    }
}
```

### 2. Training Workshops
- **Iterator Concepts**: Sichere Navigation von Collections
- **Visitor Implementation**: Type-safe Operations ohne Casting
- **Performance**: Parallel Processing und Memory-Optimierung

### 3. Code Review Checklists
- [ ] Iterator bietet hasNext() und next() korrekt an
- [ ] Visitor behandelt alle relevanten Node-Types
- [ ] Exception Handling für unbekannte Types vorhanden
- [ ] Performance-kritische Paths verwenden Parallel Processing

## Production Readiness

### Performance Benchmarks
```java
@Component
public class ReportPerformanceBenchmark {
    
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void sequentialReport(NetworkTopology topology) {
        XmlReportVisitor visitor = new XmlReportVisitor();
        reportEngine.generateReport(topology, visitor, IterationStrategy.BREADTH_FIRST);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.AverageTime) 
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void parallelReport(NetworkTopology topology) {
        ParallelProcessingVisitor visitor = new ParallelProcessingVisitor();
        reportEngine.generateReport(topology, visitor, IterationStrategy.BREADTH_FIRST);
    }
}
```

### Monitoring Integration
```java
@Component
public class MonitoredReportEngine extends NetworkReportEngine {
    
    @Override
    @Timed(name = "network.report.generation", description = "Report generation time")
    @Counted(name = "network.report.requests", description = "Report requests")
    public <T> T generateReport(NetworkTopology topology, 
                               NetworkNodeVisitor<T> visitor,
                               IterationStrategy strategy) {
        
        return super.generateReport(topology, visitor, strategy);
    }
}
```

## Fazit: Iterator + Visitor für Complex Data Processing

### Wann verwenden?
- **✅ Complex Tree/Graph Structures** müssen traversiert werden
- **✅ Multiple Operations** auf derselben Datenstruktur
- **✅ Type-safe Processing** ohne explizite Casts
- **✅ Separation of Concerns** zwischen Navigation und Operation

### Wann NICHT verwenden?
- **❌ Simple Linear Collections** mit standard Operations
- **❌ Performance-critical** Code wo Abstraktion zu viel kostet
- **❌ Rarely changing** Data Structures ohne verschiedene Operations

### Integration mit anderen Patterns
- **Command**: Iterator über Command-History für Undo/Redo
- **Observer**: Visitor implementiert Observer für Node-Changes
- **Strategy**: Verschiedene Iteration-Strategies über Strategy Pattern

**Nächstes Modul**: Memento & Interpreter für State Management und DSLs