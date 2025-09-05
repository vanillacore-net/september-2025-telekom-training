# Tag 3 - Modul 2: Template Method & Observer Pattern
## Workflows und Event-Notification

### Lernziele
- **Template Method Pattern** Konzept und Verhaltensaspekte verstehen
- **Observer Pattern** Event-Kommunikation und lose Kopplung verstehen
- Event-Driven Architecture Prinzipien in Telekom-Kontexten verstehen
- Pattern-Unterschiede und Anwendungsbereiche erkennen

---

## 1. Template Method Pattern - Das Verhalten verstehen

### Problem und Motivation: Wiederkehrende aber variierende Abläufe

In komplexen Systemen begegnen wir häufig Situationen, wo **ähnliche Abläufe mit unterschiedlichen Details** existieren. Betrachten wir die Telekom-Netzwerk-Provisionierung:

**Gemeinsame Struktur aller Provisionierungs-Workflows:**
1. **Validierung** der Konfiguration
2. **Vorbereitung** der Umgebung
3. **Spezifische Konfiguration** (hier unterscheiden sich die Workflows)
4. **Deployment** der Konfiguration
5. **Verifikation** des Ergebnisses

**Das Problem:** Ohne strukturierte Lösung entsteht Code-Duplizierung bei gemeinsamen Schritten, während die spezifischen Schritte verstreut implementiert werden.

### Template Method Pattern - Die Verhaltens-Struktur

Das **Template Method Pattern** definiert das **Skelett eines Algorithmus** in einer Basisklasse, während es Unterklassen erlaubt, **spezifische Schritte zu überschreiben** ohne die Gesamtstruktur zu ändern.

**ASCII-UML: Template Method Struktur**
```
┌─────────────────────────┐
│  AbstractWorkflow       │
│  ««abstract»»           │
├─────────────────────────┤
│ + executeWorkflow()     │ ← Template Method (konkret)
│ # validateConfig()      │ ← Gemeinsame Schritte
│ # prepareEnvironment()  │ ← Gemeinsame Schritte  
│ # deployConfig()        │ ← Gemeinsame Schritte
│ # configureSpecifics()  │ ← Abstract (variiert)
│ # customizeSteps()      │ ← Hook Method (optional)
└─────────────────────────┘
           △
           │ erbt
  ┌────────┴────────┐
  │                 │
┌─────────────────┐ ┌─────────────────┐
│ RouterWorkflow  │ │ SwitchWorkflow  │
├─────────────────┤ ├─────────────────┤
│ # configSpecif. │ │ # configSpecif. │
│ # customSteps() │ │ # customSteps() │
└─────────────────┘ └─────────────────┘
```

**Verhaltens-Prinzipien:**
- **Inversion of Control**: Die abstrakte Klasse steuert den Ablauf
- **Hollywood Principle**: "Don't call us, we'll call you"
- **Template definiert WAS und WANN**, Subklassen definieren **WIE**

### Template Method in Aktion - Konzeptuelle Betrachtung

**Einfaches Beispiel: Provisionierungs-Template**

```typescript
// Template Method Pattern - Die Struktur
abstract class ProvisioningTemplate {
  
  // TEMPLATE METHOD - definiert den Ablauf
  async executeProvisioning(device: Device): Promise<Result> {
    // Festgelegter Algorithmus
    await this.validate(device);        // ← Gemeinsam
    await this.prepare(device);         // ← Gemeinsam  
    await this.configureSpecific(device); // ← Variiert!
    await this.deploy(device);          // ← Gemeinsam
    return this.verify(device);         // ← Gemeinsam
  }
  
  // Gemeinsame Schritte (in Template implementiert)
  protected async validate(device: Device): Promise<void> {
    // Grundvalidierung für alle Devices
    if (!device.id || !device.type) {
      throw new Error('Invalid device');
    }
    // Spezifische Validierung delegieren
    await this.validateSpecific(device);
  }
  
  // Variable Schritte (Abstract Methods)
  protected abstract configureSpecific(device: Device): Promise<void>;
  protected abstract validateSpecific(device: Device): Promise<void>;
  
  // Hook Methods (optional überschreibbar)
  protected async customizeDeployment(device: Device): Promise<void> {
    // Standard-Verhalten, kann überschrieben werden
  }
}

// Konkrete Implementierung
class RouterProvisioning extends ProvisioningTemplate {
  protected async configureSpecific(device: Device): Promise<void> {
    // Router-spezifische Konfiguration
    await this.configureRouting(device);
  }
  
  protected async validateSpecific(device: Device): Promise<void> {
    // Router-spezifische Validierung
    await this.checkRoutingProtocols(device);
  }
}
```

**Kernverhalten des Template Method Patterns:**
- **Algorithmus-Skelett** ist unveränderlich
- **Spezifische Schritte** werden von Subklassen definiert
- **Hook Methods** bieten optionale Anpassungspunkte
- **Inversion of Control**: Template ruft Subklassen-Methoden auf

---

## 2. Observer Pattern - Event-basierte Kommunikation verstehen

### Problem und Motivation: Gekoppelte Abhängigkeiten

Stellen Sie sich vor: Ein Device ändert seinen Status. **Wer muss benachrichtigt werden?**
- Monitoring-System
- Audit-Logger  
- Notification-Service
- Billing-System
- Report-Generator

**Das Problem ohne Observer Pattern:**
```

---

## 3. Observer Pattern - Event Notification

### Problem: Gekoppelte Event-Verarbeitung

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

### Observer Pattern Implementierung

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
  getObserverType(): string;
  isInterestedIn(eventType: string): boolean;
}

// Device Events
interface DeviceEvent {
  type: DeviceEventType;
  device: Device;
  timestamp: Date;
  metadata?: Record<string, any>;
}

type DeviceEventType = 'StatusChanged' | 'ConfigurationUpdated' | 'PerformanceAlert' | 'MaintenanceScheduled';
```

### Observable Device Manager

```typescript
class ObservableDeviceManager implements DeviceSubject {
  private observers: Set<DeviceObserver> = new Set();
  private devices: Map<string, Device> = new Map();
  
  subscribe(observer: DeviceObserver): void {
    this.observers.add(observer);
    console.log(`Observer ${observer.getObserverType()} subscribed`);
  }
  
  unsubscribe(observer: DeviceObserver): void {
    this.observers.delete(observer);
    console.log(`Observer ${observer.getObserverType()} unsubscribed`);
  }
  
  async notifyObservers(event: DeviceEvent): Promise<void> {
    const interestedObservers = Array.from(this.observers)
      .filter(observer => observer.isInterestedIn(event.type));
    
    // Parallele Benachrichtigung für bessere Performance
    await Promise.allSettled(
      interestedObservers.map(observer => this.notifyObserverSafely(observer, event))
    );
  }
  
  private async notifyObserverSafely(observer: DeviceObserver, event: DeviceEvent): Promise<void> {
    try {
      await observer.update(event);
    } catch (error) {
      console.error(`Observer ${observer.getObserverType()} failed to handle event ${event.type}:`, error);
      // Error-Recovery oder Dead-Letter-Queue
    }
  }
  
  // Business Logic mit Observer Notifications
  async updateDeviceStatus(deviceId: string, newStatus: DeviceStatus): Promise<void> {
    const device = this.devices.get(deviceId);
    if (!device) {
      throw new Error(`Device ${deviceId} not found`);
    }
    
    const oldStatus = device.status;
    device.status = newStatus;
    
    // Event generieren und Observer benachrichtigen
    const event: DeviceEvent = {
      type: 'StatusChanged',
      device,
      timestamp: new Date(),
      metadata: {
        oldStatus,
        newStatus,
        changedBy: 'system' // oder User-ID
      }
    };
    
    await this.notifyObservers(event);
  }
  
  async updateDeviceConfiguration(deviceId: string, newConfig: DeviceConfig): Promise<void> {
    const device = this.devices.get(deviceId);
    if (!device) {
      throw new Error(`Device ${deviceId} not found`);
    }
    
    const oldConfig = { ...device.config };
    device.config = newConfig;
    
    const event: DeviceEvent = {
      type: 'ConfigurationUpdated',
      device,
      timestamp: new Date(),
      metadata: {
        oldConfig,
        newConfig,
        configChanges: this.calculateConfigDifferences(oldConfig, newConfig)
      }
    };
    
    await this.notifyObservers(event);
  }
}
```

### Konkrete Observer Implementierungen

```typescript
// Notification Observer
class NotificationObserver implements DeviceObserver {
  private notificationService: NotificationService;
  private notificationRules: NotificationRule[];
  
  constructor(notificationService: NotificationService, rules: NotificationRule[]) {
    this.notificationService = notificationService;
    this.notificationRules = rules;
  }
  
  async update(event: DeviceEvent): Promise<void> {
    const applicableRules = this.notificationRules.filter(rule => 
      rule.eventTypes.includes(event.type) &&
      rule.deviceFilter(event.device)
    );
    
    for (const rule of applicableRules) {
      const notification: Notification = {
        type: rule.notificationType,
        priority: this.calculatePriority(event, rule),
        message: this.generateMessage(event, rule),
        recipients: rule.recipients,
        timestamp: new Date()
      };
      
      await this.notificationService.send(notification);
    }
  }
  
  getObserverType(): string {
    return 'NotificationObserver';
  }
  
  isInterestedIn(eventType: string): boolean {
    return this.notificationRules.some(rule => rule.eventTypes.includes(eventType));
  }
  
  private calculatePriority(event: DeviceEvent, rule: NotificationRule): NotificationPriority {
    if (event.type === 'StatusChanged' && event.metadata?.newStatus === 'critical') {
      return 'high';
    }
    if (event.type === 'PerformanceAlert') {
      return 'medium';
    }
    return rule.defaultPriority;
  }
}

// Audit Observer
class AuditObserver implements DeviceObserver {
  private auditService: AuditService;
  private auditableEvents: Set<string>;
  
  constructor(auditService: AuditService) {
    this.auditService = auditService;
    this.auditableEvents = new Set(['StatusChanged', 'ConfigurationUpdated']);
  }
  
  async update(event: DeviceEvent): Promise<void> {
    const auditEntry: AuditEntry = {
      entityType: 'Device',
      entityId: event.device.id,
      eventType: event.type,
      timestamp: event.timestamp,
      changes: this.extractChanges(event),
      metadata: {
        deviceType: event.device.type,
        location: event.device.location,
        ...event.metadata
      }
    };
    
    await this.auditService.logEntry(auditEntry);
  }
  
  getObserverType(): string {
    return 'AuditObserver';
  }
  
  isInterestedIn(eventType: string): boolean {
    return this.auditableEvents.has(eventType);
  }
  
  private extractChanges(event: DeviceEvent): AuditChanges {
    if (event.type === 'StatusChanged') {
      return {
        field: 'status',
        oldValue: event.metadata?.oldStatus,
        newValue: event.metadata?.newStatus
      };
    }
    
    if (event.type === 'ConfigurationUpdated') {
      return {
        field: 'configuration',
        changes: event.metadata?.configChanges
      };
    }
    
    return {};
  }
}

// Monitoring Observer
class MonitoringObserver implements DeviceObserver {
  private metricsCollector: MetricsCollector;
  private alertManager: AlertManager;
  
  constructor(metricsCollector: MetricsCollector, alertManager: AlertManager) {
    this.metricsCollector = metricsCollector;
    this.alertManager = alertManager;
  }
  
  async update(event: DeviceEvent): Promise<void> {
    // Metriken sammeln
    await this.collectEventMetrics(event);
    
    // Alerts prüfen
    if (this.shouldTriggerAlert(event)) {
      await this.triggerAlert(event);
    }
  }
  
  getObserverType(): string {
    return 'MonitoringObserver';
  }
  
  isInterestedIn(eventType: string): boolean {
    return ['StatusChanged', 'PerformanceAlert', 'ConfigurationUpdated'].includes(eventType);
  }
  
  private async collectEventMetrics(event: DeviceEvent): Promise<void> {
    const metrics = {
      [`device_events_total{type="${event.type}",device_type="${event.device.type}"}`]: 1,
      [`device_status{device_id="${event.device.id}"}`]: event.device.status === 'healthy' ? 1 : 0
    };
    
    await this.metricsCollector.record(metrics);
  }
  
  private shouldTriggerAlert(event: DeviceEvent): boolean {
    if (event.type === 'StatusChanged' && event.metadata?.newStatus === 'critical') {
      return true;
    }
    
    if (event.type === 'PerformanceAlert') {
      return true;
    }
    
    return false;
  }
}
```

---

## 4. Event-Driven Architecture Integration

### Event Streaming mit Observer Pattern

```typescript
// Event Stream Observer für Kafka/RabbitMQ Integration
class EventStreamObserver implements DeviceObserver {
  private eventStream: EventStream;
  private streamingConfig: StreamingConfig;
  
  constructor(eventStream: EventStream, config: StreamingConfig) {
    this.eventStream = eventStream;
    this.streamingConfig = config;
  }
  
  async update(event: DeviceEvent): Promise<void> {
    const streamEvent: StreamEvent = {
      key: event.device.id,
      value: {
        ...event,
        source: 'device-manager',
        schemaVersion: '1.0'
      },
      headers: {
        'event-type': event.type,
        'device-type': event.device.type,
        'timestamp': event.timestamp.toISOString()
      }
    };
    
    const topic = this.determineTopicForEvent(event);
    await this.eventStream.publish(topic, streamEvent);
  }
  
  getObserverType(): string {
    return 'EventStreamObserver';
  }
  
  isInterestedIn(eventType: string): boolean {
    return this.streamingConfig.eventTypes.includes(eventType);
  }
  
  private determineTopicForEvent(event: DeviceEvent): string {
    const topicMap = {
      'StatusChanged': 'telekom.devices.status',
      'ConfigurationUpdated': 'telekom.devices.config',
      'PerformanceAlert': 'telekom.devices.performance',
      'MaintenanceScheduled': 'telekom.devices.maintenance'
    };
    
    return topicMap[event.type] || 'telekom.devices.general';
  }
}
```

### Workflow Integration mit Observer Pattern

```typescript
// Workflow Observer für automatische Workflow-Auslösung
class WorkflowTriggerObserver implements DeviceObserver {
  private workflowEngine: WorkflowEngine;
  private triggerRules: WorkflowTriggerRule[];
  
  constructor(workflowEngine: WorkflowEngine, rules: WorkflowTriggerRule[]) {
    this.workflowEngine = workflowEngine;
    this.triggerRules = rules;
  }
  
  async update(event: DeviceEvent): Promise<void> {
    const applicableRules = this.triggerRules.filter(rule => 
      rule.eventType === event.type &&
      rule.condition(event)
    );
    
    for (const rule of applicableRules) {
      const workflowInput = {
        deviceId: event.device.id,
        triggerEvent: event,
        parameters: rule.parameters
      };
      
      await this.workflowEngine.startWorkflow(rule.workflowType, workflowInput);
    }
  }
  
  getObserverType(): string {
    return 'WorkflowTriggerObserver';
  }
  
  isInterestedIn(eventType: string): boolean {
    return this.triggerRules.some(rule => rule.eventType === eventType);
  }
}
```

---

## 5. Testing-Strategien

### Template Method Tests

```typescript
describe('RouterProvisioningWorkflow', () => {
  let workflow: RouterProvisioningWorkflow;
  let mockEventPublisher: jest.Mocked<EventPublisher>;
  let mockDevice: Device;
  
  beforeEach(() => {
    mockEventPublisher = {
      publish: jest.fn().mockResolvedValue(undefined)
    };
    
    workflow = new RouterProvisioningWorkflow(mockEventPublisher);
    
    mockDevice = {
      id: 'router-123',
      type: 'router',
      config: {
        routingProtocols: [{ type: 'OSPF', settings: {} }],
        interfaces: ['eth0', 'eth1'],
        firewallRules: [],
        qosSettings: {}
      } as RouterConfig
    };
  });
  
  describe('executeWorkflow', () => {
    it('should execute complete router provisioning workflow', async () => {
      // Arrange
      jest.spyOn(workflow as any, 'configureOspf').mockResolvedValue(undefined);
      jest.spyOn(workflow as any, 'setupFirewallRules').mockResolvedValue(undefined);
      
      // Act
      const result = await workflow.executeWorkflow(mockDevice);
      
      // Assert
      expect(result.success).toBe(true);
      expect(mockEventPublisher.publish).toHaveBeenCalledWith(
        expect.objectContaining({ type: 'WorkflowStarted' })
      );
      expect(mockEventPublisher.publish).toHaveBeenCalledWith(
        expect.objectContaining({ type: 'WorkflowCompleted' })
      );
    });
    
    it('should handle workflow errors and execute rollback', async () => {
      // Arrange
      const error = new Error('OSPF configuration failed');
      jest.spyOn(workflow as any, 'configureOspf').mockRejectedValue(error);
      jest.spyOn(workflow as any, 'executeRollback').mockResolvedValue(undefined);
      
      // Act & Assert
      await expect(workflow.executeWorkflow(mockDevice)).rejects.toThrow(error);
      
      expect(mockEventPublisher.publish).toHaveBeenCalledWith(
        expect.objectContaining({ type: 'WorkflowFailed' })
      );
      expect(workflow['executeRollback']).toHaveBeenCalledWith(mockDevice);
    });
  });
});
```

### Observer Pattern Tests

```typescript
describe('ObservableDeviceManager', () => {
  let deviceManager: ObservableDeviceManager;
  let mockNotificationObserver: jest.Mocked<DeviceObserver>;
  let mockAuditObserver: jest.Mocked<DeviceObserver>;
  
  beforeEach(() => {
    deviceManager = new ObservableDeviceManager();
    
    mockNotificationObserver = {
      update: jest.fn().mockResolvedValue(undefined),
      getObserverType: jest.fn().mockReturnValue('NotificationObserver'),
      isInterestedIn: jest.fn().mockReturnValue(true)
    };
    
    mockAuditObserver = {
      update: jest.fn().mockResolvedValue(undefined),
      getObserverType: jest.fn().mockReturnValue('AuditObserver'),
      isInterestedIn: jest.fn().mockReturnValue(true)
    };
    
    deviceManager.subscribe(mockNotificationObserver);
    deviceManager.subscribe(mockAuditObserver);
  });
  
  describe('updateDeviceStatus', () => {
    it('should notify all interested observers', async () => {
      // Arrange
      const device: Device = {
        id: 'device-123',
        type: 'router',
        status: 'healthy'
      };
      deviceManager['devices'].set('device-123', device);
      
      // Act
      await deviceManager.updateDeviceStatus('device-123', 'critical');
      
      // Assert
      expect(mockNotificationObserver.update).toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'StatusChanged',
          device: expect.objectContaining({ id: 'device-123' }),
          metadata: expect.objectContaining({
            oldStatus: 'healthy',
            newStatus: 'critical'
          })
        })
      );
      
      expect(mockAuditObserver.update).toHaveBeenCalledWith(
        expect.objectContaining({ type: 'StatusChanged' })
      );
    });
    
    it('should handle observer failures gracefully', async () => {
      // Arrange
      mockNotificationObserver.update.mockRejectedValue(new Error('Notification failed'));
      const device: Device = { id: 'device-456', type: 'switch', status: 'healthy' };
      deviceManager['devices'].set('device-456', device);
      
      // Act - should not throw
      await deviceManager.updateDeviceStatus('device-456', 'warning');
      
      // Assert - andere Observer sollten trotzdem benachrichtigt werden
      expect(mockAuditObserver.update).toHaveBeenCalled();
    });
  });
});
```

---

## 6. Pattern Kombinationen und Best Practices

### Template Method + Observer Kombination

```typescript
// Observable Workflow Template
abstract class ObservableWorkflowTemplate extends DeviceWorkflowTemplate implements DeviceSubject {
  private observers: Set<WorkflowObserver> = new Set();
  
  subscribe(observer: WorkflowObserver): void {
    this.observers.add(observer);
  }
  
  unsubscribe(observer: WorkflowObserver): void {
    this.observers.delete(observer);
  }
  
  protected async publishWorkflowEvent(eventType: string, data: any): Promise<void> {
    // Parent Event Publishing
    await super.publishWorkflowEvent(eventType, data);
    
    // Observer Notification
    const workflowEvent: WorkflowEvent = {
      workflowId: this.workflowId,
      type: eventType,
      data,
      timestamp: new Date()
    };
    
    await this.notifyWorkflowObservers(workflowEvent);
  }
  
  private async notifyWorkflowObservers(event: WorkflowEvent): Promise<void> {
    await Promise.allSettled(
      Array.from(this.observers).map(observer => observer.onWorkflowEvent(event))
    );
  }
}
```

---

## 7. Praktische Übung (25 Minuten)

### Aufgabe: Device Maintenance Workflow mit Observer Integration

Implementieren Sie:

1. **MaintenanceWorkflowTemplate:**
   - Template Method für Maintenance-Schritte
   - Hook Methods für device-spezifische Wartung
   - Error Handling und Rollback

2. **Concrete Maintenance Workflows:**
   - RouterMaintenanceWorkflow
   - SwitchMaintenanceWorkflow

3. **Maintenance Observer:**
   - Tracking von Maintenance-Events
   - Scheduling Follow-up Actions
   - Compliance Reporting

### Template zum Start:

```typescript
abstract class MaintenanceWorkflowTemplate {
  // TODO: Template Method implementieren
  async executeMaintenance(device: Device, maintenanceType: MaintenanceType): Promise<MaintenanceResult> {
    // TODO: Wartungsworkflow-Schritte definieren
  }
  
  // TODO: Abstract Methods definieren
  protected abstract prepareMaintenanceEnvironment(device: Device): Promise<void>;
  protected abstract executeDeviceSpecificMaintenance(device: Device, type: MaintenanceType): Promise<void>;
  protected abstract validateMaintenanceCompletion(device: Device): Promise<void>;
}

class MaintenanceTrackingObserver implements DeviceObserver {
  // TODO: Maintenance Event Handling implementieren
}
```

### Bonus-Aufgaben:
1. Integration mit Workflow-Scheduling System
2. Cascade Maintenance für abhangige Devices
3. Predictive Maintenance basierend auf Event-Patterns

### Diskussionspunkte:
- Wie würden Sie Maintenance Windows koordinieren?
- Welche Events sind für Compliance-Reporting relevant?
- Wie kann Observer Pattern für Service-Orchestrierung genutzt werden?

---

## 8. Pattern vs. einfache Lösung

### Wann Template Method verwenden?

**✅ Verwenden wenn:**
- Workflows mit wiederkehrenden Schritten existieren
- Algorithmus-Struktur fest, Details variieren
- Code-Duplizierung vermieden werden soll
- Konsistente Workflow-Ausführung wichtig ist

**❌ Nicht verwenden wenn:**
- Workflows zu unterschiedlich sind
- Nur ein Workflow-Typ existiert
- Über-Engineering droht

### Wann Observer Pattern verwenden?

**✅ Verwenden wenn:**
- Viele Objekte auf Änderungen reagieren müssen
- Lose Kopplung zwischen Komponenten gewünscht
- Event-Driven Architecture implementiert wird
- Skalierbare Notification-Systeme benötigt werden

**❌ Nicht verwenden wenn:**
- Nur wenige, statische Abhangigkeiten existieren
- Reihenfolge der Benachrichtigungen kritisch ist
- Performance-Overhead problematisch ist

---

## Zusammenfassung

**Template Method Pattern:**
- Definiert Workflow-Struktur, erlaubt Variationen
- Reduziert Code-Duplizierung
- Erzwingt konsistente Ausführung
- Unterstützt Open-Closed Principle

**Observer Pattern:**
- Ermöglicht lose gekoppelte Event-Verarbeitung
- Skalierbare Notification-Systeme
- Unterstützt Event-Driven Architecture
- Flexibles Hinzufügen/Entfernen von Observern

**Key Takeaways:**
- Behavioral Patterns strukturieren Interaktionen zwischen Objekten
- Event-Driven Architecture profitiert erheblich von diesen Patterns
- Template Method + Observer = mächtige Kombination für Workflow-Systeme
- Testing-Strategien sind essentiell für komplexe Behavioral Patterns

**Nächste Schritte:**
- Command Pattern für Undo/Redo Operations
- State Pattern für Device Lifecycle Management
- Chain of Responsibility für Request Processing