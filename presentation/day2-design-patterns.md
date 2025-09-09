# Design Patterns Workshop - Day 2
## Strukturmuster in der Praxis

**Telekom Architecture Training**

---

# Tag 2 Agenda

- **Strukturmuster Übersicht** - Adapter, Decorator, Facade
- **Erweiterte Strukturmuster** - Composite, Proxy 
- **Enterprise Patterns** - Reale Anwendungsbeispiele
- **Hands-on Übungen** - Praktische Implementierung
- **Best Practices** - Telekom spezifische Anwendungen

**Speaker Notes:** Willkommen zu Tag 2. Heute fokussieren wir uns auf Strukturmuster, die essentiell für Enterprise-Architekturen sind. Wir bauen auf dem Wissen von gestern auf und gehen tiefer in praktische Implementierungen.

---

# Wiederholung: Strukturmuster

Strukturmuster beschäftigen sich mit der Komposition von Klassen und Objekten:

- **Objekte kombinieren** - Funktionalität durch Zusammensetzung
- **Interface Harmonisierung** - Inkompatible Schnittstellen verbinden
- **Komplexität reduzieren** - Einfache Schnittstellen für komplexe Subsysteme
- **Flexibilität erhöhen** - Laufzeit-Komposition statt Vererbung
- **Performance optimieren** - Lazy Loading und Proxy-Mechanismen

**Speaker Notes:** Strukturmuster sind das Herzstück moderner Enterprise-Architekturen. Sie ermöglichen es uns, komplexe Systeme modular und wartbar zu gestalten. Bei Telekom nutzen wir diese Patterns täglich für API-Integration und Microservices.

---

# Adapter Pattern

Das Adapter Pattern ermöglicht die Zusammenarbeit zwischen inkompatiblen Interfaces.

- **Legacy System Integration** - Alte APIs in moderne Architekturen
- **Third-Party Integration** - Externe Services standardisiert nutzen
- **Interface Vereinheitlichung** - Verschiedene APIs unter einem Interface
- **Backward Compatibility** - Alte Schnittstellen weiter unterstützen
- **Protocol Translation** - Verschiedene Protokolle harmonisieren

```typescript
// Telekom Billing System Adapter
interface ModernBillingInterface {
  createInvoice(customer: Customer, amount: number): Invoice;
  getInvoiceStatus(invoiceId: string): BillingStatus;
}

class LegacyBillingSystem {
  // Legacy API mit anderem Interface
  generateBill(custId: number, sum: number): LegacyBill {
    return new LegacyBill(custId, sum, new Date());
  }
  
  checkBillStatus(billNumber: string): number {
    // Returns numeric status codes
    return 1; // 1=pending, 2=paid, 3=overdue
  }
}

class BillingAdapter implements ModernBillingInterface {
  constructor(private legacySystem: LegacyBillingSystem) {}
  
  createInvoice(customer: Customer, amount: number): Invoice {
    const legacyBill = this.legacySystem.generateBill(customer.id, amount);
    
    return new Invoice({
      id: legacyBill.billNumber,
      customerId: customer.id,
      amount: amount,
      date: legacyBill.createdAt,
      status: 'pending'
    });
  }
  
  getInvoiceStatus(invoiceId: string): BillingStatus {
    const statusCode = this.legacySystem.checkBillStatus(invoiceId);
    
    // Convert numeric codes to modern enum
    switch(statusCode) {
      case 1: return BillingStatus.PENDING;
      case 2: return BillingStatus.PAID;
      case 3: return BillingStatus.OVERDUE;
      default: return BillingStatus.UNKNOWN;
    }
  }
}

// Verwendung
const legacySystem = new LegacyBillingSystem();
const modernBilling: ModernBillingInterface = new BillingAdapter(legacySystem);

const invoice = modernBilling.createInvoice(customer, 99.99);
const status = modernBilling.getInvoiceStatus(invoice.id);
```

**Speaker Notes:** Das Adapter Pattern ist bei Telekom besonders wichtig für die Integration von Legacy-Systemen. Wir haben viele bewährte Systeme, die weiterhin funktionieren, aber moderne APIs benötigen. Der Adapter fungiert als Übersetzer zwischen alter und neuer Welt.

---

# Decorator Pattern

Das Decorator Pattern erweitert Objektfunktionalität dynamisch zur Laufzeit.

- **Behavior Extension** - Funktionalität ohne Vererbung hinzufügen
- **Responsibility Chaining** - Multiple Decorator verschachteln
- **Runtime Composition** - Flexibel zur Laufzeit konfigurieren
- **Open-Closed Principle** - Erweiterung ohne Modifikation
- **Cross-Cutting Concerns** - Logging, Security, Caching

```typescript
// Telekom Service Decoration für Cross-Cutting Concerns
interface TelekomService {
  processRequest(request: ServiceRequest): ServiceResponse;
}

class BasicCustomerService implements TelekomService {
  processRequest(request: ServiceRequest): ServiceResponse {
    // Core business logic
    return new ServiceResponse({
      success: true,
      data: `Processed customer request: ${request.type}`,
      timestamp: new Date()
    });
  }
}

// Decorator Base Class
abstract class ServiceDecorator implements TelekomService {
  constructor(protected service: TelekomService) {}
  
  processRequest(request: ServiceRequest): ServiceResponse {
    return this.service.processRequest(request);
  }
}

// Logging Decorator
class LoggingDecorator extends ServiceDecorator {
  processRequest(request: ServiceRequest): ServiceResponse {
    console.log(`[LOG] Processing request: ${request.type} at ${new Date()}`);
    
    const response = super.processRequest(request);
    
    console.log(`[LOG] Request completed: ${response.success ? 'SUCCESS' : 'FAILED'}`);
    return response;
  }
}

// Security Decorator
class SecurityDecorator extends ServiceDecorator {
  processRequest(request: ServiceRequest): ServiceResponse {
    // Validate authentication and authorization
    if (!request.token || !this.validateToken(request.token)) {
      return new ServiceResponse({
        success: false,
        error: 'Unauthorized access',
        timestamp: new Date()
      });
    }
    
    return super.processRequest(request);
  }
  
  private validateToken(token: string): boolean {
    // Telekom token validation logic
    return token.startsWith('telekom_') && token.length > 10;
  }
}

// Performance Monitoring Decorator
class PerformanceDecorator extends ServiceDecorator {
  processRequest(request: ServiceRequest): ServiceResponse {
    const startTime = performance.now();
    
    const response = super.processRequest(request);
    
    const executionTime = performance.now() - startTime;
    console.log(`[PERF] Request executed in ${executionTime.toFixed(2)}ms`);
    
    // Add performance metrics to response
    response.metadata = { executionTime };
    return response;
  }
}

// Usage - Flexible composition
let customerService: TelekomService = new BasicCustomerService();

// Add logging
customerService = new LoggingDecorator(customerService);

// Add security
customerService = new SecurityDecorator(customerService);

// Add performance monitoring
customerService = new PerformanceDecorator(customerService);

// Process request with all decorators
const response = customerService.processRequest({
  type: 'UPDATE_CUSTOMER_DATA',
  token: 'telekom_abc123456789',
  customerId: '12345'
});
```

**Speaker Notes:** Das Decorator Pattern ist essentiell für Enterprise-Services. Bei Telekom verwenden wir es für Logging, Security, Performance Monitoring und Caching. Es ermöglicht uns, Cross-Cutting Concerns sauber zu trennen und flexibel zu kombinieren.

---

# Facade Pattern

Das Facade Pattern bietet eine vereinfachte Schnittstelle zu komplexen Subsystemen.

- **Complexity Hiding** - Einfache API für komplexe Operationen
- **Subsystem Coordination** - Multiple Services orchestrieren
- **Client Decoupling** - Clients vom Subsystem entkoppeln
- **API Standardization** - Einheitliche Schnittstellen schaffen
- **Integration Layer** - Microservices zusammenfassen

```typescript
// Telekom Customer Management Facade
class TelekomCustomerFacade {
  constructor(
    private customerService: CustomerService,
    private billingService: BillingService,
    private contractService: ContractService,
    private notificationService: NotificationService,
    private auditService: AuditService
  ) {}
  
  // Vereinfachte API für komplexe Customer Onboarding
  async onboardNewCustomer(customerData: CustomerData): Promise<OnboardingResult> {
    try {
      // 1. Create customer record
      const customer = await this.customerService.create({
        name: customerData.name,
        email: customerData.email,
        phone: customerData.phone,
        address: customerData.address
      });
      
      // 2. Set up billing account
      const billingAccount = await this.billingService.createAccount({
        customerId: customer.id,
        paymentMethod: customerData.paymentMethod,
        billingAddress: customerData.billingAddress
      });
      
      // 3. Create initial contract
      const contract = await this.contractService.create({
        customerId: customer.id,
        planType: customerData.selectedPlan,
        startDate: new Date(),
        billingAccountId: billingAccount.id
      });
      
      // 4. Send welcome notification
      await this.notificationService.sendWelcomeEmail({
        customerEmail: customer.email,
        customerName: customer.name,
        contractDetails: contract
      });
      
      // 5. Log audit trail
      await this.auditService.logCustomerAction({
        action: 'CUSTOMER_ONBOARDED',
        customerId: customer.id,
        timestamp: new Date(),
        details: { contractId: contract.id, plan: customerData.selectedPlan }
      });
      
      return new OnboardingResult({
        success: true,
        customerId: customer.id,
        contractId: contract.id,
        message: 'Customer successfully onboarded'
      });
      
    } catch (error) {
      // Comprehensive error handling
      await this.auditService.logError({
        action: 'CUSTOMER_ONBOARDING_FAILED',
        error: error.message,
        customerData: customerData.email // Safe logging
      });
      
      return new OnboardingResult({
        success: false,
        error: 'Onboarding process failed',
        details: error.message
      });
    }
  }
  
  // Vereinfachte API für Customer Data Update
  async updateCustomerProfile(customerId: string, updates: CustomerUpdates): Promise<UpdateResult> {
    const tasks: Promise<any>[] = [];
    
    // Update customer basic data
    if (updates.personalInfo) {
      tasks.push(this.customerService.updateBasicInfo(customerId, updates.personalInfo));
    }
    
    // Update billing information
    if (updates.billingInfo) {
      tasks.push(this.billingService.updateBillingInfo(customerId, updates.billingInfo));
    }
    
    // Update contract if needed
    if (updates.contractChanges) {
      tasks.push(this.contractService.amendContract(customerId, updates.contractChanges));
    }
    
    // Execute all updates in parallel
    try {
      await Promise.all(tasks);
      
      // Send notification about changes
      await this.notificationService.sendUpdateConfirmation(customerId);
      
      // Log success
      await this.auditService.logCustomerAction({
        action: 'CUSTOMER_PROFILE_UPDATED',
        customerId: customerId,
        timestamp: new Date()
      });
      
      return { success: true, message: 'Profile updated successfully' };
      
    } catch (error) {
      return { success: false, error: 'Profile update failed' };
    }
  }
}

// Simple usage for complex operations
const customerFacade = new TelekomCustomerFacade(
  customerService, billingService, contractService, 
  notificationService, auditService
);

// Complex onboarding with single method call
const result = await customerFacade.onboardNewCustomer({
  name: 'Max Mustermann',
  email: 'max@example.com',
  phone: '+49 123 456789',
  selectedPlan: 'BUSINESS_PRO',
  paymentMethod: 'SEPA'
});
```

**Speaker Notes:** Das Facade Pattern ist bei Telekom unverzichtbar für die Vereinfachung komplexer Geschäftsprozesse. Customer Onboarding involviert normalerweise 5-6 verschiedene Services. Die Facade versteckt diese Komplexität und bietet eine einfache, fehlerresistente API.

---

# Composite Pattern

Das Composite Pattern behandelt einzelne Objekte und Objektgruppen einheitlich.

- **Hierarchical Structures** - Baumstrukturen elegant handhaben
- **Uniform Interface** - Gleiche Operationen für Blätter und Äste
- **Recursive Operations** - Operationen propagieren automatisch
- **Dynamic Composition** - Strukturen zur Laufzeit ändern
- **Organizational Modeling** - Unternehmen, Teams, Projekte

```typescript
// Telekom Organizational Structure mit Composite Pattern
interface OrganizationalUnit {
  getName(): string;
  getEmployeeCount(): number;
  getBudget(): number;
  addUnit(unit: OrganizationalUnit): void;
  removeUnit(unit: OrganizationalUnit): void;
  generateReport(): OrganizationalReport;
}

class Employee implements OrganizationalUnit {
  constructor(
    private name: string,
    private salary: number,
    private position: string
  ) {}
  
  getName(): string {
    return this.name;
  }
  
  getEmployeeCount(): number {
    return 1; // An employee counts as 1
  }
  
  getBudget(): number {
    return this.salary;
  }
  
  addUnit(unit: OrganizationalUnit): void {
    throw new Error('Cannot add units to an employee');
  }
  
  removeUnit(unit: OrganizationalUnit): void {
    throw new Error('Cannot remove units from an employee');
  }
  
  generateReport(): OrganizationalReport {
    return {
      name: this.name,
      type: 'EMPLOYEE',
      employeeCount: 1,
      budget: this.salary,
      position: this.position,
      children: []
    };
  }
}

class Department implements OrganizationalUnit {
  private units: OrganizationalUnit[] = [];
  
  constructor(
    private name: string,
    private operationalBudget: number = 0
  ) {}
  
  getName(): string {
    return this.name;
  }
  
  getEmployeeCount(): number {
    return this.units.reduce((total, unit) => {
      return total + unit.getEmployeeCount();
    }, 0);
  }
  
  getBudget(): number {
    const personnelBudget = this.units.reduce((total, unit) => {
      return total + unit.getBudget();
    }, 0);
    
    return personnelBudget + this.operationalBudget;
  }
  
  addUnit(unit: OrganizationalUnit): void {
    this.units.push(unit);
  }
  
  removeUnit(unit: OrganizationalUnit): void {
    const index = this.units.indexOf(unit);
    if (index > -1) {
      this.units.splice(index, 1);
    }
  }
  
  generateReport(): OrganizationalReport {
    const childReports = this.units.map(unit => unit.generateReport());
    
    return {
      name: this.name,
      type: 'DEPARTMENT',
      employeeCount: this.getEmployeeCount(),
      budget: this.getBudget(),
      operationalBudget: this.operationalBudget,
      children: childReports
    };
  }
}

// Telekom organizational structure example
const telekom = new Department('Deutsche Telekom AG', 1000000);

// Technology Division
const technologyDiv = new Department('Technology', 500000);
const cloudTeam = new Department('Cloud Services', 50000);
const securityTeam = new Department('IT Security', 75000);

// Add employees to teams
cloudTeam.addUnit(new Employee('Anna Schmidt', 85000, 'Cloud Architect'));
cloudTeam.addUnit(new Employee('Peter Wagner', 75000, 'DevOps Engineer'));
cloudTeam.addUnit(new Employee('Lisa Chen', 80000, 'Cloud Developer'));

securityTeam.addUnit(new Employee('Michael Bauer', 90000, 'Security Architect'));
securityTeam.addUnit(new Employee('Sarah Müller', 82000, 'Security Engineer'));

// Build hierarchy
technologyDiv.addUnit(cloudTeam);
technologyDiv.addUnit(securityTeam);
telekom.addUnit(technologyDiv);

// Uniform operations on the entire structure
console.log(`Total employees: ${telekom.getEmployeeCount()}`);
console.log(`Total budget: €${telekom.getBudget().toLocaleString()}`);

// Generate comprehensive report
const report = telekom.generateReport();
console.log(JSON.stringify(report, null, 2));

// Dynamic restructuring
const newInnovationLab = new Department('Innovation Lab', 25000);
newInnovationLab.addUnit(new Employee('Dr. Frank Weber', 95000, 'Innovation Lead'));

technologyDiv.addUnit(newInnovationLab);
```

**Speaker Notes:** Das Composite Pattern ist perfekt für Telekom's komplexe Organisationsstruktur. Wir können einheitlich auf Mitarbeiter, Teams und ganze Divisionen zugreifen. Egal ob wir das Budget eines einzelnen Mitarbeiters oder der gesamten Technologie-Sparte berechnen - das Interface bleibt gleich.

---

# Proxy Pattern

Das Proxy Pattern fungiert als Stellvertreter und kontrolliert Zugriff auf andere Objekte.

- **Access Control** - Sicherheitsproxy für geschützte Ressourcen
- **Lazy Loading** - Objekte erst bei Bedarf laden
- **Caching** - Wiederholte Anfragen zwischenspeichern
- **Remote Access** - Entfernte Objekte lokal repräsentieren
- **Resource Management** - Teure Operationen optimieren

```typescript
// Telekom Data Access Proxy mit Caching und Security
interface TelekomDataService {
  getCustomerData(customerId: string): Promise<CustomerData>;
  updateCustomerData(customerId: string, data: Partial<CustomerData>): Promise<boolean>;
  getUsageStatistics(customerId: string, period: DateRange): Promise<UsageStats>;
}

class RealTelekomDataService implements TelekomDataService {
  async getCustomerData(customerId: string): Promise<CustomerData> {
    // Simulate expensive database call
    console.log(`[DB] Fetching customer data for ${customerId}`);
    await this.delay(500); // Simulate network delay
    
    return {
      id: customerId,
      name: 'Max Mustermann',
      email: 'max@telekom.de',
      contract: 'BUSINESS_PRO',
      lastLogin: new Date(),
      dataUsage: 15.5
    };
  }
  
  async updateCustomerData(customerId: string, data: Partial<CustomerData>): Promise<boolean> {
    console.log(`[DB] Updating customer data for ${customerId}`);
    await this.delay(300);
    return true;
  }
  
  async getUsageStatistics(customerId: string, period: DateRange): Promise<UsageStats> {
    console.log(`[DB] Fetching usage statistics for ${customerId}`);
    await this.delay(800); // Heavy analytics query
    
    return {
      customerId,
      period,
      dataUsed: 45.2,
      callMinutes: 890,
      smsCount: 156,
      costs: 89.95
    };
  }
  
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}

class TelekomDataServiceProxy implements TelekomDataService {
  private realService: RealTelekomDataService;
  private cache = new Map<string, { data: any; timestamp: number }>();
  private readonly CACHE_TTL = 5 * 60 * 1000; // 5 minutes
  
  constructor(private authService: AuthenticationService) {
    this.realService = new RealTelekomDataService();
  }
  
  async getCustomerData(customerId: string): Promise<CustomerData> {
    // Security check
    if (!await this.checkAccess(customerId)) {
      throw new Error('Unauthorized access to customer data');
    }
    
    // Check cache first
    const cacheKey = `customer_${customerId}`;
    const cached = this.getCachedData(cacheKey);
    if (cached) {
      console.log(`[CACHE] Returning cached customer data for ${customerId}`);
      return cached;
    }
    
    // Fetch from real service
    const data = await this.realService.getCustomerData(customerId);
    this.setCachedData(cacheKey, data);
    
    return data;
  }
  
  async updateCustomerData(customerId: string, data: Partial<CustomerData>): Promise<boolean> {
    // Security check
    if (!await this.checkAccess(customerId)) {
      throw new Error('Unauthorized access to customer data');
    }
    
    // Update via real service
    const result = await this.realService.updateCustomerData(customerId, data);
    
    // Invalidate cache
    this.invalidateCustomerCache(customerId);
    
    return result;
  }
  
  async getUsageStatistics(customerId: string, period: DateRange): Promise<UsageStats> {
    // Security check
    if (!await this.checkAccess(customerId)) {
      throw new Error('Unauthorized access to usage statistics');
    }
    
    // Check cache for expensive analytics
    const cacheKey = `usage_${customerId}_${period.start}_${period.end}`;
    const cached = this.getCachedData(cacheKey);
    if (cached) {
      console.log(`[CACHE] Returning cached usage statistics for ${customerId}`);
      return cached;
    }
    
    // Fetch from real service
    const stats = await this.realService.getUsageStatistics(customerId, period);
    this.setCachedData(cacheKey, stats);
    
    return stats;
  }
  
  private async checkAccess(customerId: string): Promise<boolean> {
    // Simulate authentication and authorization check
    const currentUser = await this.authService.getCurrentUser();
    
    // Check if user has access to this customer data
    if (currentUser.role === 'ADMIN') {
      return true;
    }
    
    if (currentUser.role === 'SUPPORT' && currentUser.customerId === customerId) {
      return true;
    }
    
    if (currentUser.customerId === customerId) {
      return true;
    }
    
    console.log(`[SECURITY] Access denied for user ${currentUser.id} to customer ${customerId}`);
    return false;
  }
  
  private getCachedData(key: string): any {
    const cached = this.cache.get(key);
    if (cached && (Date.now() - cached.timestamp < this.CACHE_TTL)) {
      return cached.data;
    }
    return null;
  }
  
  private setCachedData(key: string, data: any): void {
    this.cache.set(key, {
      data: data,
      timestamp: Date.now()
    });
  }
  
  private invalidateCustomerCache(customerId: string): void {
    const keysToDelete: string[] = [];
    for (const key of this.cache.keys()) {
      if (key.includes(customerId)) {
        keysToDelete.push(key);
      }
    }
    keysToDelete.forEach(key => this.cache.delete(key));
  }
}

// Usage
const authService = new AuthenticationService();
const dataService: TelekomDataService = new TelekomDataServiceProxy(authService);

// First call - hits database
const customer1 = await dataService.getCustomerData('123456');

// Second call - returns from cache
const customer2 = await dataService.getCustomerData('123456');

// Unauthorized call - throws security exception
try {
  await dataService.getCustomerData('unauthorized_customer');
} catch (error) {
  console.log('Security proxy blocked unauthorized access');
}
```

**Speaker Notes:** Das Proxy Pattern ist bei Telekom kritisch für Performance und Security. Wir nutzen es für Datenschutz (Customer Data Protection), Caching teurer Analytics-Queries und für Remote Service Access. Der Proxy fungiert als intelligenter Gateway zwischen Client und Service.

---

# Day 2 Hands-on Übung

**Aufgabe: Telekom Service Integration Platform**

Implementieren Sie eine Service Integration Platform mit allen gelernten Structural Patterns:

## Anforderungen:
- **Adapter** - Legacy billing system integration
- **Decorator** - Add logging, security, monitoring
- **Facade** - Unified customer service API
- **Composite** - Service hierarchy management
- **Proxy** - Caching and access control

## Basis Code Structure:

```typescript
// Your task: Implement missing patterns
interface ServicePlatform {
  // Define your unified interface
}

class LegacyBillingSystem {
  generateBill(custId: number, amount: number): LegacyBill { /* ... */ }
}

class CustomerServiceFacade {
  // Implement facade for complex operations
}

// Implement all patterns step by step
```


**Speaker Notes:** Diese Übung kombiniert alle heute gelernten Patterns in einem realistischen Telekom-Szenario. Die Teilnehmer sollen eine echte Integration Platform bauen, wie wir sie täglich verwenden. Betonen Sie die praktische Anwendbarkeit und helfen Sie bei Design-Entscheidungen.

---

# Structural Patterns Best Practices

## Telekom Specific Guidelines:

- **Security First** - Alle Patterns mit Security Decorators erweitern
- **Performance Aware** - Proxy Pattern für teure Operations
- **Legacy Integration** - Adapter Pattern für Bestandssysteme
- **API Standardization** - Facade Pattern für einheitliche APIs
- **Monitoring Integration** - Decorator für comprehensive logging

## Häufige Fallstricke:

- **Over-Engineering** - Nicht jede Klasse braucht einen Proxy
- **Cache Invalidation** - Proxy Caches müssen intelligent invalidiert werden
- **Deep Nesting** - Decorator chains nicht zu tief verschachteln
- **Interface Bloat** - Facades nicht mit zu vielen Methoden überladen

**Speaker Notes:** Diese Best Practices basieren auf realen Telekom-Projekten. Teilen Sie konkrete Beispiele wo möglich und warnen Sie vor den typischen Fehlern, die wir selbst gemacht haben.

---

# Tag 2 Zusammenfassung

## Was haben wir gelernt:

- **Adapter Pattern** - Legacy Integration mit modernen APIs
- **Decorator Pattern** - Flexible Cross-Cutting Concerns
- **Facade Pattern** - Vereinfachung komplexer Subsysteme
- **Composite Pattern** - Hierarchische Strukturen elegant handhaben
- **Proxy Pattern** - Intelligente Zugriffskontrolle und Caching

## Morgen - Tag 3:

- **Behavioral Patterns** - Observer, Strategy, Command
- **Enterprise Integration** - Message Queues und Event-Driven Architecture
- **Microservices Patterns** - Service Mesh und API Gateway
- **Advanced Topics** - CQRS, Event Sourcing

**Speaker Notes:** Fassen Sie die wichtigsten Erkenntnisse des Tages zusammen. Betonen Sie, wie diese Patterns bei Telekom täglich verwendet werden. Machen Sie die Teilnehmer neugierig auf Tag 3 - Behavioral Patterns sind noch spannender und näher an der Geschäftslogik.

---

# Q&A und Diskussion

**Fragen zu den Structural Patterns?**

- Welche Patterns verwenden Sie bereits?
- Wo sehen Sie Anwendungsmöglichkeiten in Ihren Projekten?
- Welche Herausforderungen haben Sie bei der Implementation?

**Kontakt für weitere Fragen:**
- **Email:** architecture-training@telekom.de
- **Teams:** Telekom Architecture Community
- **Wiki:** Internal Pattern Documentation

**Speaker Notes:** Ermutigen Sie zur offenen Diskussion. Viele Teilnehmer haben bereits unbewusst diese Patterns verwendet. Helfen Sie dabei, die Verbindung zwischen Theorie und ihrer täglichen Arbeit herzustellen. Sammeln Sie Feedback für Verbesserungen des Trainings.