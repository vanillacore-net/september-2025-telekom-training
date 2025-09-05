# Proxy Pattern - Trainer Notes

## Lernziele
- Proxy als Stellvertreter-Objekt verstehen  
- Verschiedene Proxy-Arten kennenlernen (Cache, Security, Lazy Loading)
- Performance-Probleme durch intelligentes Caching lösen

## Problem Demonstration (Initial)

### Teure Datenbankzugriffe
```java
// PROBLEM: Jeder Aufruf geht an die Datenbank
public Customer getCustomer(String id) {
    simulateSlowDatabaseAccess(); // 100ms Latenz
    // Kunde "CUST001" wird mehrfach abgerufen = mehrfach 100ms!
    return database.get(id);
}
```

### Performance-Messung zeigen
```bash
# Terminal-Output demonstrieren
🐌 SLOW DB CALL #1 for customer: CUST001
🐌 SLOW DB CALL #2 for customer: CUST001  # Gleicher Kunde!
🐌 SLOW DB CALL #3 for customer: CUST001  # Wieder gleicher Kunde!
Duration: 300ms+ für 3 Aufrufe
```

### Code zeigen: `customercaching/initial/`
- Jeder `getCustomer()` Aufruf = DB-Hit
- Keine Wiederverwendung
- Lineare Steigerung der Latenz

## Lösung: Proxy Pattern (Fixed)

### Pattern-Struktur
```
Client → Proxy → RealSubject
         ↓
    Cache-Logic
```

### Komponenten
- **Subject**: `CustomerService` Interface
- **RealSubject**: `DatabaseCustomerService` 
- **Proxy**: `CachedCustomerServiceProxy`

### Proxy-Verantwortlichkeiten
1. **Zugriffskontrolle**: Cache-Check vor DB-Zugriff
2. **Lazy Loading**: Daten nur laden wenn nötig
3. **Cache-Management**: TTL, Invalidierung, Statistics

## Code-Demonstration

### 1. Cache-Hit Verhalten
```java
CachedCustomerServiceProxy proxy = new CachedCustomerServiceProxy(realService, 5000);

// Erster Aufruf: Cache Miss → DB-Zugriff
Customer c1 = proxy.getCustomer("CUST001"); // 100ms

// Zweiter Aufruf: Cache Hit → Sofort!  
Customer c2 = proxy.getCustomer("CUST001"); // ~0ms

System.out.println("⚡ CACHE HIT for customer: CUST001");
```

### 2. Performance-Vergleich live zeigen
```java
// Tests ausführen und Output zeigen:
// Ohne Caching: 300ms+ (3 * 100ms)
// Mit Caching: <150ms (1 * 100ms + 2 * ~0ms)
// Verbesserung: ~50-75% schneller!
```

### 3. Cache-Invalidierung
```java
// Update invalidiert Cache
proxy.updateCustomer(customer);
// Nächster getCustomer() ist wieder Cache Miss
proxy.getCustomer(customerId); // Lädt fresh aus DB
```

## Hands-on Übungen

### 1. Cache-TTL testen
```java
CachedCustomerServiceProxy proxy = new CachedCustomerServiceProxy(realService, 100); // Kurzer TTL

proxy.getCustomer("CUST001"); // Cache Miss
Thread.sleep(150); // Warten bis Cache abläuft
proxy.getCustomer("CUST001"); // Cache Miss (expired)
```

### 2. Cache-Statistics analysieren
```java
CacheStats stats = proxy.getCacheStats();
System.out.println("Hit Ratio: " + stats.getHitRatio()); // 66.7%
System.out.println("Cache Size: " + stats.getSize());    // 2
```

### 3. Security Proxy implementieren
```java
public class SecurityProxy implements CustomerService {
    private CustomerService realService;
    private String currentUser;
    
    @Override
    public Customer getCustomer(String customerId) {
        if (!hasPermission(currentUser, customerId)) {
            throw new SecurityException("Access denied");
        }
        return realService.getCustomer(customerId);
    }
}
```

## Verschiedene Proxy-Arten demonstrieren

### 1. **Cache Proxy** (Hauptbeispiel)
- Zwischenspeichern teurer Operationen
- TTL-basierte Invalidierung
- Hit-Ratio Monitoring

### 2. **Security Proxy**
```java
// Zugriffskontrolle vor echter Operation
if (!user.hasRole("CUSTOMER_READ")) {
    throw new UnauthorizedException();
}
return realService.getCustomer(id);
```

### 3. **Lazy Loading Proxy**
```java
// Erst bei Bedarf laden
private Customer customer;

public String getName() {
    if (customer == null) {
        customer = database.loadCustomer(customerId); // Lazy!
    }
    return customer.getName();
}
```

### 4. **Remote Proxy**
```java
// Versteckt Remote-Call Details
public Customer getCustomer(String id) {
    return httpClient.get("/api/customers/" + id); // REST-Call
}
```

## Performance-Analyse

### Messungen demonstrieren
```java
// Test ausführen und zeigen:
@Test
public void performanceComparison() {
    // Ohne Proxy: 600ms (3 * 200ms für complex queries)
    // Mit Proxy:   250ms (1 * 200ms + 2 * ~0ms)  
    // Improvement: ~2.4x faster!
}
```

### Cache-Effizienz
- **Hit Ratio**: 67% (2 hits, 1 miss) bei 3 Aufrufen
- **Memory Usage**: O(cache_size) zusätzlicher Speicher
- **Network/DB**: 67% weniger Zugriffe

## Design Considerations

### 1. Interface Consistency
```java
// Proxy MUSS gleiches Interface implementieren
public class CachedProxy implements CustomerService {
    private CustomerService realService; // Delegation
    
    // Alle Methoden implementieren und delegieren/erweitern
}
```

### 2. Cache-Key Design
```java
// Verschiedene Query-Types = verschiedene Keys
String basicKey = "basic_" + customerId;
String historyKey = "history_" + customerId;
// Vermeidet falsches Cache-Sharing
```

### 3. Error Handling
```java
try {
    return cache.get(key);
} catch (CacheException e) {
    // Fallback zu Real Service bei Cache-Problemen
    return realService.getCustomer(customerId);
}
```

## Diskussionspunkte

### Wann Proxy verwenden?
✅ **Ideal für**:
- Teure Operationen (DB, Network, Berechnungen)
- Zugriffskontrolle nötig
- Lazy Loading sinnvoll
- Monitoring/Logging gewünscht

❌ **Nicht geeignet**:
- Einfache, schnelle Operationen
- Memory-kritische Umgebungen
- Interface ändert sich häufig
- Keine klare Subject-Abstraktion

### Cache-Strategy Patterns
- **Write-Through**: Update schreibt auch in Cache
- **Write-Behind**: Update invalidiert Cache
- **TTL**: Time-based expiration
- **LRU**: Least-recently-used eviction

## Real-World Beispiele

### Telekom-Kontext
- **CDN**: Content Delivery Network als Caching Proxy
- **API Gateway**: Rate Limiting, Security Proxy
- **Database Connection Pool**: Resource Management Proxy
- **Service Mesh**: Sidecar Proxy für Microservices

### Andere Bereiche
- **Spring AOP**: Method Interceptors als Proxy
- **Hibernate**: Lazy Loading Proxies für Entities
- **Java RMI**: Remote Method Invocation Proxy
- **Web Proxy**: Squid, nginx für HTTP Caching

## Anti-Pattern Warnings

### 1. Zu komplexer Proxy
```java
// SCHLECHT - Proxy macht zu viel
public class OvercomplicatedProxy implements CustomerService {
    // Caching + Security + Logging + Validation + ...
    // Verletzt Single Responsibility Principle
}
```

### 2. Cache ohne Invalidierung
```java
// PROBLEM - stale data
cache.put(key, value);
// Was wenn sich Daten in DB ändern? Cache wird nie aktualisiert!
```

### 3. Memory Leaks
```java
// PROBLEM - Cache wächst unbegrenzt
private Map<String, Customer> cache = new HashMap<>(); // Keine Size-Limits!
```

## Code Review Checklist
- [ ] Proxy implementiert gleiches Interface wie Subject
- [ ] Delegation an RealSubject korrekt
- [ ] Cache-Keys eindeutig und sinnvoll
- [ ] TTL oder andere Invalidierung vorhanden
- [ ] Error Handling für Cache-Failures
- [ ] Memory-Usage begrenzt (LRU, Size Limits)
- [ ] Thread-Safety bei concurrent access
- [ ] Statistics/Monitoring verfügbar

## Performance-Tips
- **Cache-Size**: Monitoring der Hit-Ratio
- **TTL-Tuning**: Balance zwischen Freshness und Performance
- **Memory-Management**: LRU, Weak References
- **Thread-Safety**: ConcurrentHashMap, synchronized

## Zeitplanung
- Problem-Demo mit Performance: 10 min
- Pattern-Erklärung: 10 min
- Code-Walkthrough: 15 min
- Hands-on Übungen: 15 min
- Performance-Analyse: 5 min
- Diskussion verschiedener Proxy-Arten: 10 min