# Tag 3 - Modul 1: Strategy Pattern
## Austauschbare Monitoring-Algorithmen verstehen

### Lernziele
- **Strategy Pattern** als Behavioral Pattern verstehen
- Unterschied zwischen Strategy und einfachen if-else-Strukturen
- Event-orientierte Architektur mit Strategy kombinieren
- Anwendungsszenarien für Telekom-Netzwerk-Monitoring

---

## 1. Das Strategy Pattern Problem

### Warum brauchen wir austauschbare Algorithmen?

In Telekom-Netzwerken müssen verschiedene **Gerätetypen unterschiedlich überwacht** werden:
- **Router**: Performance, Routing-Tabellen, BGP-Sessions
- **Switches**: Port-Status, VLAN-Konfiguration, Spanning-Tree
- **Firewalls**: Security-Rules, Threat-Detection, Access-Logs

Die **naive Lösung** führt schnell zu Problemen:

```typescript
// ❌ PROBLEM: Monolithische if-else-Struktur
class NetworkMonitor {
  monitorDevice(device: Device, type: string) {
    if (type === 'router') {
      // 50+ Zeilen Router-Monitoring
    } else if (type === 'switch') {
      // 40+ Zeilen Switch-Monitoring  
    } else if (type === 'firewall') {
      // 60+ Zeilen Firewall-Monitoring
    } else if (type === 'loadbalancer') {
      // 30+ Zeilen LoadBalancer-Monitoring
    }
    // Was passiert bei neuen Gerätetypen?
  }
}
```

### Die Probleme dieser Lösung:

**1. Open-Closed Principle Verletzung**
```
Jeder neue Gerätetyp → Änderung der bestehenden Klasse
→ Risiko für bestehende Funktionalität
→ Schwierige Wartung
```

**2. Single Responsibility Verletzung**
```
Eine Klasse kennt ALLE Monitoring-Algorithmen
→ Hohe Komplexität (100+ Zeilen pro Methode)
→ Schwierige Testbarkeit
```

**3. Mangelnde Flexibilität**
```
Algorithmus zur Compile-Zeit festgelegt
→ Keine Runtime-Anpassung
→ Keine A/B-Tests verschiedener Monitoring-Strategien
```

---

## 2. Strategy Pattern Konzept

### Das Pattern verstehen

**Strategy Pattern Definition:**
> "Definiert eine Familie von Algorithmen, kapselt jeden einzelnen und macht sie austauschbar."

```
┌─────────────┐    verwendet    ┌─────────────────┐
│   Context   │ ──────────────> │    Strategy     │
│             │                 │   <<interface>> │
│ - strategy  │                 ├─────────────────┤  
│ + execute() │                 │ + algorithm()   │
└─────────────┘                 └─────────────────┘
                                          ▲
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
          ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
          │ ConcreteStrategyA│   │ ConcreteStrategyB│   │ ConcreteStrategyC│
          ├─────────────────┤   ├─────────────────┤   ├─────────────────┤
          │ + algorithm()   │   │ + algorithm()   │   │ + algorithm()   │
          └─────────────────┘   └─────────────────┘   └─────────────────┘
```

### Die drei Hauptkomponenten:

**1. Strategy Interface (Algorithmus-Vertrag)**
```typescript
// Definiert WAS alle Monitoring-Algorithmen können müssen
interface MonitoringStrategy {
  monitor(device: Device): Promise<MonitoringResult>;
  getMonitoringType(): string;
  getSupportedDeviceTypes(): string[];
}
```

**2. Concrete Strategies (Spezifische Algorithmen)**
```typescript
// Implementiert WIE ein bestimmter Monitoring-Typ funktioniert
class PerformanceMonitoring implements MonitoringStrategy {
  // Spezifische Performance-Überwachung
}

class SecurityMonitoring implements MonitoringStrategy {
  // Spezifische Security-Überwachung  
}
```

**3. Context (Algorithmus-Nutzer)**
```typescript
// Verwendet Strategien ohne deren interne Details zu kennen
class DeviceMonitor {
  private strategy: MonitoringStrategy;
  
  setStrategy(strategy: MonitoringStrategy) {
    this.strategy = strategy;
  }
  
  async monitor(device: Device) {
    return await this.strategy.monitor(device);
  }
}
```

### Konkrete Strategien - Verschiedene Algorithmen

**Jede Strategy kapselt einen spezifischen Monitoring-Ansatz:**

```typescript
// Strategy 1: Performance-fokussiertes Monitoring
class PerformanceMonitoring implements MonitoringStrategy {
  async monitor(device: Device): Promise<MonitoringResult> {
    // Fokus: CPU, Memory, Network Throughput
    const metrics = {
      cpu: await this.measureCpuUsage(device),
      memory: await this.measureMemoryUsage(device),
      network: await this.measureNetworkLoad(device)
    };
    
    return {
      deviceId: device.id,
      status: this.evaluatePerformance(metrics),
      details: metrics,
      recommendations: this.suggestOptimizations(metrics)
    };
  }
  
  getMonitoringType(): string { return 'performance'; }
  
  // Performance-spezifische Bewertungslogik
  private evaluatePerformance(metrics: any): DeviceStatus {
    if (metrics.cpu > 90 || metrics.memory > 85) return 'critical';
    if (metrics.cpu > 70 || metrics.memory > 70) return 'warning'; 
    return 'healthy';
  }
}
```

```typescript
// Strategy 2: Security-fokussiertes Monitoring  
class SecurityMonitoring implements MonitoringStrategy {
  async monitor(device: Device): Promise<MonitoringResult> {
    // Fokus: Vulnerabilities, Access Patterns, Threats
    const securityChecks = {
      vulnerabilities: await this.scanVulnerabilities(device),
      accessPatterns: await this.analyzeAccessLogs(device),
      threatLevel: await this.assessThreats(device)
    };
    
    return {
      deviceId: device.id,
      status: this.evaluateSecurity(securityChecks),
      details: securityChecks,
      recommendations: this.suggestSecurityMeasures(securityChecks)
    };
  }
  
  getMonitoringType(): string { return 'security'; }
  
  // Security-spezifische Bewertungslogik
  private evaluateSecurity(checks: any): DeviceStatus {
    if (checks.threatLevel === 'high') return 'critical';
    if (checks.vulnerabilities.length > 0) return 'warning';
    return 'healthy';
  }
}
```

### Warum Strategy Pattern hier brilliert:

**1. Austauschbarkeit zur Laufzeit**
```typescript
const monitor = new DeviceMonitor();

// Unterschiedliche Monitoring-Ansätze je nach Situation
if (isBusinessHours()) {
  monitor.setStrategy(new PerformanceMonitoring());
} else {
  monitor.setStrategy(new DetailedHealthCheck());
}

if (securityAlert) {
  monitor.setStrategy(new SecurityMonitoring());
}
```

**2. Einfache Erweiterung**
```typescript
// Neue Strategy hinzufügen - KEINE Änderung bestehender Klassen
class ComplianceMonitoring implements MonitoringStrategy {
  // Compliance-spezifische Monitoring-Logik
}
```

**3. Isolierte Testbarkeit**
```typescript
// Jede Strategy separat testbar
const strategy = new PerformanceMonitoring();
const result = await strategy.monitor(testDevice);
assert(result.status === 'healthy');
```
```

## 3. Context - Der Strategienutzer

### DeviceMonitor als Context-Klasse

```typescript
// Context: Nutzt Strategien ohne deren Details zu kennen
class DeviceMonitor {
  private currentStrategy: MonitoringStrategy;
  private strategyHistory: StrategyUsage[] = [];
  
  constructor(defaultStrategy: MonitoringStrategy) {
    this.currentStrategy = defaultStrategy;
  }
  
  // Strategy wechseln (Laufzeit-Flexibilität)
  setStrategy(strategy: MonitoringStrategy): void {
    this.strategyHistory.push({
      previous: this.currentStrategy?.getMonitoringType(),
      new: strategy.getMonitoringType(),
      timestamp: new Date()
    });
    
    this.currentStrategy = strategy;
  }
  
  // Monitoring ausführen - delegiert an aktuelle Strategy
  async monitor(device: Device): Promise<MonitoringResult> {
    console.log(`Monitoring ${device.id} with ${this.currentStrategy.getMonitoringType()} strategy`);
    
    const startTime = Date.now();
    const result = await this.currentStrategy.monitor(device);
    const duration = Date.now() - startTime;
    
    // Context kann zusätzliche Meta-Informationen hinzufügen
    return {
      ...result,
      strategyUsed: this.currentStrategy.getMonitoringType(),
      executionTime: duration,
      timestamp: new Date()
    };
  }
  
  // Verschiedene Monitoring-Modi
  async performRoutineCheck(device: Device) {
    this.setStrategy(new BasicHealthCheck());
    return await this.monitor(device);
  }
  
  async performDeepAnalysis(device: Device) {
    const strategies = [
      new PerformanceMonitoring(),
      new SecurityMonitoring(), 
      new ConnectivityCheck()
    ];
    
    const results = [];
    for (const strategy of strategies) {
      this.setStrategy(strategy);
      results.push(await this.monitor(device));
    }
    
    return this.aggregateResults(results);
  }
}
```

### Strategy-Auswahl-Logik

```typescript
// Intelligente Strategy-Auswahl basierend auf Kontext
class MonitoringStrategySelector {
  selectStrategy(device: Device, context: MonitoringContext): MonitoringStrategy {
    // Gerätetype-basierte Auswahl
    if (device.type === 'router' && context.priority === 'performance') {
      return new RouterPerformanceMonitoring();
    }
    
    if (device.criticality === 'high' && context.isBusinessHours) {
      return new CriticalDeviceMonitoring();
    }
    
    // Zeit-basierte Auswahl
    if (context.isNightTime) {
      return new DetailedMaintenanceCheck();
    }
    
    // Fallback auf Standard-Monitoring
    return new BasicHealthCheck();
  }
}
```

### Der entscheidende Vorteil:

**Ohne Strategy Pattern:**
```typescript
// Jede Änderung erfordert Modifikation der Hauptklasse
class DeviceMonitor {
  monitor(device: Device, type: string) {
    if (type === 'performance') {
      // 50 Zeilen Performance-Code
    } else if (type === 'security') {
      // 60 Zeilen Security-Code
    }
    // Bei neuen Typen: Code hier ändern ❌
  }
}
```

**Mit Strategy Pattern:**
```typescript
// Neue Monitoring-Typen ohne Änderung bestehender Klassen
class NewComplianceMonitoring implements MonitoringStrategy {
  // Vollständig isolierte Implementierung ✅
}

// Nutzung: 
monitor.setStrategy(new NewComplianceMonitoring());
```
```

## 4. Event-Driven Architecture Integration

### Strategy Pattern + Events

**Strategy Pattern funktioniert hervorragend mit Event-Driven Architecture:**

```typescript
// Jede Strategy kann Events publizieren
class EventAwareStrategy implements MonitoringStrategy {
  constructor(private eventBus: EventBus) {}
  
  async monitor(device: Device): Promise<MonitoringResult> {
    // Strategy-spezifische Arbeit
    const result = await this.performMonitoring(device);
    
    // Events basierend auf Strategy-Ergebnissen
    if (result.status === 'critical') {
      await this.eventBus.publish(new CriticalDeviceEvent({
        deviceId: device.id,
        strategyType: this.getMonitoringType(),
        details: result.details
      }));
    }
    
    return result;
  }
}
```

### Multi-Strategy Orchestrierung

```typescript
class MonitoringOrchestrator {
  private strategies: MonitoringStrategy[] = [];
  
  // Mehrere Strategien parallel ausführen
  async comprehensiveMonitoring(device: Device): Promise<AggregatedResult> {
    const strategiesForDevice = this.selectStrategiesForDevice(device);
    
    // Parallel execution mit Promise.allSettled
    const results = await Promise.allSettled(
      strategiesForDevice.map(strategy => strategy.monitor(device))
    );
    
    return this.aggregateResults(results);
  }
  
  private selectStrategiesForDevice(device: Device): MonitoringStrategy[] {
    const strategies = [];
    
    // Immer Performance für Router
    if (device.type === 'router') {
      strategies.push(new RouterPerformanceMonitoring());
    }
    
    // Security für kritische Geräte
    if (device.criticality === 'high') {
      strategies.push(new SecurityMonitoring());
    }
    
    // Compliance-Monitoring während Audits
    if (this.isAuditPeriod()) {
      strategies.push(new ComplianceMonitoring());
    }
    
    return strategies;
  }
}
```

### Strategy Registry Pattern

```typescript
// Zentrale Registrierung aller verfügbaren Strategien
class MonitoringStrategyRegistry {
  private strategies = new Map<string, () => MonitoringStrategy>();
  
  register(type: string, factory: () => MonitoringStrategy) {
    this.strategies.set(type, factory);
  }
  
  create(type: string): MonitoringStrategy {
    const factory = this.strategies.get(type);
    if (!factory) {
      throw new Error(`Unknown monitoring strategy: ${type}`);
    }
    return factory();
  }
  
  listAvailableStrategies(): string[] {
    return Array.from(this.strategies.keys());
  }
}

// Verwendung:
const registry = new MonitoringStrategyRegistry();
registry.register('performance', () => new PerformanceMonitoring());
registry.register('security', () => new SecurityMonitoring());
registry.register('compliance', () => new ComplianceMonitoring());

// Strategy zur Laufzeit erstellen
const strategy = registry.create('performance');
```
```

## 5. Strategy Pattern vs. Alternative Ansätze

### Vergleich verschiedener Lösungsansätze

**1. If-Else-Struktur (Naive Lösung)**
```typescript
// ❌ Probleme: Schwer erweiterbar, nicht testbar, hohe Kopplung
class SimpleMonitor {
  monitor(device: Device, type: string) {
    if (type === 'performance') { /* 50 Zeilen */ }
    else if (type === 'security') { /* 60 Zeilen */ }
    // Neue Typen = Änderung dieser Klasse
  }
}
```

**2. Switch-Statement (Strukturierter, aber starr)**
```typescript
// ⚠️ Besser lesbar, aber immer noch nicht erweiterbar
class SwitchMonitor {
  monitor(device: Device, type: MonitoringType) {
    switch (type) {
      case MonitoringType.PERFORMANCE:
        return this.performanceMonitoring(device);
      case MonitoringType.SECURITY:
        return this.securityMonitoring(device);
      // Neue Cases = Änderung der Klasse
    }
  }
}
```

**3. Factory Pattern (Flexibler)**
```typescript
// ✅ Gut für Objekterzeugung, aber weniger flexibel zur Laufzeit
class MonitoringFactory {
  create(type: string): MonitoringStrategy {
    switch (type) {
      case 'performance': return new PerformanceMonitoring();
      case 'security': return new SecurityMonitoring();
    }
  }
}
```

**4. Strategy Pattern (Optimal für Behavioral Variationen)**
```typescript
// ✅ Maximale Flexibilität, Erweiterbarkeit, Testbarkeit
class StrategyBasedMonitor {
  private strategy: MonitoringStrategy;
  
  setStrategy(strategy: MonitoringStrategy) {
    this.strategy = strategy; // Laufzeit-Wechsel möglich
  }
  
  monitor(device: Device) {
    return this.strategy.monitor(device); // Delegation
  }
}
```

### Wann Strategy Pattern verwenden?

**✅ IDEAL für Strategy Pattern:**
- **Algorithmenvariationen**: Verschiedene Wege, dasselbe Problem zu lösen
- **Laufzeit-Flexibilität**: Algorithmus zur Laufzeit wechselbar
- **Erweiterbarkeit**: Häufige Hinzufügung neuer Varianten erwartet
- **A/B-Testing**: Verschiedene Ansätze vergleichen
- **Conditional Complexity**: Komplexe if-else-Strukturen vermeiden

**❌ NICHT optimal für Strategy Pattern:**
- **Einziger Algorithmus**: Nur eine Art, das Problem zu lösen
- **Statische Auswahl**: Algorithmus zur Compile-Zeit festgelegt
- **Einfache Conditionals**: Wenige, simple if-else-Bedingungen
- **Performance-kritisch**: Overhead durch Interface-Aufrufe problematisch

### Telekom-spezifische Anwendungsfälle:

**Perfekt für Strategy:**
```
✅ Device Monitoring (Router vs. Switch vs. Firewall)
✅ Routing Algorithms (OSPF vs. BGP vs. EIGRP)
✅ Load Balancing (Round-Robin vs. Weighted vs. Health-based)
✅ Backup Strategies (Full vs. Incremental vs. Differential)
✅ Scaling Policies (CPU-based vs. Traffic-based vs. Time-based)
```

**Übertreibung mit Strategy:**
```
❌ Einfache Konfigurationsvalidierung
❌ Statische Datenformatierung
❌ Einmalige Berechnungen
❌ Simple CRUD-Operationen
```
```

## 6. Testing von Strategy Pattern

### Warum Strategy Pattern testfreundlich ist

**Jede Strategy isoliert testbar:**

```typescript
// Test für spezifische Strategy - keine Abhängigkeiten
describe('PerformanceMonitoring Strategy', () => {
  let strategy: PerformanceMonitoring;
  let mockDevice: Device;
  
  beforeEach(() => {
    strategy = new PerformanceMonitoring();
    mockDevice = { id: 'test-router', type: 'router' };
  });
  
  it('should detect high CPU usage', async () => {
    // Arrange: Mock nur diese Strategy
    jest.spyOn(strategy, 'measureCpuUsage').mockResolvedValue(95);
    
    // Act: Teste nur diesen Algorithmus
    const result = await strategy.monitor(mockDevice);
    
    // Assert: Verifiziere Strategy-spezifisches Verhalten
    expect(result.status).toBe('critical');
    expect(result.recommendations).toContain('CPU optimization');
  });
});
```

**Context separat testbar:**

```typescript
// Test des Context ohne Strategy-Details
describe('DeviceMonitor Context', () => {
  let monitor: DeviceMonitor;
  let mockStrategy: jest.Mocked<MonitoringStrategy>;
  
  beforeEach(() => {
    // Mock Strategy - Details egal
    mockStrategy = {
      monitor: jest.fn(),
      getMonitoringType: jest.fn().mockReturnValue('test-strategy')
    };
    
    monitor = new DeviceMonitor(mockStrategy);
  });
  
  it('should delegate to current strategy', async () => {
    // Arrange
    const device = { id: 'test-device' };
    const expectedResult = { status: 'healthy' };
    mockStrategy.monitor.mockResolvedValue(expectedResult);
    
    // Act
    const result = await monitor.monitor(device);
    
    // Assert: Context-Verhalten, nicht Strategy-Details
    expect(mockStrategy.monitor).toHaveBeenCalledWith(device);
    expect(result).toEqual(expectedResult);
  });
  
  it('should allow strategy switching', () => {
    // Test der Strategy-Wechsel-Logik
    const newStrategy = new SecurityMonitoring();
    
    monitor.setStrategy(newStrategy);
    
    expect(monitor.getCurrentStrategy()).toBe(newStrategy);
  });
});
```

### Integration Tests

```typescript
// Vollständige Integration mit echten Strategien
describe('Monitoring Integration', () => {
  let monitor: DeviceMonitor;
  let testDevice: Device;
  
  beforeEach(() => {
    testDevice = createTestRouter();
  });
  
  it('should perform comprehensive monitoring', async () => {
    // Test mit echten Strategien
    const strategies = [
      new PerformanceMonitoring(),
      new SecurityMonitoring(),
      new ConnectivityCheck()
    ];
    
    const results = [];
    for (const strategy of strategies) {
      monitor.setStrategy(strategy);
      const result = await monitor.monitor(testDevice);
      results.push(result);
    }
    
    // Verifiziere Integration der verschiedenen Strategien
    expect(results).toHaveLength(3);
    expect(results.every(r => r.deviceId === testDevice.id)).toBe(true);
  });
});
```

### Mock vs. Real Strategy Testing

```typescript
// Wann Mocks, wann echte Strategien?

// ✅ Mocks für Context-Tests
describe('Context Logic', () => {
  // Mock Strategy - fokussiert auf Context-Verhalten
  const mockStrategy = createMockStrategy();
});

// ✅ Echte Strategien für Algorithmus-Tests
describe('Performance Strategy Algorithm', () => {
  // Echte Strategy - teste spezifische Algorithmus-Logik
  const strategy = new PerformanceMonitoring();
});

// ✅ Beides für Integration-Tests
describe('Full System Integration', () => {
  // Echte Strategien + echte Context-Implementierung
});
```
```

## 7. Praktische Implementierung

### Schritt-für-Schritt Refactoring

**Von monolithisch zu Strategy Pattern:**

```typescript
// SCHRITT 1: Bestehender monolithischer Code
class LegacyMonitor {
  monitor(device: Device, type: string) {
    if (type === 'performance') {
      // 50 Zeilen Performance-Code
      const cpu = this.getCpuUsage(device);
      const memory = this.getMemoryUsage(device);
      return { status: cpu > 80 ? 'critical' : 'healthy' };
    } else if (type === 'security') {
      // 60 Zeilen Security-Code
      const threats = this.scanThreats(device);
      return { status: threats > 0 ? 'critical' : 'healthy' };
    }
  }
}
```

```typescript
// SCHRITT 2: Strategy Interface extrahieren
interface MonitoringStrategy {
  monitor(device: Device): Promise<MonitoringResult>;
  getType(): string;
}

// SCHRITT 3: Erste Strategy extrahieren
class PerformanceStrategy implements MonitoringStrategy {
  async monitor(device: Device): Promise<MonitoringResult> {
    // Code aus if-Block extrahiert
    const cpu = await this.getCpuUsage(device);
    const memory = await this.getMemoryUsage(device);
    return { 
      deviceId: device.id,
      status: cpu > 80 ? 'critical' : 'healthy',
      metrics: { cpu, memory }
    };
  }
  
  getType(): string { return 'performance'; }
}
```

```typescript
// SCHRITT 4: Weitere Strategien extrahieren
class SecurityStrategy implements MonitoringStrategy {
  async monitor(device: Device): Promise<MonitoringResult> {
    const threats = await this.scanThreats(device);
    return {
      deviceId: device.id,
      status: threats > 0 ? 'critical' : 'healthy',
      securityDetails: { threatCount: threats }
    };
  }
  
  getType(): string { return 'security'; }
}
```

```typescript
// SCHRITT 5: Context refactoring
class RefactoredMonitor {
  private strategy: MonitoringStrategy;
  
  constructor(defaultStrategy: MonitoringStrategy) {
    this.strategy = defaultStrategy;
  }
  
  setStrategy(strategy: MonitoringStrategy) {
    this.strategy = strategy;
  }
  
  async monitor(device: Device) {
    return await this.strategy.monitor(device);
  }
}
```

### Graduelle Migration

```typescript
// Hybrid-Ansatz für schrittweise Einführung
class HybridMonitor {
  private strategies = new Map<string, MonitoringStrategy>();
  
  // Neue Strategy-basierte Monitoring
  registerStrategy(type: string, strategy: MonitoringStrategy) {
    this.strategies.set(type, strategy);
  }
  
  async monitor(device: Device, type: string) {
    // Erst prüfen: Strategy verfügbar?
    const strategy = this.strategies.get(type);
    if (strategy) {
      return await strategy.monitor(device); // ✅ Neuer Weg
    }
    
    // Fallback auf Legacy-Code
    return this.legacyMonitor(device, type); // ⚠️ Alter Weg
  }
  
  private legacyMonitor(device: Device, type: string) {
    // Bestehender Code bleibt unverändert
    // Wird schrittweise durch Strategien ersetzt
  }
}
```

## 8. Diskussion und Abwägung

### Strategy Pattern Entscheidungsmatrix

**Bewertungskriterien für Telekom-Netzwerk-Szenarien:**

| Szenario | Algorithmus-Variationen | Laufzeit-Wechsel | Erweiterbarkeit | Strategy Empfehlung |
|----------|------------------------|------------------|-----------------|---------------------|
| Device Monitoring | Hoch (5+ Typen) | Wichtig | Häufig | ✅ **IDEAL** |
| Routing Algorithms | Mittel (3-4 Typen) | Selten | Gelegentlich | ✅ **Gut geeignet** |
| Data Validation | Niedrig (2 Typen) | Nie | Selten | ❌ **Übertreibung** |
| Load Balancing | Hoch (6+ Typen) | Kritisch | Häufig | ✅ **IDEAL** |
| Log Formatting | Niedrig (2-3 Typen) | Selten | Nie | ❌ **Nicht nötig** |

### Komplexitäts-Abwägung

**Einfache Lösung (ausreichend für 2-3 Varianten):**
```typescript
class SimpleMonitor {
  monitor(device: Device, intensive: boolean = false) {
    if (intensive) {
      return this.detailedMonitoring(device);  // Aufwendige Checks
    }
    return this.basicMonitoring(device);       // Schnelle Checks
  }
}

// ✅ Vorteile: Einfach, direkt, performant
// ❌ Nachteile: Nicht erweiterbar, wird schnell unübersichtlich
```

**Strategy Pattern (sinnvoll ab 3+ komplexen Varianten):**
```typescript
class StrategyMonitor {
  constructor(private strategy: MonitoringStrategy) {}
  
  setStrategy(strategy: MonitoringStrategy) {
    this.strategy = strategy;
  }
  
  monitor(device: Device) {
    return this.strategy.monitor(device);
  }
}

// ✅ Vorteile: Erweiterbar, testbar, flexibel
// ❌ Nachteile: Mehr Klassen, indirection, setup overhead
```

### Performance-Überlegungen

**Strategy Pattern Overhead:**
```typescript
// Zusätzlicher Methodenaufruf durch Interface
interface Strategy { execute(); }
class ConcreteStrategy implements Strategy {
  execute() { /* Arbeit */ }
}

// vs. direkte Implementierung
class Direct {
  execute() { /* Arbeit */ }
}

// Overhead: ~1-5% bei häufigen Aufrufen
// Fazit: Vernachlässigbar außer in Hochfrequenz-Szenarien
```

**Wann Performance wichtiger als Flexibilität:**
```
❌ Strategy Pattern vermeiden bei:
• Echtzeit-Pakete-Processing (Mikrosekunden kritisch)
• High-Frequency Trading
• Embedded Systems mit extremen Constraints

✅ Strategy Pattern bevorzugen bei:
• Business Logic (Millisekunden unkritisch)
• Monitoring Systems
• Configuration Management
• User Interface Logic
```

### Refactoring-Empfehlungen

**Wann von if-else zu Strategy wechseln:**
```
🔄 Refactoring-Trigger:
• if-else-Block > 3 Alternativen
• Jede Alternative > 10 Zeilen Code  
• Neue Alternativen alle paar Monate
• Verschiedene Teams arbeiten an Alternativen
• A/B-Testing verschiedener Ansätze nötig
```

**Graduelle Migration:**
```typescript
// Phase 1: Hybrid-Ansatz
if (useNewStrategy && strategies.has(type)) {
  return strategies.get(type).execute();
} else {
  return legacyIfElseLogic();
}

// Phase 2: Vollständige Migration
return this.strategy.execute();
```

---

## 9. Praktische Übung (25 Minuten)

### Aufgabe: Router-spezifische Monitoring-Strategien entwickeln

**Verstehen Sie Strategy Pattern durch Konzeptarbeit:**

#### Teil A: Strategy-Design (15 Minuten)

**Diskutieren Sie in 3er-Gruppen:**

1. **Router-Monitoring-Varianten identifizieren:**
   ```
   - Welche verschiedenen Arten Router zu überwachen gibt es?
   - BGP-Sessions vs. OSPF-Nachbarn vs. Interface-Status
   - Unterschiede zwischen Core-Router und Edge-Router
   - Monitoring während Wartungszeiten vs. Normalbetrieb
   ```

2. **Strategy Interface entwerfen:**
   ```typescript
   // Was sollte JEDE Router-Monitoring-Strategy können?
   interface RouterMonitoringStrategy {
     // Welche Methoden braucht jede Strategy?
     // Welche Informationen muss jede zurückgeben?
     // Wie unterscheiden sich die Strategien?
   }
   ```

3. **Konkrete Strategien konzipieren:**
   ```
   Strategy 1: BGPSessionMonitoring
   - Was überwacht sie?
   - Wann ist der Router "healthy" vs. "critical"?
   - Welche Events werden ausgelöst?
   
   Strategy 2: InterfaceStatusMonitoring  
   - Andere Bewertungskriterien?
   - Andere Schwellwerte?
   - Andere Empfehlungen?
   ```

#### Teil B: Strategy-Anwendung (10 Minuten)

**Diskutieren Sie Anwendungsszenarien:**

1. **Wann welche Strategy?**
   ```
   - Wann würden Sie BGP-Monitoring einsetzen?
   - Wann Interface-Monitoring?
   - Können mehrere Strategien parallel laufen?
   ```

2. **Context-Design:**
   ```
   - Wie wechselt der RouterMonitor zwischen Strategien?
   - Welche Informationen braucht er dafür?
   - Wie koordiniert er mehrere Strategien?
   ```

3. **Event-Integration:**
   ```
   - Welche Events sollte jede Strategy senden?
   - Wie unterscheiden sich die Event-Typen?
   - Wer reagiert auf welche Events?
   ```

### Diskussionsergebnisse teilen:

**Jede Gruppe präsentiert (2 min/Gruppe):**
- Ihre wichtigste Erkenntnis über Strategy Pattern
- Ein konkretes Beispiel aus ihrer Router-Monitoring-Diskussion
- Eine Herausforderung, die sie identifiziert haben

---

## Zusammenfassung

### Strategy Pattern - Das Wichtigste verstehen

**Kernidee:**
> Strategy Pattern trennt **WAS** gemacht wird (Context) von **WIE** es gemacht wird (Strategy)

**Die drei Säulen:**
```
1. Strategy Interface - Definiert den Vertrag
2. Concrete Strategies - Implementieren verschiedene Algorithmen  
3. Context - Nutzt Strategien ohne deren Details zu kennen
```

**Wann Strategy Pattern einsetzen:**
```
✅ Mehrere Wege, dasselbe Problem zu lösen
✅ Algorithmus zur Laufzeit wechselbar
✅ Komplexe if-else-Strukturen vermeiden
✅ A/B-Testing verschiedener Ansätze
✅ Häufige Erweiterung um neue Varianten

❌ Nur ein Algorithmus
❌ Statische Compile-Zeit-Entscheidung
❌ Einfache 2-3 Zeilen if-else
❌ Performance extrem kritisch
```

**Telekom-Anwendungsgebiete:**
```
🔧 Device Monitoring (Router/Switch/Firewall)
🔧 Routing Algorithms (OSPF/BGP/EIGRP)
🔧 Load Balancing Strategies
🔧 Backup & Recovery Strategies
🔧 Scaling Policies
```

**Behavioral Pattern Charakteristik:**
- **Fokus:** Verhalten und Algorithmen zur Laufzeit ändern
- **Flexibilität:** Austauschbare Strategien
- **Erweiterbarkeit:** Neue Strategien ohne Änderung bestehender Klassen
- **Testbarkeit:** Jede Strategy isoliert testbar

**Nächstes Modul:** Template Method Pattern - Workflow-Strukturierung mit festen Schritten und variablen Details