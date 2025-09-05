# Tag 3 - Modul 4: State Pattern & Chain of Responsibility
## Zustandsmaschinen und Request-Pipelines

### Lernziele
- **State Pattern** für Device Lifecycle Management implementieren
- **Chain of Responsibility** für Request-Processing-Pipelines einsetzen
- Zustandsmaschinen für komplexe Device-Stati entwickeln
- Event-Driven State Transitions mit CQRS integrieren

---

## 1. State Pattern - Device Lifecycle Management

### Problem: Komplexe Zustandslogik

```typescript
// ❌ Problematisch: Zustandslogik in monolithischer Klasse
class NetworkDevice {
  private status: DeviceStatus = 'inactive';
  
  async activate(): Promise<void> {
    if (this.status === 'inactive') {
      this.status = 'initializing';
      await this.performInitialization();
      this.status = 'active';
    } else if (this.status === 'maintenance') {
      await this.exitMaintenanceMode();
      this.status = 'active';
    } else {
      throw new Error(`Cannot activate device in status: ${this.status}`);
    }
  }
  
  async deactivate(): Promise<void> {
    if (this.status === 'active') {
      await this.gracefulShutdown();
      this.status = 'inactive';
    } else if (this.status === 'error') {
      this.status = 'inactive';
    } else {
      throw new Error(`Cannot deactivate device in status: ${this.status}`);
    }
  }
  
  async enterMaintenance(): Promise<void> {
    if (this.status === 'active') {
      await this.redirectTraffic();
      this.status = 'maintenance';
    } else {
      throw new Error(`Cannot enter maintenance from status: ${this.status}`);
    }
  }
  
  // Weitere komplexe if-else Logik für jeden State...
}
```

**Probleme:**
- Zustandslogik verteilt sich über die gesamte Klasse
- Schwierige Erweiterung um neue Zustände
- Inkonsistente Implementierung von Transitions
- Komplexe Zustandsvalidierung

---

## 2. State Pattern Implementierung

### State Interface und Context

```typescript
// State Interface
interface DeviceState {
  activate(context: DeviceContext): Promise<StateTransitionResult>;
  deactivate(context: DeviceContext): Promise<StateTransitionResult>;
  enterMaintenance(context: DeviceContext): Promise<StateTransitionResult>;
  exitMaintenance(context: DeviceContext): Promise<StateTransitionResult>;
  handleError(context: DeviceContext, error: Error): Promise<StateTransitionResult>;
  
  // State Information
  getStateName(): string;
  getAllowedTransitions(): string[];
  getStateMetadata(): StateMetadata;
}

// State Transition Result
interface StateTransitionResult {
  success: boolean;
  newState?: DeviceState;
  message: string;
  events: StateEvent[];
  executionTime: number;
  error?: string;
}

// State Event für Event-Driven Architecture
interface StateEvent {
  deviceId: string;
  eventType: StateEventType;
  fromState: string;
  toState?: string;
  timestamp: Date;
  metadata: any;
}

type StateEventType = 'StateEntered' | 'StateExited' | 'TransitionStarted' | 'TransitionCompleted' | 'TransitionFailed';

interface StateMetadata {
  isOperational: boolean;
  allowsTraffic: boolean;
  requiresMonitoring: boolean;
  maintenanceLevel: 'none' | 'minimal' | 'full';
  riskLevel: 'low' | 'medium' | 'high';
}

// Device Context - State Pattern Context
class DeviceContext {
  private currentState: DeviceState;
  private deviceInfo: NetworkDevice;
  private eventPublisher: EventPublisher;
  private stateHistory: StateHistoryEntry[] = [];
  private transitionLocks: Map<string, boolean> = new Map();
  
  constructor(
    deviceInfo: NetworkDevice,
    initialState: DeviceState,
    eventPublisher: EventPublisher
  ) {
    this.deviceInfo = deviceInfo;
    this.currentState = initialState;
    this.eventPublisher = eventPublisher;
    
    this.addToHistory(initialState, 'StateEntered');
  }
  
  // Public State Transition Methods
  async activate(): Promise<StateTransitionResult> {
    return await this.executeTransition('activate', () => 
      this.currentState.activate(this)
    );
  }
  
  async deactivate(): Promise<StateTransitionResult> {
    return await this.executeTransition('deactivate', () => 
      this.currentState.deactivate(this)
    );
  }
  
  async enterMaintenance(): Promise<StateTransitionResult> {
    return await this.executeTransition('enterMaintenance', () => 
      this.currentState.enterMaintenance(this)
    );
  }
  
  async exitMaintenance(): Promise<StateTransitionResult> {
    return await this.executeTransition('exitMaintenance', () => 
      this.currentState.exitMaintenance(this)
    );
  }
  
  async handleError(error: Error): Promise<StateTransitionResult> {
    return await this.executeTransition('handleError', () => 
      this.currentState.handleError(this, error)
    );
  }
  
  // State Management
  async changeState(newState: DeviceState): Promise<void> {
    const oldStateName = this.currentState.getStateName();
    
    // Exit current state
    await this.publishStateEvent('StateExited', {
      fromState: oldStateName,
      toState: newState.getStateName()
    });
    
    this.addToHistory(this.currentState, 'StateExited');
    
    // Enter new state
    this.currentState = newState;
    
    await this.publishStateEvent('StateEntered', {
      fromState: oldStateName,
      toState: newState.getStateName()
    });
    
    this.addToHistory(newState, 'StateEntered');
  }
  
  // Helper Methods für States
  async executeTransition(
    transitionName: string, 
    transitionFn: () => Promise<StateTransitionResult>
  ): Promise<StateTransitionResult> {
    const lockKey = `${this.deviceInfo.id}-${transitionName}`;
    
    // Concurrent Transition Prevention
    if (this.transitionLocks.get(lockKey)) {
      return {
        success: false,
        message: `Transition ${transitionName} already in progress`,
        events: [],
        executionTime: 0
      };
    }
    
    this.transitionLocks.set(lockKey, true);
    const startTime = Date.now();
    
    try {
      await this.publishStateEvent('TransitionStarted', {
        fromState: this.currentState.getStateName(),
        transition: transitionName
      });
      
      const result = await transitionFn();
      
      if (result.success && result.newState) {
        await this.changeState(result.newState);
      }
      
      await this.publishStateEvent(
        result.success ? 'TransitionCompleted' : 'TransitionFailed',
        {
          fromState: this.currentState.getStateName(),
          transition: transitionName,
          success: result.success
        }
      );
      
      result.executionTime = Date.now() - startTime;
      return result;
      
    } finally {
      this.transitionLocks.set(lockKey, false);
    }
  }
  
  // Getters
  getCurrentState(): DeviceState {
    return this.currentState;
  }
  
  getDeviceInfo(): NetworkDevice {
    return this.deviceInfo;
  }
  
  getStateHistory(): StateHistoryEntry[] {
    return [...this.stateHistory];
  }
  
  // State Information
  isOperational(): boolean {
    return this.currentState.getStateMetadata().isOperational;
  }
  
  allowsTraffic(): boolean {
    return this.currentState.getStateMetadata().allowsTraffic;
  }
  
  // Event Publishing
  private async publishStateEvent(eventType: StateEventType, data: any): Promise<void> {
    const event: StateEvent = {
      deviceId: this.deviceInfo.id,
      eventType,
      fromState: this.currentState.getStateName(),
      timestamp: new Date(),
      metadata: data
    };
    
    await this.eventPublisher.publish(event);
  }
  
  // History Management
  private addToHistory(state: DeviceState, eventType: StateEventType): void {
    this.stateHistory.push({
      state: state.getStateName(),
      eventType,
      timestamp: new Date(),
      metadata: state.getStateMetadata()
    });
    
    // History begrenzen
    if (this.stateHistory.length > 100) {
      this.stateHistory.shift();
    }
  }
}

interface StateHistoryEntry {
  state: string;
  eventType: StateEventType;
  timestamp: Date;
  metadata: StateMetadata;
}
```

### Konkrete State Implementierungen

```typescript
// Inactive State
class InactiveDeviceState implements DeviceState {
  async activate(context: DeviceContext): Promise<StateTransitionResult> {
    try {
      const device = context.getDeviceInfo();
      
      // Pre-activation checks
      const healthCheck = await this.performHealthCheck(device);
      if (!healthCheck.passed) {
        return {
          success: false,
          message: `Health check failed: ${healthCheck.issues.join(', ')}`,
          events: [],
          executionTime: 0
        };
      }
      
      // Initialize device
      await this.initializeDevice(device);
      
      return {
        success: true,
        newState: new InitializingDeviceState(),
        message: 'Device activation started',
        events: [{
          deviceId: device.id,
          eventType: 'TransitionStarted',
          fromState: 'Inactive',
          toState: 'Initializing',
          timestamp: new Date(),
          metadata: { reason: 'Manual activation' }
        }],
        executionTime: 0
      };
      
    } catch (error) {
      return {
        success: false,
        message: `Activation failed: ${error.message}`,
        events: [],
        executionTime: 0,
        error: error.message
      };
    }
  }
  
  async deactivate(context: DeviceContext): Promise<StateTransitionResult> {
    return {
      success: false,
      message: 'Device is already inactive',
      events: [],
      executionTime: 0
    };
  }
  
  async enterMaintenance(context: DeviceContext): Promise<StateTransitionResult> {
    return {
      success: false,
      message: 'Cannot enter maintenance mode from inactive state',
      events: [],
      executionTime: 0
    };
  }
  
  async exitMaintenance(context: DeviceContext): Promise<StateTransitionResult> {
    return {
      success: false,
      message: 'Device is not in maintenance mode',
      events: [],
      executionTime: 0
    };
  }
  
  async handleError(context: DeviceContext, error: Error): Promise<StateTransitionResult> {
    // Inactive state can't handle errors, but can log them
    console.warn(`Error in inactive device ${context.getDeviceInfo().id}:`, error.message);
    
    return {
      success: true,
      message: 'Error logged, device remains inactive',
      events: [],
      executionTime: 0
    };
  }
  
  getStateName(): string {
    return 'Inactive';
  }
  
  getAllowedTransitions(): string[] {
    return ['activate'];
  }
  
  getStateMetadata(): StateMetadata {
    return {
      isOperational: false,
      allowsTraffic: false,
      requiresMonitoring: false,
      maintenanceLevel: 'none',
      riskLevel: 'low'
    };
  }
  
  private async performHealthCheck(device: NetworkDevice): Promise<HealthCheckResult> {
    const issues: string[] = [];
    
    // Hardware checks
    if (!await this.checkHardwareStatus(device)) {
      issues.push('Hardware issues detected');
    }
    
    // Connectivity checks
    if (!await this.checkConnectivity(device)) {
      issues.push('Network connectivity issues');
    }
    
    // Configuration checks
    if (!await this.validateConfiguration(device)) {
      issues.push('Invalid configuration');
    }
    
    return {
      passed: issues.length === 0,
      issues
    };
  }
  
  private async initializeDevice(device: NetworkDevice): Promise<void> {
    // Device-specific initialization logic
    await device.performSelfTest();
    await device.loadConfiguration();
    await device.establishManagementConnection();
  }
}
```

```typescript
// Active State
class ActiveDeviceState implements DeviceState {
  async activate(context: DeviceContext): Promise<StateTransitionResult> {
    return {
      success: false,
      message: 'Device is already active',
      events: [],
      executionTime: 0
    };
  }
  
  async deactivate(context: DeviceContext): Promise<StateTransitionResult> {
    try {
      const device = context.getDeviceInfo();
      
      // Graceful traffic drainage
      await this.drainTraffic(device);
      
      // Notify connected devices
      await this.notifyConnectedDevices(device);
      
      // Clean shutdown
      await this.performGracefulShutdown(device);
      
      return {
        success: true,
        newState: new InactiveDeviceState(),
        message: 'Device deactivated successfully',
        events: [{
          deviceId: device.id,
          eventType: 'TransitionStarted',
          fromState: 'Active',
          toState: 'Inactive',
          timestamp: new Date(),
          metadata: { reason: 'Graceful shutdown' }
        }],
        executionTime: 0
      };
      
    } catch (error) {
      return {
        success: false,
        message: `Deactivation failed: ${error.message}`,
        events: [],
        executionTime: 0,
        error: error.message
      };
    }
  }
  
  async enterMaintenance(context: DeviceContext): Promise<StateTransitionResult> {
    try {
      const device = context.getDeviceInfo();
      
      // Check if maintenance is allowed
      const maintenanceCheck = await this.canEnterMaintenance(device);
      if (!maintenanceCheck.allowed) {
        return {
          success: false,
          message: maintenanceCheck.reason,
          events: [],
          executionTime: 0
        };
      }
      
      // Redirect traffic
      await this.redirectTraffic(device);
      
      // Reduce monitoring
      await this.adjustMonitoring(device, 'maintenance');
      
      return {
        success: true,
        newState: new MaintenanceDeviceState(),
        message: 'Entered maintenance mode',
        events: [{
          deviceId: device.id,
          eventType: 'TransitionStarted',
          fromState: 'Active',
          toState: 'Maintenance',
          timestamp: new Date(),
          metadata: { 
            reason: 'Scheduled maintenance',
            trafficRedirected: true
          }
        }],
        executionTime: 0
      };
      
    } catch (error) {
      return {
        success: false,
        message: `Failed to enter maintenance: ${error.message}`,
        events: [],
        executionTime: 0,
        error: error.message
      };
    }
  }
  
  async exitMaintenance(context: DeviceContext): Promise<StateTransitionResult> {
    return {
      success: false,
      message: 'Device is not in maintenance mode',
      events: [],
      executionTime: 0
    };
  }
  
  async handleError(context: DeviceContext, error: Error): Promise<StateTransitionResult> {
    const device = context.getDeviceInfo();
    
    // Error severity analysis
    const severity = this.analyzeErrorSeverity(error);
    
    if (severity === 'critical') {
      // Immediate transition to error state
      await this.performEmergencyShutdown(device);
      
      return {
        success: true,
        newState: new ErrorDeviceState(error),
        message: `Critical error detected: ${error.message}`,
        events: [{
          deviceId: device.id,
          eventType: 'TransitionStarted',
          fromState: 'Active',
          toState: 'Error',
          timestamp: new Date(),
          metadata: { 
            errorType: 'critical',
            originalError: error.message
          }
        }],
        executionTime: 0
      };
    } else {
      // Try to recover from non-critical errors
      try {
        await this.attemptErrorRecovery(device, error);
        
        return {
          success: true,
          message: `Recovered from error: ${error.message}`,
          events: [],
          executionTime: 0
        };
      } catch (recoveryError) {
        return {
          success: true,
          newState: new ErrorDeviceState(error),
          message: `Error recovery failed: ${recoveryError.message}`,
          events: [],
          executionTime: 0
        };
      }
    }
  }
  
  getStateName(): string {
    return 'Active';
  }
  
  getAllowedTransitions(): string[] {
    return ['deactivate', 'enterMaintenance', 'handleError'];
  }
  
  getStateMetadata(): StateMetadata {
    return {
      isOperational: true,
      allowsTraffic: true,
      requiresMonitoring: true,
      maintenanceLevel: 'minimal',
      riskLevel: 'low'
    };
  }
  
  private async canEnterMaintenance(device: NetworkDevice): Promise<MaintenanceCheckResult> {
    // Business rules für Maintenance
    const currentHour = new Date().getHours();
    if (currentHour >= 6 && currentHour <= 22) {
      return {
        allowed: false,
        reason: 'Maintenance not allowed during business hours (6 AM - 10 PM)'
      };
    }
    
    // Check device criticality
    const criticality = await device.getCriticalityLevel();
    if (criticality === 'critical' && !await this.hasRedundantPath(device)) {
      return {
        allowed: false,
        reason: 'Critical device without redundant path cannot enter maintenance'
      };
    }
    
    return {
      allowed: true,
      reason: 'Maintenance allowed'
    };
  }
  
  private analyzeErrorSeverity(error: Error): 'low' | 'medium' | 'critical' {
    const criticalPatterns = [
      'hardware failure',
      'power loss',
      'temperature critical',
      'memory corruption'
    ];
    
    const errorMessage = error.message.toLowerCase();
    
    if (criticalPatterns.some(pattern => errorMessage.includes(pattern))) {
      return 'critical';
    }
    
    return 'medium';
  }
}
```

```typescript
// Maintenance State
class MaintenanceDeviceState implements DeviceState {
  async activate(context: DeviceContext): Promise<StateTransitionResult> {
    return {
      success: false,
      message: 'Cannot activate device in maintenance mode. Exit maintenance first.',
      events: [],
      executionTime: 0
    };
  }
  
  async deactivate(context: DeviceContext): Promise<StateTransitionResult> {
    try {
      const device = context.getDeviceInfo();
      
      // Exit maintenance first, then deactivate
      await this.exitMaintenanceMode(device);
      await this.performShutdown(device);
      
      return {
        success: true,
        newState: new InactiveDeviceState(),
        message: 'Device deactivated from maintenance mode',
        events: [],
        executionTime: 0
      };
      
    } catch (error) {
      return {
        success: false,
        message: `Failed to deactivate from maintenance: ${error.message}`,
        events: [],
        executionTime: 0,
        error: error.message
      };
    }
  }
  
  async enterMaintenance(context: DeviceContext): Promise<StateTransitionResult> {
    return {
      success: false,
      message: 'Device is already in maintenance mode',
      events: [],
      executionTime: 0
    };
  }
  
  async exitMaintenance(context: DeviceContext): Promise<StateTransitionResult> {
    try {
      const device = context.getDeviceInfo();
      
      // Maintenance completion checks
      const readinessCheck = await this.checkMaintenanceReadiness(device);
      if (!readinessCheck.ready) {
        return {
          success: false,
          message: `Cannot exit maintenance: ${readinessCheck.issues.join(', ')}`,
          events: [],
          executionTime: 0
        };
      }
      
      // Restore traffic routing
      await this.restoreTrafficRouting(device);
      
      // Restore full monitoring
      await this.adjustMonitoring(device, 'active');
      
      // Validate operational status
      const operationalCheck = await this.validateOperationalStatus(device);
      if (!operationalCheck.operational) {
        throw new Error(`Device not operational after maintenance: ${operationalCheck.issues.join(', ')}`);
      }
      
      return {
        success: true,
        newState: new ActiveDeviceState(),
        message: 'Successfully exited maintenance mode',
        events: [{
          deviceId: device.id,
          eventType: 'TransitionCompleted',
          fromState: 'Maintenance',
          toState: 'Active',
          timestamp: new Date(),
          metadata: {
            maintenanceDuration: this.calculateMaintenanceDuration(device),
            trafficRestored: true
          }
        }],
        executionTime: 0
      };
      
    } catch (error) {
      return {
        success: false,
        message: `Failed to exit maintenance: ${error.message}`,
        events: [],
        executionTime: 0,
        error: error.message
      };
    }
  }
  
  async handleError(context: DeviceContext, error: Error): Promise<StateTransitionResult> {
    // In maintenance mode, most errors are expected/tolerable
    console.info(`Maintenance mode error (expected): ${error.message}`);
    
    return {
      success: true,
      message: 'Error handled in maintenance mode',
      events: [],
      executionTime: 0
    };
  }
  
  getStateName(): string {
    return 'Maintenance';
  }
  
  getAllowedTransitions(): string[] {
    return ['exitMaintenance', 'deactivate'];
  }
  
  getStateMetadata(): StateMetadata {
    return {
      isOperational: false,
      allowsTraffic: false,
      requiresMonitoring: false,
      maintenanceLevel: 'full',
      riskLevel: 'medium'
    };
  }
}
```

---

## 3. Chain of Responsibility - Request Processing Pipeline

### Problem: Monolithische Request-Verarbeitung

```typescript
// ❌ Problematisch: Alle Verarbeitungslogik in einer Klasse
class NetworkRequestProcessor {
  async processRequest(request: NetworkRequest): Promise<NetworkResponse> {
    // Authentication
    if (!this.isAuthenticated(request)) {
      return this.createErrorResponse('Authentication failed');
    }
    
    // Authorization
    if (!this.isAuthorized(request)) {
      return this.createErrorResponse('Authorization failed');
    }
    
    // Rate Limiting
    if (this.isRateLimited(request)) {
      return this.createErrorResponse('Rate limit exceeded');
    }
    
    // Input Validation
    if (!this.isValidInput(request)) {
      return this.createErrorResponse('Invalid input');
    }
    
    // Business Logic
    const result = await this.executeBusinessLogic(request);
    
    // Response Transformation
    return this.transformResponse(result);
  }
  
  // Alle Handler-Methoden in einer Klasse...
}
```

### Chain of Responsibility Implementation

```typescript
// Handler Interface
abstract class RequestHandler {
  protected nextHandler?: RequestHandler;
  
  setNext(handler: RequestHandler): RequestHandler {
    this.nextHandler = handler;
    return handler;
  }
  
  async handle(request: NetworkRequest): Promise<NetworkResponse | null> {
    const response = await this.process(request);
    
    if (response) {
      return response;
    }
    
    if (this.nextHandler) {
      return await this.nextHandler.handle(request);
    }
    
    return null;
  }
  
  protected abstract process(request: NetworkRequest): Promise<NetworkResponse | null>;
  
  protected abstract getHandlerName(): string;
  
  protected async publishHandlerEvent(eventType: string, request: NetworkRequest, data?: any): Promise<void> {
    // Event publishing für Monitoring und Auditing
    const event = {
      handlerName: this.getHandlerName(),
      eventType,
      requestId: request.id,
      timestamp: new Date(),
      data
    };
    
    // Event Publisher würde hier injiziert werden
    console.log('Handler Event:', event);
  }
}

// Network Request und Response Interfaces
interface NetworkRequest {
  id: string;
  type: NetworkRequestType;
  source: RequestSource;
  payload: any;
  headers: Record<string, string>;
  timestamp: Date;
  metadata?: Record<string, any>;
}

interface NetworkResponse {
  requestId: string;
  success: boolean;
  data?: any;
  error?: string;
  statusCode: number;
  processingTime: number;
  handlerChain: string[];
}

type NetworkRequestType = 'DeviceConfig' | 'NetworkTopology' | 'MonitoringData' | 'MaintenanceScheduling';

interface RequestSource {
  userId?: string;
  deviceId?: string;
  serviceId?: string;
  ipAddress: string;
  userAgent?: string;
}
```

### Konkrete Handler Implementierungen

```typescript
// Authentication Handler
class AuthenticationHandler extends RequestHandler {
  private authService: AuthenticationService;
  
  constructor(authService: AuthenticationService) {
    super();
    this.authService = authService;
  }
  
  protected async process(request: NetworkRequest): Promise<NetworkResponse | null> {
    await this.publishHandlerEvent('AuthenticationStarted', request);
    
    try {
      const authToken = request.headers['authorization'];
      
      if (!authToken) {
        await this.publishHandlerEvent('AuthenticationFailed', request, {
          reason: 'No authentication token provided'
        });
        
        return {
          requestId: request.id,
          success: false,
          error: 'Authentication required',
          statusCode: 401,
          processingTime: 0,
          handlerChain: [this.getHandlerName()]
        };
      }
      
      const authResult = await this.authService.validateToken(authToken);
      
      if (!authResult.valid) {
        await this.publishHandlerEvent('AuthenticationFailed', request, {
          reason: authResult.reason
        });
        
        return {
          requestId: request.id,
          success: false,
          error: `Authentication failed: ${authResult.reason}`,
          statusCode: 401,
          processingTime: 0,
          handlerChain: [this.getHandlerName()]
        };
      }
      
      // Authentication erfolgreich - Request mit User Info erweitern
      request.metadata = {
        ...request.metadata,
        authenticatedUser: authResult.user,
        authenticationTimestamp: new Date()
      };
      
      await this.publishHandlerEvent('AuthenticationSucceeded', request, {
        userId: authResult.user.id
      });
      
      // Kein Response - weiter in der Chain
      return null;
      
    } catch (error) {
      await this.publishHandlerEvent('AuthenticationError', request, {
        error: error.message
      });
      
      return {
        requestId: request.id,
        success: false,
        error: `Authentication service error: ${error.message}`,
        statusCode: 500,
        processingTime: 0,
        handlerChain: [this.getHandlerName()]
      };
    }
  }
  
  protected getHandlerName(): string {
    return 'AuthenticationHandler';
  }
}

// Authorization Handler
class AuthorizationHandler extends RequestHandler {
  private authzService: AuthorizationService;
  private rolePermissionMap: Map<string, string[]> = new Map();
  
  constructor(authzService: AuthorizationService) {
    super();
    this.authzService = authzService;
    this.initializePermissions();
  }
  
  protected async process(request: NetworkRequest): Promise<NetworkResponse | null> {
    await this.publishHandlerEvent('AuthorizationStarted', request);
    
    try {
      const user = request.metadata?.authenticatedUser;
      
      if (!user) {
        // Sollte nicht vorkommen wenn Authentication Handler korrekt funktioniert
        return {
          requestId: request.id,
          success: false,
          error: 'User not authenticated',
          statusCode: 401,
          processingTime: 0,
          handlerChain: [this.getHandlerName()]
        };
      }
      
      const requiredPermissions = this.getRequiredPermissions(request.type);
      const authzResult = await this.authzService.checkPermissions(
        user.id, 
        requiredPermissions
      );
      
      if (!authzResult.authorized) {
        await this.publishHandlerEvent('AuthorizationDenied', request, {
          userId: user.id,
          requiredPermissions,
          userPermissions: authzResult.userPermissions
        });
        
        return {
          requestId: request.id,
          success: false,
          error: `Insufficient permissions. Required: ${requiredPermissions.join(', ')}`,
          statusCode: 403,
          processingTime: 0,
          handlerChain: [this.getHandlerName()]
        };
      }
      
      // Authorization erfolgreich
      request.metadata = {
        ...request.metadata,
        authorizedPermissions: authzResult.userPermissions,
        authorizationTimestamp: new Date()
      };
      
      await this.publishHandlerEvent('AuthorizationGranted', request, {
        userId: user.id,
        grantedPermissions: authzResult.userPermissions
      });
      
      return null; // Weiter in der Chain
      
    } catch (error) {
      return {
        requestId: request.id,
        success: false,
        error: `Authorization service error: ${error.message}`,
        statusCode: 500,
        processingTime: 0,
        handlerChain: [this.getHandlerName()]
      };
    }
  }
  
  private getRequiredPermissions(requestType: NetworkRequestType): string[] {
    const permissionMap = {
      'DeviceConfig': ['device:read', 'device:write'],
      'NetworkTopology': ['network:read'],
      'MonitoringData': ['monitoring:read'],
      'MaintenanceScheduling': ['maintenance:read', 'maintenance:write']
    };
    
    return permissionMap[requestType] || [];
  }
  
  private initializePermissions(): void {
    this.rolePermissionMap.set('admin', [
      'device:read', 'device:write',
      'network:read', 'network:write',
      'monitoring:read', 'monitoring:write',
      'maintenance:read', 'maintenance:write'
    ]);
    
    this.rolePermissionMap.set('operator', [
      'device:read',
      'network:read',
      'monitoring:read',
      'maintenance:read'
    ]);
  }
  
  protected getHandlerName(): string {
    return 'AuthorizationHandler';
  }
}

// Rate Limiting Handler
class RateLimitingHandler extends RequestHandler {
  private rateLimiter: RateLimiter;
  private limits: Map<string, RateLimit> = new Map();
  
  constructor(rateLimiter: RateLimiter) {
    super();
    this.rateLimiter = rateLimiter;
    this.initializeRateLimits();
  }
  
  protected async process(request: NetworkRequest): Promise<NetworkResponse | null> {
    await this.publishHandlerEvent('RateLimitCheckStarted', request);
    
    try {
      const identifier = this.getRateLimitIdentifier(request);
      const limit = this.getRateLimitForRequest(request);
      
      const limitResult = await this.rateLimiter.checkLimit(
        identifier, 
        limit.requests, 
        limit.windowMs
      );
      
      if (limitResult.exceeded) {
        await this.publishHandlerEvent('RateLimitExceeded', request, {
          identifier,
          limit: limit.requests,
          window: limit.windowMs,
          current: limitResult.current,
          resetTime: limitResult.resetTime
        });
        
        return {
          requestId: request.id,
          success: false,
          error: `Rate limit exceeded. Try again in ${Math.ceil((limitResult.resetTime - Date.now()) / 1000)} seconds`,
          statusCode: 429,
          processingTime: 0,
          handlerChain: [this.getHandlerName()]
        };
      }
      
      // Rate limit OK - Request mit Limit-Info erweitern
      request.metadata = {
        ...request.metadata,
        rateLimitInfo: {
          identifier,
          remaining: limit.requests - limitResult.current,
          resetTime: limitResult.resetTime
        }
      };
      
      await this.publishHandlerEvent('RateLimitPassed', request);
      
      return null; // Weiter in der Chain
      
    } catch (error) {
      return {
        requestId: request.id,
        success: false,
        error: `Rate limiting service error: ${error.message}`,
        statusCode: 500,
        processingTime: 0,
        handlerChain: [this.getHandlerName()]
      };
    }
  }
  
  private getRateLimitIdentifier(request: NetworkRequest): string {
    // User-based rate limiting wenn möglich, sonst IP-based
    const user = request.metadata?.authenticatedUser;
    if (user) {
      return `user:${user.id}`;
    }
    
    return `ip:${request.source.ipAddress}`;
  }
  
  private getRateLimitForRequest(request: NetworkRequest): RateLimit {
    // Verschiedene Limits für verschiedene Request-Typen
    const typeLimit = this.limits.get(request.type);
    if (typeLimit) {
      return typeLimit;
    }
    
    // Default limit
    return this.limits.get('default')!;
  }
  
  private initializeRateLimits(): void {
    this.limits.set('default', { requests: 100, windowMs: 60000 }); // 100 requests per minute
    this.limits.set('DeviceConfig', { requests: 20, windowMs: 60000 }); // 20 config changes per minute
    this.limits.set('MonitoringData', { requests: 500, windowMs: 60000 }); // 500 monitoring requests per minute
  }
  
  protected getHandlerName(): string {
    return 'RateLimitingHandler';
  }
}

interface RateLimit {
  requests: number;
  windowMs: number;
}
```

### Validation und Business Logic Handlers

```typescript
// Input Validation Handler
class InputValidationHandler extends RequestHandler {
  private validationService: ValidationService;
  
  constructor(validationService: ValidationService) {
    super();
    this.validationService = validationService;
  }
  
  protected async process(request: NetworkRequest): Promise<NetworkResponse | null> {
    await this.publishHandlerEvent('ValidationStarted', request);
    
    try {
      const validationResult = await this.validationService.validate(
        request.type, 
        request.payload
      );
      
      if (!validationResult.valid) {
        await this.publishHandlerEvent('ValidationFailed', request, {
          errors: validationResult.errors
        });
        
        return {
          requestId: request.id,
          success: false,
          error: `Input validation failed: ${validationResult.errors.join(', ')}`,
          statusCode: 400,
          processingTime: 0,
          handlerChain: [this.getHandlerName()]
        };
      }
      
      // Validation erfolgreich - normalisierte Daten setzen
      request.payload = validationResult.normalizedData || request.payload;
      
      await this.publishHandlerEvent('ValidationPassed', request);
      
      return null; // Weiter in der Chain
      
    } catch (error) {
      return {
        requestId: request.id,
        success: false,
        error: `Validation service error: ${error.message}`,
        statusCode: 500,
        processingTime: 0,
        handlerChain: [this.getHandlerName()]
      };
    }
  }
  
  protected getHandlerName(): string {
    return 'InputValidationHandler';
  }
}

// Business Logic Handler
class BusinessLogicHandler extends RequestHandler {
  private businessServices: Map<NetworkRequestType, BusinessService> = new Map();
  
  constructor(businessServices: BusinessService[]) {
    super();
    
    for (const service of businessServices) {
      this.businessServices.set(service.getRequestType(), service);
    }
  }
  
  protected async process(request: NetworkRequest): Promise<NetworkResponse | null> {
    const startTime = Date.now();
    await this.publishHandlerEvent('BusinessLogicStarted', request);
    
    try {
      const businessService = this.businessServices.get(request.type);
      
      if (!businessService) {
        return {
          requestId: request.id,
          success: false,
          error: `No business service available for request type: ${request.type}`,
          statusCode: 501,
          processingTime: Date.now() - startTime,
          handlerChain: [this.getHandlerName()]
        };
      }
      
      const result = await businessService.process(request);
      
      const response: NetworkResponse = {
        requestId: request.id,
        success: result.success,
        data: result.data,
        error: result.error,
        statusCode: result.success ? 200 : 500,
        processingTime: Date.now() - startTime,
        handlerChain: [this.getHandlerName()]
      };
      
      await this.publishHandlerEvent(
        result.success ? 'BusinessLogicCompleted' : 'BusinessLogicFailed', 
        request, 
        { result }
      );
      
      return response;
      
    } catch (error) {
      return {
        requestId: request.id,
        success: false,
        error: `Business logic error: ${error.message}`,
        statusCode: 500,
        processingTime: Date.now() - startTime,
        handlerChain: [this.getHandlerName()]
      };
    }
  }
  
  protected getHandlerName(): string {
    return 'BusinessLogicHandler';
  }
}

// Business Service Interface
interface BusinessService {
  getRequestType(): NetworkRequestType;
  process(request: NetworkRequest): Promise<BusinessResult>;
}

interface BusinessResult {
  success: boolean;
  data?: any;
  error?: string;
}
```

### Request Processor mit Chain Setup

```typescript
class NetworkRequestProcessor {
  private handlerChain: RequestHandler;
  private eventPublisher: EventPublisher;
  
  constructor(
    authService: AuthenticationService,
    authzService: AuthorizationService,
    rateLimiter: RateLimiter,
    validationService: ValidationService,
    businessServices: BusinessService[],
    eventPublisher: EventPublisher
  ) {
    this.eventPublisher = eventPublisher;
    this.setupHandlerChain(
      authService,
      authzService,
      rateLimiter,
      validationService,
      businessServices
    );
  }
  
  async processRequest(request: NetworkRequest): Promise<NetworkResponse> {
    const startTime = Date.now();
    
    await this.publishProcessingEvent('RequestReceived', request);
    
    try {
      const response = await this.handlerChain.handle(request);
      
      if (!response) {
        // Kein Handler hat eine Response zurückgegeben
        const errorResponse: NetworkResponse = {
          requestId: request.id,
          success: false,
          error: 'Request not handled by any handler in the chain',
          statusCode: 500,
          processingTime: Date.now() - startTime,
          handlerChain: ['No handler processed request']
        };
        
        await this.publishProcessingEvent('RequestNotHandled', request, { response: errorResponse });
        return errorResponse;
      }
      
      // Handler chain paths aktualisieren
      response.processingTime = Date.now() - startTime;
      response.handlerChain = this.buildHandlerChainPath(request);
      
      await this.publishProcessingEvent(
        response.success ? 'RequestProcessed' : 'RequestFailed', 
        request, 
        { response }
      );
      
      return response;
      
    } catch (error) {
      const errorResponse: NetworkResponse = {
        requestId: request.id,
        success: false,
        error: `Request processing error: ${error.message}`,
        statusCode: 500,
        processingTime: Date.now() - startTime,
        handlerChain: ['ProcessingError']
      };
      
      await this.publishProcessingEvent('RequestProcessingError', request, { 
        error: error.message,
        response: errorResponse 
      });
      
      return errorResponse;
    }
  }
  
  private setupHandlerChain(
    authService: AuthenticationService,
    authzService: AuthorizationService,
    rateLimiter: RateLimiter,
    validationService: ValidationService,
    businessServices: BusinessService[]
  ): void {
    // Chain of Responsibility Setup
    const authenticationHandler = new AuthenticationHandler(authService);
    const authorizationHandler = new AuthorizationHandler(authzService);
    const rateLimitingHandler = new RateLimitingHandler(rateLimiter);
    const validationHandler = new InputValidationHandler(validationService);
    const businessLogicHandler = new BusinessLogicHandler(businessServices);
    
    // Chain verknüpfen
    authenticationHandler
      .setNext(authorizationHandler)
      .setNext(rateLimitingHandler)
      .setNext(validationHandler)
      .setNext(businessLogicHandler);
    
    this.handlerChain = authenticationHandler;
  }
  
  private buildHandlerChainPath(request: NetworkRequest): string[] {
    // Würde normalerweise aus dem Request Metadata extrahiert
    return [
      'AuthenticationHandler',
      'AuthorizationHandler', 
      'RateLimitingHandler',
      'InputValidationHandler',
      'BusinessLogicHandler'
    ];
  }
  
  private async publishProcessingEvent(
    eventType: string, 
    request: NetworkRequest, 
    data?: any
  ): Promise<void> {
    const event = {
      eventType,
      requestId: request.id,
      requestType: request.type,
      timestamp: new Date(),
      data
    };
    
    await this.eventPublisher.publish(event);
  }
}
```

---

## 4. Testing-Strategien

### State Pattern Tests

```typescript
describe('DeviceContext State Management', () => {
  let deviceContext: DeviceContext;
  let mockDevice: NetworkDevice;
  let mockEventPublisher: jest.Mocked<EventPublisher>;
  
  beforeEach(() => {
    mockDevice = {
      id: 'device-123',
      type: 'router',
      performSelfTest: jest.fn().mockResolvedValue(undefined),
      loadConfiguration: jest.fn().mockResolvedValue(undefined)
    };
    
    mockEventPublisher = {
      publish: jest.fn().mockResolvedValue(undefined)
    };
    
    deviceContext = new DeviceContext(
      mockDevice,
      new InactiveDeviceState(),
      mockEventPublisher
    );
  });
  
  describe('State Transitions', () => {
    it('should transition from Inactive to Initializing on activate', async () => {
      // Act
      const result = await deviceContext.activate();
      
      // Assert
      expect(result.success).toBe(true);
      expect(deviceContext.getCurrentState().getStateName()).toBe('Initializing');
      expect(mockEventPublisher.publish).toHaveBeenCalledWith(
        expect.objectContaining({
          eventType: 'StateEntered',
          fromState: 'Inactive'
        })
      );
    });
    
    it('should prevent concurrent transitions', async () => {
      // Arrange
      const firstActivation = deviceContext.activate();
      const secondActivation = deviceContext.activate();
      
      // Act
      const [firstResult, secondResult] = await Promise.all([
        firstActivation, 
        secondActivation
      ]);
      
      // Assert
      expect(firstResult.success).toBe(true);
      expect(secondResult.success).toBe(false);
      expect(secondResult.message).toContain('already in progress');
    });
  });
  
  describe('State History', () => {
    it('should maintain state history', async () => {
      // Act
      await deviceContext.activate();
      
      // Assert
      const history = deviceContext.getStateHistory();
      expect(history).toHaveLength(2); // Initial Inactive + Transition to Initializing
      expect(history[0].state).toBe('Inactive');
      expect(history[1].state).toBe('Initializing');
    });
  });
});
```

### Chain of Responsibility Tests

```typescript
describe('NetworkRequestProcessor Chain', () => {
  let processor: NetworkRequestProcessor;
  let mockAuthService: jest.Mocked<AuthenticationService>;
  let mockBusinessService: jest.Mocked<BusinessService>;
  
  beforeEach(() => {
    mockAuthService = {
      validateToken: jest.fn()
    };
    
    mockBusinessService = {
      getRequestType: jest.fn().mockReturnValue('DeviceConfig'),
      process: jest.fn()
    };
    
    processor = new NetworkRequestProcessor(
      mockAuthService,
      // ... andere Mock-Services
      [mockBusinessService],
      mockEventPublisher
    );
  });
  
  describe('Request Processing', () => {
    it('should process valid authenticated request', async () => {
      // Arrange
      mockAuthService.validateToken.mockResolvedValue({
        valid: true,
        user: { id: 'user-123', name: 'Test User' }
      });
      
      mockBusinessService.process.mockResolvedValue({
        success: true,
        data: { result: 'processed' }
      });
      
      const request: NetworkRequest = {
        id: 'req-123',
        type: 'DeviceConfig',
        source: { ipAddress: '192.168.1.100' },
        payload: { config: 'test' },
        headers: { authorization: 'Bearer valid-token' },
        timestamp: new Date()
      };
      
      // Act
      const response = await processor.processRequest(request);
      
      // Assert
      expect(response.success).toBe(true);
      expect(response.data).toEqual({ result: 'processed' });
      expect(response.handlerChain).toContain('AuthenticationHandler');
      expect(response.handlerChain).toContain('BusinessLogicHandler');
    });
    
    it('should reject unauthenticated request', async () => {
      // Arrange
      const request: NetworkRequest = {
        id: 'req-456',
        type: 'DeviceConfig',
        source: { ipAddress: '192.168.1.100' },
        payload: { config: 'test' },
        headers: {}, // Kein Auth-Header
        timestamp: new Date()
      };
      
      // Act
      const response = await processor.processRequest(request);
      
      // Assert
      expect(response.success).toBe(false);
      expect(response.statusCode).toBe(401);
      expect(response.error).toContain('Authentication required');
      expect(mockBusinessService.process).not.toHaveBeenCalled();
    });
  });
});
```

---

## 5. Pattern Integration und Best Practices

### State Pattern + Event Sourcing

```typescript
// Event-Sourced Device State Management
class EventSourcedDeviceContext extends DeviceContext {
  private eventStore: EventStore;
  
  constructor(
    deviceInfo: NetworkDevice,
    eventStore: EventStore,
    eventPublisher: EventPublisher
  ) {
    // State aus Event Store rekonstruieren
    const currentState = EventSourcedDeviceContext.rebuildStateFromEvents(
      deviceInfo.id, 
      eventStore
    );
    
    super(deviceInfo, currentState, eventPublisher);
    this.eventStore = eventStore;
  }
  
  async changeState(newState: DeviceState): Promise<void> {
    await super.changeState(newState);
    
    // Event für Event Sourcing speichern
    const stateChangeEvent = {
      streamId: this.getDeviceInfo().id,
      eventType: 'StateChanged',
      data: {
        newState: newState.getStateName(),
        timestamp: new Date(),
        metadata: newState.getStateMetadata()
      }
    };
    
    await this.eventStore.append(stateChangeEvent);
  }
  
  static rebuildStateFromEvents(deviceId: string, eventStore: EventStore): DeviceState {
    // Implementation würde Events lesen und State rekonstruieren
    return new InactiveDeviceState(); // Simplified
  }
}
```

---

## 6. Praktische Übung (20 Minuten)

### Aufgabe: Device Monitoring Pipeline

Kombinieren Sie State Pattern und Chain of Responsibility:

1. **MonitoringDeviceState:**
   - Neuer State für intensive Monitoring
   - Transitions von/zu Active State
   - Spezielle Monitoring-Events

2. **Monitoring Request Pipeline:**
   - DataValidationHandler
   - MetricsEnrichmentHandler
   - AlertingHandler
   - ArchivingHandler

3. **Integration:**
   - State-basierte Request-Verarbeitung
   - Verschiedene Handler-Chains je State

### Implementation Template:

```typescript
class MonitoringDeviceState implements DeviceState {
  // TODO: Monitoring State implementieren
}

class DataValidationHandler extends RequestHandler {
  protected async process(request: NetworkRequest): Promise<NetworkResponse | null> {
    // TODO: Monitoring-Daten validieren
  }
}

class StateAwareRequestProcessor {
  // TODO: State-basierte Request-Verarbeitung
}
```

### Diskussionspunkte:
- Wie beeinflussen Device States die Request-Verarbeitung?
- Welche Handler sind für welche States relevant?
- Wie kann man State Transitions durch Request Processing auslösen?

---

## 7. Pattern vs. einfache Lösung

### Wann State Pattern verwenden?

**✅ Verwenden wenn:**
- Objekt-Verhalten sich basierend auf internem Zustand ändert
- Komplexe Zustandsmaschinen implementiert werden
- Zustandslogik nicht in if-else-Kaskaden verteilt werden soll
- Neue Zustände häufig hinzugefügt werden

**❌ Nicht verwenden wenn:**
- Nur wenige, einfache Zustände existieren
- Zustandswechsel selten vorkommen
- Overhead nicht gerechtfertigt

### Wann Chain of Responsibility verwenden?

**✅ Verwenden wenn:**
- Request von verschiedenen Handlern verarbeitet werden kann
- Processing-Pipeline dynamisch konfigurierbar sein soll
- Sender und Empfänger entkoppelt werden sollen
- Handler zur Laufzeit hinzugefügt/entfernt werden

**❌ Nicht verwenden wenn:**
- Nur ein Handler für Request-Typ existiert
- Processing-Reihenfolge immer gleich ist
- Performance kritischer als Flexibilität

---

## Zusammenfassung

**State Pattern Vorteile:**
- Saubere Trennung von Zustandslogik
- Einfache Erweiterung um neue Zustände
- Konsistente State Transitions
- Event-Driven Architecture Unterstützung

**Chain of Responsibility Vorteile:**
- Flexible, konfigurierbare Processing-Pipelines
- Lose Kopplung zwischen Sendern und Empfängern
- Single Responsibility für jeden Handler
- Einfache Erweiterung und Modifikation

**Key Takeaways:**
- Behavioral Patterns strukturieren komplexe Interaktionen
- State Pattern + Chain of Responsibility = mächtige Kombinationen
- Event-Driven Architecture profitiert von beiden Patterns
- Testing-Strategien essentiell für komplexe Zustandsmaschinen

**Behavioral Patterns Gesamtübersicht:**
- **Strategy:** Austauschbare Algorithmen
- **Template Method:** Workflow-Strukturierung
- **Observer:** Event-Notification
- **Command:** Undo/Redo Operations
- **State:** Zustandsmaschinen
- **Chain of Responsibility:** Request-Pipelines

**Nächster Tag:** Architektur-Patterns und Microservices