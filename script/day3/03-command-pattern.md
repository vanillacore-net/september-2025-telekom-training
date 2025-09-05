# Tag 3 - Modul 3: Command Pattern
## Operations mit Undo/Redo für Network Operations

### Lernziele
- **Command Pattern** für Network Operations verstehen und implementieren
- **Undo/Redo-Mechanismen** für kritische Netzwerk-Änderungen entwickeln
- **Macro Commands** für komplexe Operationssequenzen einsetzen
- **CQRS (Command Query Responsibility Segregation)** in Telekom-Szenarien anwenden

---

## 1. Command Pattern Grundlagen

### Problem: Gekoppelte Operation-Ausführung

```typescript
// ❌ Problematisch: Direkte Methodenaufrufe ohne Kontrolle
class NetworkOperationService {
  async configureRouter(routerId: string, config: RouterConfig): Promise<void> {
    // Direkte Ausführung ohne Undo-Möglichkeit
    await this.routerService.applyConfiguration(routerId, config);
  }
  
  async createVlan(switchId: string, vlanConfig: VlanConfig): Promise<void> {
    // Keine Möglichkeit für Rollback
    await this.switchService.addVlan(switchId, vlanConfig);
  }
  
  async updateFirewallRules(firewallId: string, rules: FirewallRule[]): Promise<void> {
    // Kein Audit Trail
    await this.firewallService.updateRules(firewallId, rules);
  }
  
  // Wie kann man diese Operationen rückgängig machen?
  // Wie kann man sie in Makros kombinieren?
  // Wie kann man sie queuen und später ausführen?
}
```

**Probleme:**
- Keine Undo/Redo-Funktionalität
- Keine Möglichkeit, Operationen zu queuen
- Schwierige Implementierung von Transaktionen
- Kein Audit Trail für Compliance

---

## 2. Command Pattern Implementierung

### Command Interface und Base Classes

```typescript
// Core Command Interface
interface NetworkCommand {
  execute(): Promise<CommandResult>;
  undo(): Promise<CommandResult>;
  canUndo(): boolean;
  getDescription(): string;
  getCommandId(): string;
  getTimestamp(): Date;
  getAffectedResources(): NetworkResource[];
}

// Command Result für detaillierte Rückmeldung
interface CommandResult {
  success: boolean;
  message: string;
  data?: any;
  errors?: string[];
  executionTime: number;
  affectedResources: NetworkResource[];
}

// Network Resource Identification
interface NetworkResource {
  id: string;
  type: 'router' | 'switch' | 'firewall' | 'vlan' | 'interface';
  name: string;
  location?: string;
}

// Abstract Base Command mit gemeinsamer Funktionalität
abstract class BaseNetworkCommand implements NetworkCommand {
  protected commandId: string;
  protected timestamp: Date;
  protected executionState: CommandExecutionState;
  protected originalState?: any;
  protected eventPublisher: EventPublisher;
  
  constructor(eventPublisher: EventPublisher) {
    this.commandId = this.generateCommandId();
    this.timestamp = new Date();
    this.executionState = 'pending';
    this.eventPublisher = eventPublisher;
  }
  
  abstract execute(): Promise<CommandResult>;
  abstract undo(): Promise<CommandResult>;
  abstract getDescription(): string;
  abstract getAffectedResources(): NetworkResource[];
  
  getCommandId(): string {
    return this.commandId;
  }
  
  getTimestamp(): Date {
    return this.timestamp;
  }
  
  canUndo(): boolean {
    return this.executionState === 'executed' && this.originalState !== undefined;
  }
  
  protected async publishCommandEvent(eventType: string, data: any): Promise<void> {
    const event = {
      commandId: this.commandId,
      eventType,
      timestamp: new Date(),
      data: {
        ...data,
        affectedResources: this.getAffectedResources()
      }
    };
    
    await this.eventPublisher.publish(event);
  }
  
  protected generateCommandId(): string {
    return `cmd_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }
  
  protected async captureOriginalState(resource: NetworkResource): Promise<any> {
    // Implementation depends on resource type
    // This would typically call the appropriate service to get current state
    return {};
  }
}

type CommandExecutionState = 'pending' | 'executing' | 'executed' | 'failed' | 'undone';
```

---

## 3. Macro Commands - Komplexe Operationen verstehen

### Problem: Atomare Operationssequenzen

**Szenario**: Ein kompletter Netzwerk-Rollout erfordert:
1. VLAN erstellen
2. Router konfigurieren
3. Firewall-Regeln setzen
4. Interfaces aktivieren

**Was wenn Schritt 3 fehlschlägt?** Alle vorherigen Schritte müssen rückgängig gemacht werden!

### Composite Command Pattern

**Macro Command Struktur:**
```typescript
class MacroCommand implements Command {
  private commands: Command[] = [];
  private executedCommands: Command[] = [];
  
  addCommand(command: Command): void {
    this.commands.push(command);
  }
  
  execute(): void {
    for (const command of this.commands) {
      try {
        command.execute();
        this.executedCommands.push(command);
      } catch (error) {
        // Fehler: Alle ausgeführten Commands rückgängig machen
        this.rollbackExecutedCommands();
        throw error;
      }
    }
  }
  
  undo(): void {
    // Rückgängig in umgekehrter Reihenfolge
    for (let i = this.executedCommands.length - 1; i >= 0; i--) {
      this.executedCommands[i].undo();
    }
    this.executedCommands = [];
  }
  
  private rollbackExecutedCommands(): void {
    // Rollback bei Fehler
    for (let i = this.executedCommands.length - 1; i >= 0; i--) {
      try {
        this.executedCommands[i].undo();
      } catch (rollbackError) {
        // Rollback-Fehler loggen, aber weiter versuchen
        console.error('Rollback failed:', rollbackError);
      }
    }
    this.executedCommands = [];
  }
}

// Usage
const networkRollout = new MacroCommand();
networkRollout.addCommand(new CreateVlanCommand(vlan1));
networkRollout.addCommand(new ConfigureRouterCommand(router1));
networkRollout.addCommand(new SetFirewallRulesCommand(firewall1));
networkRollout.addCommand(new ActivateInterfacesCommand(interfaces));

// Alles oder nichts - transactional execution
networkRollout.execute();
```

**Verhaltens-Aspekte:**
- **Transactional**: Entweder alle Commands oder keiner
- **Rollback-Safety**: Bei Fehler werden erfolgreiche Steps rückgängig gemacht
- **Atomic Operations**: Komplexe Operationen werden atomisch

---

## 4. CQRS mit Command Pattern verstehen

### Command Query Responsibility Segregation

**CQRS Grundprinzip**: **Trennung von Schreib- und Leseoperationen**

**Warum in Netzwerk-Management wichtig?**
- **Commands** (Schreiben): Konfiguration ändern, kritisch, langsam
- **Queries** (Lesen): Status abfragen, häufig, schnell

**CQRS mit Command Pattern:**
```typescript
// Command Side (Write Operations)
interface NetworkCommand {
  execute(): Promise<void>;
  undo(): Promise<void>;
}

// Query Side (Read Operations) 
interface NetworkQuery<T> {
  execute(): Promise<T>;
}

// Commands ändern Zustand
class ConfigureRouterCommand implements NetworkCommand {
  async execute(): Promise<void> {
    // Router konfigurieren
    await this.router.applyConfig(this.config);
    
    // Event für Read-Side publishen
    await this.eventBus.publish(new RouterConfiguredEvent({
      routerId: this.routerId,
      config: this.config
    }));
  }
}

// Queries lesen nur
class GetRouterStatusQuery implements NetworkQuery<RouterStatus> {
  async execute(): Promise<RouterStatus> {
    // Nur lesen, nie ändern
    return await this.statusRepository.getRouterStatus(this.routerId);
  }
}
```

**CQRS Event Flow:**
```
Command Execute → State Change → Event Published → Read Model Updated
```

**Vorteile von CQRS:**
- **Skalierung**: Read/Write getrennt optimierbar
- **Konsistenz**: Commands sind transaktional
- **Auditability**: Alle Änderungen als Commands nachverfolgbar
- **Performance**: Queries können caching nutzen

---

## 5. Undo/Redo-Mechanismen verstehen

### Problem: Wie macht man Netzwerk-Änderungen rückgängig?

**Verschiedene Undo-Strategien:**

1. **Memento-basiert** (Zustand speichern):
```typescript
class VlanCreateCommand implements Command {
  execute(): void {
    // Vlan erstellen
    this.switch.createVlan(this.vlanId, this.config);
  }
  
  undo(): void {
    // Vlan löschen (umgekehrte Operation)
    this.switch.deleteVlan(this.vlanId);
  }
}
```

2. **Kompensation-basiert** (Gegenaktion):
```typescript
class RouteAddCommand implements Command {
  execute(): void {
    this.router.addRoute(this.route);
  }
  
  undo(): void {
    this.router.removeRoute(this.route); // Gegenaktion
  }
}
```

3. **Snapshot-basiert** (Gesamtzustand):
```typescript
class ComplexConfigCommand implements Command {
  private beforeSnapshot: Config;
  
  execute(): void {
    this.beforeSnapshot = this.device.getFullConfig();
    this.device.applyComplexConfig(this.newConfig);
  }
  
  undo(): void {
    this.device.restoreConfig(this.beforeSnapshot);
  }
}
```

### Undo-History Management

**Command History Pattern:**
```typescript
class CommandHistory {
  private history: Command[] = [];
  private currentPosition: number = -1;
  private maxHistorySize: number = 100;
  
  execute(command: Command): void {
    // Neue Commands löschen Redo-Möglichkeiten
    this.history = this.history.slice(0, this.currentPosition + 1);
    
    // Command ausführen und speichern
    command.execute();
    this.history.push(command);
    this.currentPosition++;
    
    // History-Größe begrenzen
    if (this.history.length > this.maxHistorySize) {
      this.history.shift();
      this.currentPosition--;
    }
  }
  
  undo(): boolean {
    if (this.canUndo()) {
      const command = this.history[this.currentPosition];
      command.undo();
      this.currentPosition--;
      return true;
    }
    return false;
  }
  
  redo(): boolean {
    if (this.canRedo()) {
      this.currentPosition++;
      const command = this.history[this.currentPosition];
      command.execute();
      return true;
    }
    return false;
  }
  
  canUndo(): boolean {
    return this.currentPosition >= 0;
  }
  
  canRedo(): boolean {
    return this.currentPosition < this.history.length - 1;
  }
}
```

---

## 6. Testing-Aspekte für Commands verstehen

### Command Testing-Herausforderungen

**Was testen bei Commands?**
1. **Execute Behavior**: Führt Command korrekte Aktion aus?
2. **Undo Behavior**: Stellt Command Original-Zustand wieder her?
3. **State Management**: Wird Zustand korrekt gespeichert?
4. **Error Handling**: Verhalten bei Fehlern korrekt?

**Konzeptueller Test-Ansatz:**
```typescript
// Command Tests konzeptionell
test('Command executes and stores state for undo', () => {
  // Arrange
  const router = new MockRouter();
  const originalConfig = router.getConfig();
  const newConfig = { ...originalConfig, ip: '192.168.1.1' };
  const command = new ConfigureRouterCommand(router, newConfig);
  
  // Act
  command.execute();
  
  // Assert: Neue Konfiguration angewendet
  expect(router.getConfig()).toEqual(newConfig);
  
  // Act: Undo
  command.undo();
  
  // Assert: Original-Konfiguration wiederhergestellt
  expect(router.getConfig()).toEqual(originalConfig);
});
```

### Macro Command Testing

**Test-Szenarien für Macro Commands:**
```typescript
test('Macro command rolls back on failure', () => {
  const macroCommand = new MacroCommand();
  const successCommand = new MockSuccessCommand();
  const failureCommand = new MockFailureCommand(); // Wirft Fehler
  
  macroCommand.addCommand(successCommand);
  macroCommand.addCommand(failureCommand);
  
  // Act & Assert: Fehler wird geworfen
  expect(() => macroCommand.execute()).toThrow();
  
  // Assert: Erfolgreiche Commands wurden rückgängig gemacht
  expect(successCommand.wasUndone()).toBe(true);
});
```
  
  constructor(
    routerId: string, 
    configuration: RouterConfig, 
    routerService: RouterService,
    eventPublisher: EventPublisher
  ) {
    super(eventPublisher);
    this.routerId = routerId;
    this.newConfiguration = configuration;
    this.routerService = routerService;
  }
  
  async execute(): Promise<CommandResult> {
    const startTime = Date.now();
    
    try {
      this.executionState = 'executing';
      await this.publishCommandEvent('CommandStarted', { routerId: this.routerId });
      
      // Aktuelle Konfiguration sichern für Undo
      this.previousConfiguration = await this.routerService.getConfiguration(this.routerId);
      this.originalState = this.previousConfiguration;
      
      // Neue Konfiguration anwenden
      await this.routerService.applyConfiguration(this.routerId, this.newConfiguration);
      
      // Konfiguration validieren
      const isValid = await this.validateConfiguration();
      if (!isValid) {
        throw new Error('Configuration validation failed');
      }
      
      this.executionState = 'executed';
      
      const result: CommandResult = {
        success: true,
        message: `Router ${this.routerId} configured successfully`,
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources(),
        data: {
          routerId: this.routerId,
          appliedConfiguration: this.newConfiguration
        }
      };
      
      await this.publishCommandEvent('CommandCompleted', result);
      return result;
      
    } catch (error) {
      this.executionState = 'failed';
      
      const result: CommandResult = {
        success: false,
        message: `Failed to configure router ${this.routerId}`,
        errors: [error.message],
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
      
      await this.publishCommandEvent('CommandFailed', result);
      return result;
    }
  }
  
  async undo(): Promise<CommandResult> {
    if (!this.canUndo() || !this.previousConfiguration) {
      return {
        success: false,
        message: 'Cannot undo: No previous state available',
        executionTime: 0,
        affectedResources: this.getAffectedResources()
      };
    }
    
    const startTime = Date.now();
    
    try {
      await this.publishCommandEvent('UndoStarted', { routerId: this.routerId });
      
      // Vorherige Konfiguration wiederherstellen
      await this.routerService.applyConfiguration(this.routerId, this.previousConfiguration);
      
      this.executionState = 'undone';
      
      const result: CommandResult = {
        success: true,
        message: `Router ${this.routerId} configuration reverted successfully`,
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
      
      await this.publishCommandEvent('UndoCompleted', result);
      return result;
      
    } catch (error) {
      const result: CommandResult = {
        success: false,
        message: `Failed to undo router ${this.routerId} configuration`,
        errors: [error.message],
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
      
      await this.publishCommandEvent('UndoFailed', result);
      return result;
    }
  }
  
  getDescription(): string {
    return `Configure Router ${this.routerId} with ${Object.keys(this.newConfiguration).length} settings`;
  }
  
  getAffectedResources(): NetworkResource[] {
    return [{
      id: this.routerId,
      type: 'router',
      name: `Router-${this.routerId}`
    }];
  }
  
  private async validateConfiguration(): Promise<boolean> {
    // Router-spezifische Validierungslogik
    const currentConfig = await this.routerService.getConfiguration(this.routerId);
    
    // Beispiel-Validierungen
    if (this.newConfiguration.interfaces) {
      for (const iface of this.newConfiguration.interfaces) {
        if (!await this.routerService.isInterfaceAvailable(this.routerId, iface.name)) {
          return false;
        }
      }
    }
    
    return true;
  }
}
```

```typescript
// VLAN Creation Command
class CreateVlanCommand extends BaseNetworkCommand {
  private switchId: string;
  private vlanConfig: VlanConfig;
  private switchService: SwitchService;
  private createdVlanId?: string;
  
  constructor(
    switchId: string,
    vlanConfig: VlanConfig,
    switchService: SwitchService,
    eventPublisher: EventPublisher
  ) {
    super(eventPublisher);
    this.switchId = switchId;
    this.vlanConfig = vlanConfig;
    this.switchService = switchService;
  }
  
  async execute(): Promise<CommandResult> {
    const startTime = Date.now();
    
    try {
      this.executionState = 'executing';
      
      // VLAN erstellen
      const vlanResult = await this.switchService.createVlan(this.switchId, this.vlanConfig);
      this.createdVlanId = vlanResult.vlanId;
      this.originalState = { created: true, vlanId: this.createdVlanId };
      
      // Ports zuweisen falls konfiguriert
      if (this.vlanConfig.assignedPorts && this.vlanConfig.assignedPorts.length > 0) {
        await this.assignPortsToVlan();
      }
      
      this.executionState = 'executed';
      
      const result: CommandResult = {
        success: true,
        message: `VLAN ${this.vlanConfig.name} created successfully on switch ${this.switchId}`,
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources(),
        data: {
          vlanId: this.createdVlanId,
          vlanConfig: this.vlanConfig
        }
      };
      
      await this.publishCommandEvent('VlanCreated', result);
      return result;
      
    } catch (error) {
      this.executionState = 'failed';
      
      const result: CommandResult = {
        success: false,
        message: `Failed to create VLAN on switch ${this.switchId}`,
        errors: [error.message],
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
      
      return result;
    }
  }
  
  async undo(): Promise<CommandResult> {
    if (!this.canUndo() || !this.createdVlanId) {
      return {
        success: false,
        message: 'Cannot undo: VLAN was not successfully created',
        executionTime: 0,
        affectedResources: this.getAffectedResources()
      };
    }
    
    const startTime = Date.now();
    
    try {
      // VLAN löschen
      await this.switchService.deleteVlan(this.switchId, this.createdVlanId);
      
      this.executionState = 'undone';
      
      const result: CommandResult = {
        success: true,
        message: `VLAN ${this.createdVlanId} removed successfully from switch ${this.switchId}`,
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
      
      await this.publishCommandEvent('VlanDeleted', result);
      return result;
      
    } catch (error) {
      return {
        success: false,
        message: `Failed to remove VLAN ${this.createdVlanId}`,
        errors: [error.message],
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
    }
  }
  
  getDescription(): string {
    return `Create VLAN '${this.vlanConfig.name}' on Switch ${this.switchId}`;
  }
  
  getAffectedResources(): NetworkResource[] {
    const resources: NetworkResource[] = [{
      id: this.switchId,
      type: 'switch',
      name: `Switch-${this.switchId}`
    }];
    
    if (this.createdVlanId) {
      resources.push({
        id: this.createdVlanId,
        type: 'vlan',
        name: this.vlanConfig.name
      });
    }
    
    return resources;
  }
  
  private async assignPortsToVlan(): Promise<void> {
    if (!this.vlanConfig.assignedPorts || !this.createdVlanId) return;
    
    for (const portId of this.vlanConfig.assignedPorts) {
      await this.switchService.assignPortToVlan(
        this.switchId, 
        portId, 
        this.createdVlanId
      );
    }
  }
}
```

### Firewall Rules Command

```typescript
// Firewall Rules Update Command
class UpdateFirewallRulesCommand extends BaseNetworkCommand {
  private firewallId: string;
  private newRules: FirewallRule[];
  private firewallService: FirewallService;
  private previousRules?: FirewallRule[];
  private ruleValidationService: RuleValidationService;
  
  constructor(
    firewallId: string,
    rules: FirewallRule[],
    firewallService: FirewallService,
    ruleValidationService: RuleValidationService,
    eventPublisher: EventPublisher
  ) {
    super(eventPublisher);
    this.firewallId = firewallId;
    this.newRules = rules;
    this.firewallService = firewallService;
    this.ruleValidationService = ruleValidationService;
  }
  
  async execute(): Promise<CommandResult> {
    const startTime = Date.now();
    
    try {
      this.executionState = 'executing';
      
      // Vorherige Regeln sichern
      this.previousRules = await this.firewallService.getRules(this.firewallId);
      this.originalState = this.previousRules;
      
      // Regelvalidierung vor Anwendung
      const validationResult = await this.validateRules();
      if (!validationResult.isValid) {
        throw new Error(`Rule validation failed: ${validationResult.errors.join(', ')}`);
      }
      
      // Backup der aktuellen Konfiguration für Rollback
      await this.createConfigurationBackup();
      
      // Neue Regeln anwenden
      await this.firewallService.updateRules(this.firewallId, this.newRules);
      
      // Connectivity Tests nach Regel-Update
      const connectivityTest = await this.performConnectivityTests();
      if (!connectivityTest.success) {
        await this.rollbackToBackup();
        throw new Error(`Connectivity test failed: ${connectivityTest.message}`);
      }
      
      this.executionState = 'executed';
      
      const result: CommandResult = {
        success: true,
        message: `Firewall rules updated successfully on ${this.firewallId}`,
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources(),
        data: {
          rulesAdded: this.newRules.length,
          rulesRemoved: this.previousRules ? this.previousRules.length : 0,
          connectivityTestResults: connectivityTest
        }
      };
      
      await this.publishCommandEvent('FirewallRulesUpdated', result);
      return result;
      
    } catch (error) {
      this.executionState = 'failed';
      
      const result: CommandResult = {
        success: false,
        message: `Failed to update firewall rules on ${this.firewallId}`,
        errors: [error.message],
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
      
      return result;
    }
  }
  
  async undo(): Promise<CommandResult> {
    if (!this.canUndo() || !this.previousRules) {
      return {
        success: false,
        message: 'Cannot undo: No previous rules available',
        executionTime: 0,
        affectedResources: this.getAffectedResources()
      };
    }
    
    const startTime = Date.now();
    
    try {
      // Vorherige Regeln wiederherstellen
      await this.firewallService.updateRules(this.firewallId, this.previousRules);
      
      // Connectivity nach Rollback prüfen
      const connectivityTest = await this.performConnectivityTests();
      if (!connectivityTest.success) {
        console.warn('Connectivity test failed after rollback, but continuing...');
      }
      
      this.executionState = 'undone';
      
      const result: CommandResult = {
        success: true,
        message: `Firewall rules reverted successfully on ${this.firewallId}`,
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
      
      await this.publishCommandEvent('FirewallRulesReverted', result);
      return result;
      
    } catch (error) {
      return {
        success: false,
        message: `Failed to revert firewall rules on ${this.firewallId}`,
        errors: [error.message],
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
    }
  }
  
  getDescription(): string {
    return `Update Firewall Rules on ${this.firewallId} (${this.newRules.length} rules)`;
  }
  
  getAffectedResources(): NetworkResource[] {
    return [{
      id: this.firewallId,
      type: 'firewall',
      name: `Firewall-${this.firewallId}`
    }];
  }
  
  private async validateRules(): Promise<ValidationResult> {
    return await this.ruleValidationService.validateRuleSet(this.newRules);
  }
  
  private async createConfigurationBackup(): Promise<void> {
    await this.firewallService.createBackup(this.firewallId, `backup_${this.commandId}`);
  }
  
  private async performConnectivityTests(): Promise<ConnectivityTestResult> {
    // Kritische Verbindungen testen
    const criticalEndpoints = await this.getCriticalEndpoints();
    
    for (const endpoint of criticalEndpoints) {
      const testResult = await this.firewallService.testConnectivity(
        this.firewallId, 
        endpoint
      );
      
      if (!testResult.success) {
        return {
          success: false,
          message: `Connectivity test failed for ${endpoint.name}`,
          failedEndpoint: endpoint
        };
      }
    }
    
    return {
      success: true,
      message: 'All connectivity tests passed',
      testedEndpoints: criticalEndpoints.length
    };
  }
}
```

---

## 3. Macro Commands für komplexe Operationen

### Composite Command Implementation

```typescript
// Macro Command für komplexe Operationssequenzen
class MacroNetworkCommand extends BaseNetworkCommand {
  private commands: NetworkCommand[];
  private executedCommands: NetworkCommand[] = [];
  private name: string;
  
  constructor(name: string, commands: NetworkCommand[], eventPublisher: EventPublisher) {
    super(eventPublisher);
    this.name = name;
    this.commands = commands;
  }
  
  async execute(): Promise<CommandResult> {
    const startTime = Date.now();
    const results: CommandResult[] = [];
    const errors: string[] = [];
    
    try {
      this.executionState = 'executing';
      await this.publishCommandEvent('MacroCommandStarted', {
        name: this.name,
        commandCount: this.commands.length
      });
      
      for (let i = 0; i < this.commands.length; i++) {
        const command = this.commands[i];
        
        await this.publishCommandEvent('SubCommandStarted', {
          commandIndex: i + 1,
          totalCommands: this.commands.length,
          commandDescription: command.getDescription()
        });
        
        const result = await command.execute();
        results.push(result);
        
        if (result.success) {
          this.executedCommands.push(command);
          await this.publishCommandEvent('SubCommandCompleted', {
            commandIndex: i + 1,
            result
          });
        } else {
          errors.push(`Command ${i + 1}: ${result.message}`);
          
          // Bei Fehler: Rollback aller erfolgreich ausgeführten Commands
          await this.rollbackExecutedCommands();
          
          throw new Error(`Macro command failed at step ${i + 1}: ${result.message}`);
        }
      }
      
      this.executionState = 'executed';
      
      const macroResult: CommandResult = {
        success: true,
        message: `Macro command '${this.name}' completed successfully`,
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources(),
        data: {
          commandsExecuted: this.executedCommands.length,
          subResults: results
        }
      };
      
      await this.publishCommandEvent('MacroCommandCompleted', macroResult);
      return macroResult;
      
    } catch (error) {
      this.executionState = 'failed';
      
      const macroResult: CommandResult = {
        success: false,
        message: `Macro command '${this.name}' failed`,
        errors: [error.message, ...errors],
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources(),
        data: {
          failedAtStep: this.executedCommands.length + 1,
          partialResults: results
        }
      };
      
      await this.publishCommandEvent('MacroCommandFailed', macroResult);
      return macroResult;
    }
  }
  
  async undo(): Promise<CommandResult> {
    if (!this.canUndo()) {
      return {
        success: false,
        message: 'Cannot undo: Macro command was not successfully executed',
        executionTime: 0,
        affectedResources: this.getAffectedResources()
      };
    }
    
    const startTime = Date.now();
    
    try {
      await this.rollbackExecutedCommands();
      
      this.executionState = 'undone';
      
      const result: CommandResult = {
        success: true,
        message: `Macro command '${this.name}' rolled back successfully`,
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
      
      await this.publishCommandEvent('MacroCommandRolledBack', result);
      return result;
      
    } catch (error) {
      return {
        success: false,
        message: `Failed to rollback macro command '${this.name}'`,
        errors: [error.message],
        executionTime: Date.now() - startTime,
        affectedResources: this.getAffectedResources()
      };
    }
  }
  
  private async rollbackExecutedCommands(): Promise<void> {
    // Rückwärts durch die erfolgreich ausgeführten Commands
    for (let i = this.executedCommands.length - 1; i >= 0; i--) {
      const command = this.executedCommands[i];
      
      if (command.canUndo()) {
        try {
          await command.undo();
          await this.publishCommandEvent('SubCommandRolledBack', {
            commandIndex: i + 1,
            commandDescription: command.getDescription()
          });
        } catch (error) {
          console.error(`Failed to rollback command ${i + 1}:`, error);
          // Continue with rollback of other commands
        }
      }
    }
    
    this.executedCommands = [];
  }
  
  getDescription(): string {
    return `Macro: ${this.name} (${this.commands.length} operations)`;
  }
  
  getAffectedResources(): NetworkResource[] {
    const allResources: NetworkResource[] = [];
    
    for (const command of this.commands) {
      allResources.push(...command.getAffectedResources());
    }
    
    // Deduplizierung basierend auf ID
    const uniqueResources = allResources.reduce((unique, resource) => {
      if (!unique.find(r => r.id === resource.id && r.type === resource.type)) {
        unique.push(resource);
      }
      return unique;
    }, [] as NetworkResource[]);
    
    return uniqueResources;
  }
  
  canUndo(): boolean {
    return this.executionState === 'executed' && this.executedCommands.length > 0;
  }
}
```

### Practical Macro Examples

```typescript
// Factory für häufige Makro-Operationen
class NetworkMacroFactory {
  private eventPublisher: EventPublisher;
  private routerService: RouterService;
  private switchService: SwitchService;
  private firewallService: FirewallService;
  
  constructor(
    eventPublisher: EventPublisher,
    routerService: RouterService,
    switchService: SwitchService,
    firewallService: FirewallService
  ) {
    this.eventPublisher = eventPublisher;
    this.routerService = routerService;
    this.switchService = switchService;
    this.firewallService = firewallService;
  }
  
  // Neuen Standort einrichten
  createSiteSetupMacro(
    routerId: string,
    switchId: string,
    firewallId: string,
    siteConfig: SiteConfiguration
  ): MacroNetworkCommand {
    const commands: NetworkCommand[] = [
      // 1. Router konfigurieren
      new ConfigureRouterCommand(
        routerId,
        siteConfig.routerConfig,
        this.routerService,
        this.eventPublisher
      ),
      
      // 2. Management VLANs erstellen
      ...siteConfig.managementVlans.map(vlan => 
        new CreateVlanCommand(
          switchId,
          vlan,
          this.switchService,
          this.eventPublisher
        )
      ),
      
      // 3. Firewall-Basisregeln einrichten
      new UpdateFirewallRulesCommand(
        firewallId,
        siteConfig.baseFirewallRules,
        this.firewallService,
        new RuleValidationService(),
        this.eventPublisher
      )
    ];
    
    return new MacroNetworkCommand(
      `Site Setup: ${siteConfig.siteName}`,
      commands,
      this.eventPublisher
    );
  }
  
  // Wartungsmodus aktivieren
  createMaintenanceModeMacro(
    deviceIds: string[],
    maintenanceConfig: MaintenanceConfiguration
  ): MacroNetworkCommand {
    const commands: NetworkCommand[] = [];
    
    // Traffic umleiten
    for (const deviceId of deviceIds) {
      commands.push(new TrafficRedirectionCommand(
        deviceId,
        maintenanceConfig.redirectionRules,
        this.routerService,
        this.eventPublisher
      ));
    }
    
    // Monitoring anpassen
    commands.push(new UpdateMonitoringCommand(
      deviceIds,
      maintenanceConfig.monitoringSettings,
      this.eventPublisher
    ));
    
    return new MacroNetworkCommand(
      `Enable Maintenance Mode`,
      commands,
      this.eventPublisher
    );
  }
}
```

---

## 4. Command Invoker und Queue Management

### Command Executor mit Queue Support

```typescript
class NetworkCommandExecutor {
  private commandHistory: NetworkCommand[] = [];
  private commandQueue: QueuedCommand[] = [];
  private isProcessingQueue = false;
  private maxHistorySize = 1000;
  private eventPublisher: EventPublisher;
  private executionPolicy: ExecutionPolicy;
  
  constructor(eventPublisher: EventPublisher, executionPolicy: ExecutionPolicy) {
    this.eventPublisher = eventPublisher;
    this.executionPolicy = executionPolicy;
  }
  
  // Sofortige Ausführung
  async executeCommand(command: NetworkCommand): Promise<CommandResult> {
    // Pre-execution Policy Check
    const policyCheck = await this.executionPolicy.canExecute(command);
    if (!policyCheck.allowed) {
      return {
        success: false,
        message: `Execution denied: ${policyCheck.reason}`,
        executionTime: 0,
        affectedResources: command.getAffectedResources()
      };
    }
    
    const result = await command.execute();
    
    if (result.success) {
      this.addToHistory(command);
    }
    
    await this.publishExecutionEvent('CommandExecuted', {
      commandId: command.getCommandId(),
      result
    });
    
    return result;
  }
  
  // Command in Queue einreihen
  async queueCommand(
    command: NetworkCommand, 
    scheduledTime?: Date, 
    priority: CommandPriority = 'normal'
  ): Promise<string> {
    const queueId = this.generateQueueId();
    
    const queuedCommand: QueuedCommand = {
      queueId,
      command,
      scheduledTime: scheduledTime || new Date(),
      priority,
      status: 'queued',
      queuedAt: new Date()
    };
    
    this.commandQueue.push(queuedCommand);
    this.sortQueue();
    
    await this.publishExecutionEvent('CommandQueued', {
      queueId,
      commandId: command.getCommandId(),
      scheduledTime
    });
    
    // Queue processing starten falls nicht aktiv
    if (!this.isProcessingQueue) {
      this.processQueue();
    }
    
    return queueId;
  }
  
  // Undo letzten Command
  async undoLastCommand(): Promise<CommandResult> {
    const lastCommand = this.getLastExecutableCommand();
    
    if (!lastCommand || !lastCommand.canUndo()) {
      return {
        success: false,
        message: 'No undoable command found',
        executionTime: 0,
        affectedResources: []
      };
    }
    
    const result = await lastCommand.undo();
    
    await this.publishExecutionEvent('CommandUndone', {
      commandId: lastCommand.getCommandId(),
      result
    });
    
    return result;
  }
  
  // Undo spezifischen Command by ID
  async undoCommand(commandId: string): Promise<CommandResult> {
    const command = this.commandHistory.find(cmd => cmd.getCommandId() === commandId);
    
    if (!command) {
      return {
        success: false,
        message: `Command with ID ${commandId} not found in history`,
        executionTime: 0,
        affectedResources: []
      };
    }
    
    if (!command.canUndo()) {
      return {
        success: false,
        message: `Command ${commandId} cannot be undone`,
        executionTime: 0,
        affectedResources: command.getAffectedResources()
      };
    }
    
    return await command.undo();
  }
  
  // Queue Processing
  private async processQueue(): Promise<void> {
    this.isProcessingQueue = true;
    
    while (this.commandQueue.length > 0) {
      const queuedCommand = this.getNextQueuedCommand();
      
      if (!queuedCommand) {
        break; // Kein Command bereit für Ausführung
      }
      
      if (queuedCommand.scheduledTime > new Date()) {
        // Warten bis zur geplanten Zeit
        await this.sleep(queuedCommand.scheduledTime.getTime() - Date.now());
      }
      
      queuedCommand.status = 'executing';
      
      try {
        const result = await this.executeCommand(queuedCommand.command);
        queuedCommand.status = result.success ? 'completed' : 'failed';
        queuedCommand.result = result;
        
      } catch (error) {
        queuedCommand.status = 'failed';
        queuedCommand.error = error.message;
      }
      
      // Command aus Queue entfernen
      this.removeFromQueue(queuedCommand.queueId);
      
      await this.publishExecutionEvent('QueuedCommandProcessed', {
        queueId: queuedCommand.queueId,
        status: queuedCommand.status
      });
    }
    
    this.isProcessingQueue = false;
  }
  
  // Command History Management
  private addToHistory(command: NetworkCommand): void {
    this.commandHistory.push(command);
    
    // History-Größe begrenzen
    if (this.commandHistory.length > this.maxHistorySize) {
      this.commandHistory.shift();
    }
  }
  
  private getLastExecutableCommand(): NetworkCommand | undefined {
    for (let i = this.commandHistory.length - 1; i >= 0; i--) {
      const command = this.commandHistory[i];
      if (command.canUndo()) {
        return command;
      }
    }
    return undefined;
  }
  
  // Queue Management
  private sortQueue(): void {
    this.commandQueue.sort((a, b) => {
      // Priorität: high > normal > low
      const priorityWeight = { high: 3, normal: 2, low: 1 };
      const priorityDiff = priorityWeight[a.priority] - priorityWeight[b.priority];
      
      if (priorityDiff !== 0) {
        return -priorityDiff; // Hohe Priorität zuerst
      }
      
      // Bei gleicher Priorität: frühere Scheduled Time zuerst
      return a.scheduledTime.getTime() - b.scheduledTime.getTime();
    });
  }
  
  private getNextQueuedCommand(): QueuedCommand | undefined {
    return this.commandQueue.find(cmd => 
      cmd.status === 'queued' && cmd.scheduledTime <= new Date()
    );
  }
  
  private removeFromQueue(queueId: string): void {
    const index = this.commandQueue.findIndex(cmd => cmd.queueId === queueId);
    if (index !== -1) {
      this.commandQueue.splice(index, 1);
    }
  }
  
  // Utility Methods
  private generateQueueId(): string {
    return `queue_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }
  
  private sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
  
  private async publishExecutionEvent(eventType: string, data: any): Promise<void> {
    await this.eventPublisher.publish({
      type: eventType,
      timestamp: new Date(),
      data
    });
  }
  
  // Public Query Methods
  getCommandHistory(): NetworkCommand[] {
    return [...this.commandHistory];
  }
  
  getQueueStatus(): QueueStatus {
    return {
      queueLength: this.commandQueue.length,
      isProcessing: this.isProcessingQueue,
      nextScheduledCommand: this.getNextQueuedCommand()?.scheduledTime
    };
  }
  
  cancelQueuedCommand(queueId: string): boolean {
    const command = this.commandQueue.find(cmd => cmd.queueId === queueId);
    
    if (command && command.status === 'queued') {
      command.status = 'cancelled';
      this.removeFromQueue(queueId);
      return true;
    }
    
    return false;
  }
}

// Supporting Interfaces
interface QueuedCommand {
  queueId: string;
  command: NetworkCommand;
  scheduledTime: Date;
  priority: CommandPriority;
  status: QueueCommandStatus;
  queuedAt: Date;
  result?: CommandResult;
  error?: string;
}

type CommandPriority = 'high' | 'normal' | 'low';
type QueueCommandStatus = 'queued' | 'executing' | 'completed' | 'failed' | 'cancelled';

interface QueueStatus {
  queueLength: number;
  isProcessing: boolean;
  nextScheduledCommand?: Date;
}
```

---

## 5. CQRS Integration

### Command Bus und Query Handler

```typescript
// Command Bus für CQRS Pattern
class NetworkCommandBus {
  private commandHandlers: Map<string, CommandHandler> = new Map();
  private commandExecutor: NetworkCommandExecutor;
  private eventPublisher: EventPublisher;
  
  constructor(commandExecutor: NetworkCommandExecutor, eventPublisher: EventPublisher) {
    this.commandExecutor = commandExecutor;
    this.eventPublisher = eventPublisher;
  }
  
  registerHandler<T extends NetworkCommandRequest>(commandType: string, handler: CommandHandler<T>): void {
    this.commandHandlers.set(commandType, handler);
  }
  
  async execute<T extends NetworkCommandRequest>(commandRequest: T): Promise<CommandResult> {
    const handler = this.commandHandlers.get(commandRequest.type);
    
    if (!handler) {
      throw new Error(`No handler registered for command type: ${commandRequest.type}`);
    }
    
    // Command aus Request erstellen
    const command = await handler.handle(commandRequest);
    
    // Command ausführen
    return await this.commandExecutor.executeCommand(command);
  }
  
  async schedule<T extends NetworkCommandRequest>(
    commandRequest: T, 
    scheduledTime: Date, 
    priority: CommandPriority = 'normal'
  ): Promise<string> {
    const handler = this.commandHandlers.get(commandRequest.type);
    
    if (!handler) {
      throw new Error(`No handler registered for command type: ${commandRequest.type}`);
    }
    
    const command = await handler.handle(commandRequest);
    return await this.commandExecutor.queueCommand(command, scheduledTime, priority);
  }
}

// Command Handler Interface
interface CommandHandler<T extends NetworkCommandRequest = NetworkCommandRequest> {
  handle(request: T): Promise<NetworkCommand>;
}

// Command Request Types (CQRS Commands)
interface NetworkCommandRequest {
  type: string;
  requestId: string;
  userId: string;
  timestamp: Date;
}

interface ConfigureRouterRequest extends NetworkCommandRequest {
  type: 'ConfigureRouter';
  routerId: string;
  configuration: RouterConfig;
  validateConnectivity: boolean;
}

interface CreateVlanRequest extends NetworkCommandRequest {
  type: 'CreateVlan';
  switchId: string;
  vlanConfig: VlanConfig;
  assignPorts: boolean;
}

interface UpdateFirewallRulesRequest extends NetworkCommandRequest {
  type: 'UpdateFirewallRules';
  firewallId: string;
  rules: FirewallRule[];
  performConnectivityTests: boolean;
}

// Command Handler Implementations
class ConfigureRouterHandler implements CommandHandler<ConfigureRouterRequest> {
  private routerService: RouterService;
  private eventPublisher: EventPublisher;
  
  constructor(routerService: RouterService, eventPublisher: EventPublisher) {
    this.routerService = routerService;
    this.eventPublisher = eventPublisher;
  }
  
  async handle(request: ConfigureRouterRequest): Promise<NetworkCommand> {
    // Validation
    if (!request.routerId || !request.configuration) {
      throw new Error('Router ID and configuration are required');
    }
    
    // Command erstellen
    return new ConfigureRouterCommand(
      request.routerId,
      request.configuration,
      this.routerService,
      this.eventPublisher
    );
  }
}

class CreateVlanHandler implements CommandHandler<CreateVlanRequest> {
  private switchService: SwitchService;
  private eventPublisher: EventPublisher;
  
  constructor(switchService: SwitchService, eventPublisher: EventPublisher) {
    this.switchService = switchService;
    this.eventPublisher = eventPublisher;
  }
  
  async handle(request: CreateVlanRequest): Promise<NetworkCommand> {
    return new CreateVlanCommand(
      request.switchId,
      request.vlanConfig,
      this.switchService,
      this.eventPublisher
    );
  }
}
```

### Query Side (CQRS)

```typescript
// Query Handler für Read-Operations
class NetworkQueryHandler {
  private commandExecutor: NetworkCommandExecutor;
  private networkStateRepository: NetworkStateRepository;
  
  constructor(
    commandExecutor: NetworkCommandExecutor,
    networkStateRepository: NetworkStateRepository
  ) {
    this.commandExecutor = commandExecutor;
    this.networkStateRepository = networkStateRepository;
  }
  
  // Command History Queries
  async getCommandHistory(deviceId?: string): Promise<CommandHistoryDto[]> {
    const history = this.commandExecutor.getCommandHistory();
    
    let filteredHistory = history;
    if (deviceId) {
      filteredHistory = history.filter(cmd => 
        cmd.getAffectedResources().some(resource => resource.id === deviceId)
      );
    }
    
    return filteredHistory.map(cmd => ({
      commandId: cmd.getCommandId(),
      description: cmd.getDescription(),
      timestamp: cmd.getTimestamp(),
      canUndo: cmd.canUndo(),
      affectedResources: cmd.getAffectedResources()
    }));
  }
  
  // Queue Status Queries
  async getQueueStatus(): Promise<QueueStatusDto> {
    const status = this.commandExecutor.getQueueStatus();
    
    return {
      queueLength: status.queueLength,
      isProcessing: status.isProcessing,
      nextScheduledCommand: status.nextScheduledCommand,
      estimatedProcessingTime: this.estimateProcessingTime(status.queueLength)
    };
  }
  
  // Device Configuration History
  async getDeviceConfigurationHistory(deviceId: string): Promise<ConfigurationHistoryDto[]> {
    return await this.networkStateRepository.getConfigurationHistory(deviceId);
  }
  
  // Network Topology Impact Analysis
  async analyzeCommandImpact(commandId: string): Promise<ImpactAnalysisDto> {
    const command = this.commandExecutor.getCommandHistory()
      .find(cmd => cmd.getCommandId() === commandId);
    
    if (!command) {
      throw new Error(`Command ${commandId} not found`);
    }
    
    const affectedResources = command.getAffectedResources();
    const dependentResources = await this.findDependentResources(affectedResources);
    
    return {
      commandId,
      directlyAffected: affectedResources,
      indirectlyAffected: dependentResources,
      riskLevel: this.calculateRiskLevel(affectedResources, dependentResources)
    };
  }
  
  private estimateProcessingTime(queueLength: number): number {
    // Durchschnittliche Ausführungszeit pro Command (in Sekunden)
    const avgExecutionTime = 30;
    return queueLength * avgExecutionTime;
  }
  
  private async findDependentResources(resources: NetworkResource[]): Promise<NetworkResource[]> {
    const dependents: NetworkResource[] = [];
    
    for (const resource of resources) {
      const deps = await this.networkStateRepository.findDependentResources(resource.id);
      dependents.push(...deps);
    }
    
    return dependents;
  }
  
  private calculateRiskLevel(direct: NetworkResource[], indirect: NetworkResource[]): 'low' | 'medium' | 'high' {
    const totalAffected = direct.length + indirect.length;
    
    if (totalAffected > 10) return 'high';
    if (totalAffected > 5) return 'medium';
    return 'low';
  }
}

// DTOs für Query Results
interface CommandHistoryDto {
  commandId: string;
  description: string;
  timestamp: Date;
  canUndo: boolean;
  affectedResources: NetworkResource[];
}

interface QueueStatusDto {
  queueLength: number;
  isProcessing: boolean;
  nextScheduledCommand?: Date;
  estimatedProcessingTime: number; // in seconds
}

interface ImpactAnalysisDto {
  commandId: string;
  directlyAffected: NetworkResource[];
  indirectlyAffected: NetworkResource[];
  riskLevel: 'low' | 'medium' | 'high';
}
```

---

## 6. Testing-Strategien

### Unit Tests für Commands

```typescript
describe('ConfigureRouterCommand', () => {
  let command: ConfigureRouterCommand;
  let mockRouterService: jest.Mocked<RouterService>;
  let mockEventPublisher: jest.Mocked<EventPublisher>;
  let routerConfig: RouterConfig;
  
  beforeEach(() => {
    mockRouterService = {
      getConfiguration: jest.fn(),
      applyConfiguration: jest.fn(),
      isInterfaceAvailable: jest.fn()
    };
    
    mockEventPublisher = {
      publish: jest.fn().mockResolvedValue(undefined)
    };
    
    routerConfig = {
      interfaces: [
        { name: 'eth0', ip: '192.168.1.1' },
        { name: 'eth1', ip: '10.0.0.1' }
      ],
      routingProtocols: ['OSPF']
    };
    
    command = new ConfigureRouterCommand(
      'router-123',
      routerConfig,
      mockRouterService,
      mockEventPublisher
    );
  });
  
  describe('execute', () => {
    it('should successfully configure router and allow undo', async () => {
      // Arrange
      const previousConfig = { interfaces: [], routingProtocols: [] };
      mockRouterService.getConfiguration.mockResolvedValue(previousConfig);
      mockRouterService.applyConfiguration.mockResolvedValue(undefined);
      mockRouterService.isInterfaceAvailable.mockResolvedValue(true);
      
      // Act
      const result = await command.execute();
      
      // Assert
      expect(result.success).toBe(true);
      expect(result.message).toContain('configured successfully');
      expect(command.canUndo()).toBe(true);
      
      expect(mockRouterService.getConfiguration).toHaveBeenCalledWith('router-123');
      expect(mockRouterService.applyConfiguration).toHaveBeenCalledWith('router-123', routerConfig);
      expect(mockEventPublisher.publish).toHaveBeenCalledWith(
        expect.objectContaining({ eventType: 'CommandCompleted' })
      );
    });
    
    it('should handle configuration failure and prevent undo', async () => {
      // Arrange
      const error = new Error('Network interface not available');
      mockRouterService.getConfiguration.mockResolvedValue({});
      mockRouterService.applyConfiguration.mockRejectedValue(error);
      
      // Act
      const result = await command.execute();
      
      // Assert
      expect(result.success).toBe(false);
      expect(result.errors).toContain(error.message);
      expect(command.canUndo()).toBe(false);
    });
  });
  
  describe('undo', () => {
    it('should restore previous configuration', async () => {
      // Arrange
      const previousConfig = { interfaces: [], routingProtocols: ['BGP'] };
      mockRouterService.getConfiguration.mockResolvedValue(previousConfig);
      mockRouterService.applyConfiguration.mockResolvedValue(undefined);
      mockRouterService.isInterfaceAvailable.mockResolvedValue(true);
      
      // Execute first
      await command.execute();
      
      // Act
      const undoResult = await command.undo();
      
      // Assert
      expect(undoResult.success).toBe(true);
      expect(mockRouterService.applyConfiguration).toHaveBeenLastCalledWith(
        'router-123', 
        previousConfig
      );
    });
  });
});
```

### Integration Tests für Macro Commands

```typescript
describe('MacroNetworkCommand Integration', () => {
  let macroCommand: MacroNetworkCommand;
  let mockCommands: jest.Mocked<NetworkCommand>[];
  let mockEventPublisher: jest.Mocked<EventPublisher>;
  
  beforeEach(() => {
    mockEventPublisher = {
      publish: jest.fn().mockResolvedValue(undefined)
    };
    
    mockCommands = [
      {
        execute: jest.fn().mockResolvedValue({ success: true, message: 'Command 1 success' }),
        undo: jest.fn().mockResolvedValue({ success: true, message: 'Command 1 undone' }),
        canUndo: jest.fn().mockReturnValue(true),
        getDescription: jest.fn().mockReturnValue('Test Command 1'),
        getCommandId: jest.fn().mockReturnValue('cmd-1'),
        getTimestamp: jest.fn().mockReturnValue(new Date()),
        getAffectedResources: jest.fn().mockReturnValue([{ id: 'res-1', type: 'router', name: 'Router 1' }])
      },
      {
        execute: jest.fn().mockResolvedValue({ success: true, message: 'Command 2 success' }),
        undo: jest.fn().mockResolvedValue({ success: true, message: 'Command 2 undone' }),
        canUndo: jest.fn().mockReturnValue(true),
        getDescription: jest.fn().mockReturnValue('Test Command 2'),
        getCommandId: jest.fn().mockReturnValue('cmd-2'),
        getTimestamp: jest.fn().mockReturnValue(new Date()),
        getAffectedResources: jest.fn().mockReturnValue([{ id: 'res-2', type: 'switch', name: 'Switch 1' }])
      }
    ];
    
    macroCommand = new MacroNetworkCommand(
      'Test Macro',
      mockCommands,
      mockEventPublisher
    );
  });
  
  it('should execute all commands in sequence', async () => {
    // Act
    const result = await macroCommand.execute();
    
    // Assert
    expect(result.success).toBe(true);
    expect(mockCommands[0].execute).toHaveBeenCalled();
    expect(mockCommands[1].execute).toHaveBeenCalled();
    expect(macroCommand.canUndo()).toBe(true);
  });
  
  it('should rollback all executed commands on failure', async () => {
    // Arrange
    mockCommands[1].execute.mockResolvedValue({
      success: false,
      message: 'Command 2 failed',
      executionTime: 100,
      affectedResources: []
    });
    
    // Act
    const result = await macroCommand.execute();
    
    // Assert
    expect(result.success).toBe(false);
    expect(mockCommands[0].execute).toHaveBeenCalled();
    expect(mockCommands[1].execute).toHaveBeenCalled();
    
    // First command should be rolled back
    expect(mockCommands[0].undo).toHaveBeenCalled();
    expect(mockCommands[1].undo).not.toHaveBeenCalled(); // Failed command doesn't need undo
  });
});
```

---

## 7. Pattern-Entscheidungen verstehen

### Wann Command Pattern verwenden?

**Indikatoren für Command Pattern:**
- **Undo/Redo benötigt**: Kritische Operationen müssen rückgängig machbar sein
- **Operationen queuen**: Commands sollen gespeichert und später ausgeführt werden
- **Audit Trail**: Vollständige Nachverfolgung aller Änderungen erforderlich
- **Transaktionale Operationen**: Mehrere Aktionen als atomische Einheit
- **Makro-Operationen**: Komplexe Operationssequenzen
- **CQRS Implementation**: Trennung von Commands und Queries

**Command Pattern NICHT verwenden wenn:**
- **Einfache Operationen**: Direkte Methodenaufrufe sind ausreichend
- **Kein Undo benötigt**: Operationen sind final und sicher
- **Performance kritisch**: Command-Overhead ist problematisch
- **Stateless Operations**: Keine Zustandsveränderungen zu verwalten

### Komplexität vs. Nutzen verstehen

**Einfache Lösung wählen wenn:**
```typescript
// Einfach: Status-Updates ohne Rollback-Bedarf
class SimpleOperations {
  updateStatus(device: Device, status: Status): void {
    device.status = status;
    this.notifyStatusChange(device);
  }
}
```

**Command Pattern wählen wenn:**
```typescript
// Command Pattern: Kritische Konfigurationen mit Undo
class ConfigurationCommand implements Command {
  private originalConfig: Config;
  
  execute(): void {
    this.originalConfig = this.device.getConfig();
    this.device.applyConfig(this.newConfig);
  }
  
  undo(): void {
    this.device.applyConfig(this.originalConfig);
  }
}
```

---

## 8. Praktische Übung (20 Minuten)

### Diskussions-Aufgabe: Critical Network Operations

**Szenario**: Entwerfen Sie ein **Command-basiertes System** für **kritische Netzwerk-Operationen** bei der Telekom.

**Anforderungen:**
1. **Sicherheitskritische Operationen**: Firewall-Updates, Router-Konfigurationen
2. **Rollback-Fähigkeit**: Alle Änderungen müssen rückgängig machbar sein
3. **Audit-Compliance**: Vollständige Nachverfolgung aller Änderungen
4. **Batch-Operations**: Mehrere Änderungen als atomische Einheit

**Diskussionsfragen:**

1. **Command Design:**
   - Welche Operationen benötigen Command Pattern?
   - Welche können mit einfachen Methodenaufrufen gelöst werden?
   - Wie implementieren Sie sichere Undo-Operationen?

2. **Safety & Rollback:**
   - Wie stellen Sie sicher, dass Rollbacks immer funktionieren?
   - Was tun bei Rollback-Fehlern?
   - Wie validieren Sie Command-Ausführung?

3. **Macro Commands:**
   - Welche Operationssequenzen benötigen Macro Commands?
   - Wie behandeln Sie partielle Fehler?
   - Welche Rollback-Strategien verwenden Sie?

4. **CQRS Integration:**
   - Wie trennen Sie kritische Commands von Status-Queries?
   - Welche Events sollten Commands auslösen?

**Konzeptuelle Lösung diskutieren:**

```typescript
// Kritische Network Operation
class CriticalFirewallCommand implements Command {
  private backupConfig: FirewallConfig;
  
  execute(): void {
    // 1. Backup erstellen
    this.backupConfig = this.firewall.getConfig();
    
    // 2. Neue Regeln anwenden
    this.firewall.applyRules(this.newRules);
    
    // 3. Validierung
    if (!this.validateRules()) {
      this.undo(); // Automatisches Rollback
      throw new Error('Rule validation failed');
    }
  }
  
  undo(): void {
    this.firewall.applyRules(this.backupConfig.rules);
  }
}
```

**Gruppenarbeit (10 Minuten):**
Diskutieren Sie:
- Welche Netzwerk-Operationen sind zu kritisch für direkte Ausführung?
- Wie würden Sie Command-Validierung implementieren?
- Welche Rolle spielt Command Pattern bei Disaster Recovery?

**Ergebnisse zusammentragen (10 Minuten):**
Jede Gruppe präsentiert ihre Erkenntnisse zu Command Pattern in kritischen Systemen.

---

## Zusammenfassung

### Command Pattern - Die wichtigsten Erkenntnisse

**Was ist das Command Pattern?**
- **Request als Objekt**: Aktionen werden zu manipulierbaren Objekten
- **Undo/Redo-Fähigkeit**: Operationen können rückgängig gemacht werden
- **Entkopplung**: Invoker kennt nicht die Details der Ausführung

**Wann einsetzen?**
- Bei **kritischen Operationen** mit Rollback-Bedarf
- Für **Audit-Trails** und Compliance-Anforderungen
- Wenn **Operationen gequeued** werden müssen
- Für **transaktionale Operationssequenzen**

### Macro Commands verstehen

**Was sind Macro Commands?**
- **Composite Pattern** für Commands
- **Atomare Ausführung** mehrerer Operationen
- **Automatisches Rollback** bei Fehlern

### CQRS-Integration verstehen

**Command Query Responsibility Segregation:**
- **Commands**: Ändern Zustand, sind kritisch
- **Queries**: Lesen Zustand, sind häufig
- **Getrennte Optimierung** für Read/Write

### Verhaltensorientierte Denkweise

**Command Pattern fokussiert auf:**
- **WIE** Operationen rückgängig gemacht werden
- **WANN** Operationen ausgeführt werden
- **WER** für Undo-Fähigkeit verantwortlich ist

**Nächstes Modul:** State Pattern und Chain of Responsibility