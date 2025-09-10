# Block 3: Behavioral Patterns - Content Extraction for PowerPoint

## Strategy Pattern - Austauschbare Monitoring-Algorithmen

### Slide 1: Was ist hier schlecht?
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

### Slide 2: Code Smells identifiziert
- **Open-Closed Principle Verletzung**
  - Jeder neue Gerätetyp → Änderung der bestehenden Klasse
  - Risiko für bestehende Funktionalität
  - Schwierige Wartung
- **Single Responsibility Verletzung**
  - Eine Klasse kennt ALLE Monitoring-Algorithmen
  - Hohe Komplexität (100+ Zeilen pro Methode)
- **Mangelnde Flexibilität**
  - Algorithmus zur Compile-Zeit festgelegt
  - Keine Runtime-Anpassung
  - Keine A/B-Tests verschiedener Monitoring-Strategien

### Slide 3: Lösung: Strategy Pattern
- Definiert eine Familie von Algorithmen
- Kapselt jeden einzelnen und macht sie austauschbar
- **Austauschbarkeit zur Laufzeit** möglich
- **Einfache Erweiterung** ohne bestehende Klassen zu ändern
- **Isolierte Testbarkeit** jeder Strategy
- **Inversion of Control**: Context ruft Strategies auf
- **Hollywood Principle**: "Don't call us, we'll call you"

### Slide 4: Implementierung - Flexible Lösung
```typescript
// Strategy Interface
interface MonitoringStrategy {
  monitor(device: Device): Promise<MonitoringResult>;
  getMonitoringType(): string;
  getSupportedDeviceTypes(): string[];
}

// Context nutzt Strategien ohne deren Details zu kennen
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

---

## Template Method Pattern - Workflows und Event-Notification

### Slide 5: Was ist hier schlecht?
```typescript
// ❌ PROBLEM: Code-Duplizierung bei gemeinsamen Schritten
class RouterProvisioning {
  execute() {
    this.validate();    // Gemeinsam
    this.prepare();     // Gemeinsam
    this.configureRouter(); // Spezifisch
    this.deploy();      // Gemeinsam
  }
}

class SwitchProvisioning {
  execute() {
    this.validate();    // Dupliziert!
    this.prepare();     // Dupliziert!
    this.configureSwitch(); // Spezifisch
    this.deploy();      // Dupliziert!
  }
}
```

### Slide 6: Code Smells identifiziert
- **Code-Duplizierung**: Gemeinsame Schritte überall kopiert
- **Inkonsistente Implementierung**: Verschiedene validate()-Implementierungen
- **Schwierige Wartung**: Änderung im gemeinsamen Schritt erfordert Updates überall
- **Open-Closed Violation**: Neue Provisioning-Typen = viel Duplicate Code
- **Unstrukturierte Abläufe**: Kein einheitliches Workflow-Framework
- **Fehlende Standards**: Jede Implementierung macht es anders

### Slide 7: Lösung: Template Method Pattern
- **Algorithmus-Skelett** ist unveränderlich definiert
- **Spezifische Schritte** werden von Subklassen implementiert
- **Hook Methods** bieten optionale Anpassungspunkte
- **Inversion of Control**: Template ruft Subklassen-Methoden auf
- **Konsistente Ausführung**: Gleiches Workflow-Framework überall
- **DRY-Principle**: Don't Repeat Yourself
- **Hollywood Principle**: Template koordiniert den Ablauf

### Slide 8: Implementierung - Strukturierter Workflow
```typescript
// Template Method Pattern - Die Struktur
abstract class ProvisioningTemplate {
  // TEMPLATE METHOD - definiert den Ablauf
  async executeProvisioning(device: Device): Promise<Result> {
    await this.validate(device);        // ← Gemeinsam
    await this.prepare(device);         // ← Gemeinsam  
    await this.configureSpecific(device); // ← Variiert!
    await this.deploy(device);          // ← Gemeinsam
    return this.verify(device);         // ← Gemeinsam
  }
  
  // Variable Schritte (Abstract Methods)
  protected abstract configureSpecific(device: Device): Promise<void>;
}
```

---

## Observer Pattern - Event-basierte Kommunikation

### Slide 9: Was ist hier schlecht?
```typescript
// ❌ Problematisch: Tight Coupling
class DeviceManager {
  async updateDeviceStatus(device: Device, newStatus: DeviceStatus): Promise<void> {
    device.status = newStatus;
    
    // Direkte Kopplungen
    this.notificationService.sendAlert(device, newStatus);
    this.auditService.logStatusChange(device, newStatus);
    this.monitoringService.updateMetrics(device, newStatus);
    this.billingService.trackUsage(device, newStatus);
    
    // Was passiert, wenn neue Services hinzukommen?
  }
}
```

### Slide 10: Code Smells identifiziert
- **Tight Coupling**: DeviceManager kennt alle abhängigen Services
- **Open-Closed Violation**: Neue Services → Änderung von DeviceManager
- **Single Responsibility Violation**: DeviceManager macht zu viel
- **Schwierige Testbarkeit**: Muss alle Services mocken
- **Unflexible Architektur**: Services können nicht dynamisch hinzugefügt werden
- **Synchrone Abhängigkeiten**: Fehler in einem Service blockiert alle anderen

### Slide 11: Lösung: Observer Pattern
- **Lose gekoppelte Event-Verarbeitung** zwischen Objekten
- **Ein-zu-Viele-Abhängigkeit**: Subject benachrichtigt viele Observer
- **Subscribe/Unsubscribe**: Observer können sich dynamisch registrieren
- **Event-Driven Architecture**: Asynchrone Kommunikation
- **Skalierbare Notification-Systeme** möglich
- **Single Responsibility**: Jeder Observer hat spezifischen Zweck
- **Flexibles Hinzufügen/Entfernen** von Observern zur Laufzeit

### Slide 12: Implementierung - Event-Driven Architecture
```typescript
// Subject Interface
interface DeviceSubject {
  subscribe(observer: DeviceObserver): void;
  unsubscribe(observer: DeviceObserver): void;
  notifyObservers(event: DeviceEvent): Promise<void>;
}

// Observer Interface
interface DeviceObserver {
  update(event: DeviceEvent): Promise<void>;
  isInterestedIn(eventType: string): boolean;
}

// Lose gekoppelte Event-Verarbeitung
await this.notifyObservers(deviceChangeEvent);
```

---

## Command Pattern - Operations mit Undo/Redo

### Slide 13: Was ist hier schlecht?
```typescript
// ❌ PROBLEM: Direkte Methodenaufrufe ohne Kontrolle
class NetworkOperationService {
  async configureRouter(routerId: string, config: RouterConfig): Promise<void> {
    // Direkte Ausführung ohne Undo-Möglichkeit
    await this.routerService.applyConfiguration(routerId, config);
  }
  
  async createVlan(switchId: string, vlanConfig: VlanConfig): Promise<void> {
    // Keine Möglichkeit für Rollback
    await this.switchService.addVlan(switchId, vlanConfig);
  }
  
  // Wie kann man diese Operationen rückgängig machen?
  // Wie kann man sie in Makros kombinieren?
}
```

### Slide 14: Code Smells identifiziert
- **Keine Undo/Redo-Funktionalität**: Kritische Operationen nicht rückgängig machbar
- **Keine Operationen-Queue**: Commands können nicht gespeichert und später ausgeführt werden
- **Schwierige Transaktionen**: Mehrere Aktionen nicht als atomische Einheit
- **Kein Audit Trail**: Compliance-Anforderungen nicht erfüllbar
- **Monolithische Ausführung**: Direkte Kopplung zwischen Request und Execution
- **Fehlende Batch-Operations**: Komplexe Operationssequenzen nicht möglich

### Slide 15: Lösung: Command Pattern
- **Request als Objekt**: Aktionen werden zu manipulierbaren Objekten
- **Undo/Redo-Fähigkeit**: Operationen können rückgängig gemacht werden
- **Entkopplung**: Invoker kennt nicht die Details der Ausführung
- **Queue-Unterstützung**: Commands können gespeichert und später ausgeführt werden
- **Macro Commands**: Komplexe Operationssequenzen als atomische Einheit
- **CQRS Integration**: Trennung von Commands und Queries
- **Audit Trail**: Vollständige Nachverfolgung aller Änderungen

### Slide 16: Implementierung - Undo/Redo Operations
```typescript
// Command Interface
interface NetworkCommand {
  execute(): Promise<CommandResult>;
  undo(): Promise<CommandResult>;
  canUndo(): boolean;
  getDescription(): string;
}

// Macro Command für transaktionale Operationen
class MacroCommand implements Command {
  execute(): void {
    for (const command of this.commands) {
      try {
        command.execute();
      } catch (error) {
        this.rollbackExecutedCommands(); // Automatic rollback
        throw error;
      }
    }
  }
}
```

---

## State Pattern - Device Lifecycle Management

### Slide 17: Was ist hier schlecht?
```typescript
// ❌ Problematisch: Zustandslogik in monolithischer Klasse
class NetworkDevice {
  private status: DeviceStatus = 'inactive';
  
  async activate(): Promise<void> {
    if (this.status === 'inactive') {
      await this.performInitialization();
      this.status = 'active';
    } else if (this.status === 'maintenance') {
      await this.exitMaintenanceMode();
      this.status = 'active';
    } else {
      throw new Error(`Cannot activate device in status: ${this.status}`);
    }
  }
  // Weitere komplexe if-else Logik für jeden State...
}
```

### Slide 18: Code Smells identifiziert
- **Zustandslogik verteilt** sich über die gesamte Klasse
- **Schwierige Erweiterung** um neue Zustände
- **Inkonsistente Implementierung** von Transitions
- **Komplexe Zustandsvalidierung** in if-else-Kaskaden
- **Open-Closed Violation**: Neue States erfordern Klassenänderung
- **Single Responsibility Violation**: Eine Klasse verwaltet alle States
- **Unübersichtliche Transitionen**: State-Wechsel-Logik verstreut

### Slide 19: Lösung: State Pattern
- **Zustandsspezifisches Verhalten** in separate Klassen gekapselt
- **Saubere Trennung von Zustandslogik** für jeden State
- **Einfache Erweiterung** um neue Zustände ohne bestehende zu ändern
- **Konsistente State Transitions** durch definierte Interfaces
- **Event-Driven Architecture Unterstützung** mit State Events
- **State History Tracking**: Verfolgung aller Zustandswechsel
- **Concurrent Transition Prevention**: Schutz vor gleichzeitigen Änderungen

### Slide 20: Implementierung - Zustandsmaschinen
```typescript
// State Interface
interface DeviceState {
  activate(context: DeviceContext): Promise<StateTransitionResult>;
  deactivate(context: DeviceContext): Promise<StateTransitionResult>;
  enterMaintenance(context: DeviceContext): Promise<StateTransitionResult>;
  
  getStateName(): string;
  getAllowedTransitions(): string[];
}

// Context delegiert an aktuellen State
class DeviceContext {
  private currentState: DeviceState;
  
  async activate(): Promise<StateTransitionResult> {
    return await this.currentState.activate(this);
  }
}
```

---

## Chain of Responsibility - Request Processing Pipeline

### Slide 21: Was ist hier schlecht?
```typescript
// ❌ Problematisch: Alle Verarbeitungslogik in einer Klasse
class NetworkRequestProcessor {
  async processRequest(request: NetworkRequest): Promise<NetworkResponse> {
    // Authentication
    if (!this.isAuthenticated(request)) return this.createErrorResponse('Auth failed');
    // Authorization  
    if (!this.isAuthorized(request)) return this.createErrorResponse('Authz failed');
    // Rate Limiting
    if (this.isRateLimited(request)) return this.createErrorResponse('Rate limit');
    // Input Validation
    if (!this.isValidInput(request)) return this.createErrorResponse('Invalid');
    // Business Logic
    return await this.executeBusinessLogic(request);
  }
}
```

### Slide 22: Code Smells identifiziert
- **Single Responsibility Violation**: Eine Klasse macht alles
- **Open-Closed Violation**: Neue Handler erfordern Klassenänderung
- **Schwierige Testbarkeit**: Muss alle Handler-Logiken testen
- **Unflexible Reihenfolge**: Processing-Pipeline ist fest kodiert
- **Tight Coupling**: Request-Processor kennt alle Handler-Details
- **Keine Wiederverwendbarkeit**: Handler können nicht einzeln genutzt werden
- **Monolithische Struktur**: Schwierig zu erweitern und zu warten

### Slide 23: Lösung: Chain of Responsibility Pattern
- **Request von verschiedenen Handlern** verarbeitet werden kann
- **Processing-Pipeline dynamisch konfigurierbar** zur Laufzeit
- **Sender und Empfänger entkoppelt**: Keine direkte Abhängigkeit
- **Handler zur Laufzeit** hinzufügbar und entfernbar
- **Single Responsibility**: Jeder Handler hat spezifischen Zweck
- **Flexible Request-Flow**: Verschiedene Chains für verschiedene Request-Typen
- **Middleware-Pattern**: Ähnlich Express.js oder ASP.NET Core Middleware

### Slide 24: Implementierung - Request-Pipelines
```typescript
// Handler Chain
abstract class RequestHandler {
  protected nextHandler?: RequestHandler;
  
  setNext(handler: RequestHandler): RequestHandler {
    this.nextHandler = handler;
    return handler;
  }
  
  async handle(request: NetworkRequest): Promise<NetworkResponse | null> {
    const response = await this.process(request);
    
    if (response) return response;
    
    if (this.nextHandler) {
      return await this.nextHandler.handle(request);
    }
    return null;
  }
}

// Flexible Pipeline-Konfiguration
authHandler.setNext(authzHandler).setNext(validationHandler);
```

---

## Block 3 Zusammenfassung

### Slide 25: Behavioral Patterns Überblick
- **Strategy Pattern**: Austauschbare Algorithmen zur Laufzeit
- **Template Method**: Workflow-Strukturierung mit festen und variablen Schritten
- **Observer Pattern**: Event-basierte lose Kopplung
- **Command Pattern**: Undo/Redo und transaktionale Operationen
- **State Pattern**: Zustandsmaschinen für komplexe Lifecycles
- **Chain of Responsibility**: Flexible Request-Processing-Pipelines

**Fokus auf Verhalten und Interaktionen zwischen Objekten**

### Slide 26: Telekom-Anwendungsgebiete
- **Device Monitoring**: Strategy für verschiedene Gerätetypen
- **Network Provisioning**: Template Method für konsistente Workflows
- **Event Processing**: Observer für Monitoring und Audit-Systeme
- **Configuration Management**: Command für sichere Rollback-Fähigkeiten
- **Device Lifecycle**: State Pattern für komplexe Gerätezustände
- **Request Processing**: Chain of Responsibility für API-Pipelines

**Behavioral Patterns strukturieren komplexe Telekom-Netzwerk-Operationen**

### Slide 27: Key Takeaways
- **Flexibilität zur Laufzeit**: Verhalten kann dynamisch geändert werden
- **Lose Kopplung**: Komponenten interagieren über definierte Interfaces
- **Event-Driven Architecture**: Observer und State Patterns unterstützen EDA
- **Erweiterbarkeit**: Neue Verhaltensweisen ohne Änderung bestehender Klassen
- **Testbarkeit**: Jedes Verhalten isoliert testbar
- **Single Responsibility**: Klare Trennung von Verantwortlichkeiten

**Behavioral Patterns sind essentiell für maintainable, skalierbare Systeme**

---

## Speaker Notes

**Strategy Pattern:**
- Erklären Sie den Unterschied zwischen Strategy und einfachen if-else Strukturen
- Betonen Sie die Laufzeit-Flexibilität für A/B-Testing
- Zeigen Sie Telekom-spezifische Beispiele mit Router/Switch/Firewall Monitoring

**Template Method:**
- Verdeutlichen Sie das Hollywood Principle: "Don't call us, we'll call you"
- Erklären Sie den Unterschied zwischen Abstract Methods und Hook Methods
- Betonen Sie die Konsistenz bei Provisioning-Workflows

**Observer Pattern:**
- Vergleichen Sie mit Event-Driven Architecture Patterns
- Erklären Sie lose Kopplung vs. tight coupling
- Zeigen Sie Skalierbarkeits-Vorteile bei vielen Event-Konsumenten

**Command Pattern:**
- Betonen Sie die Wichtigkeit für kritische Netzwerk-Operationen
- Erklären Sie Macro Commands für atomare Operationssequenzen
- Verbinden Sie mit CQRS für Read/Write-Trennung

**State Pattern:**
- Zeigen Sie komplexe Device-Lifecycles (inactive → initializing → active → maintenance → error)
- Erklären Sie Event-Driven State Transitions
- Betonen Sie Concurrent Transition Prevention

**Chain of Responsibility:**
- Vergleichen Sie mit Middleware-Patterns in Web-Frameworks
- Zeigen Sie Pipeline-Konfigurierbarkeit
- Erklären Sie Request-Flow und Handler-Verkettung