# Day 3: Behavioral Patterns - Hands-On Exercises

## Learning Objectives
- Master algorithm encapsulation and responsibility distribution
- Apply Strategy, Template Method, Observer, Command, State, and Chain of Responsibility patterns
- Design flexible workflow and process management systems  
- Practice event-driven architecture and notification systems

---

## Exercise 1: Dynamic Pricing Strategy Engine 💰
**Pattern Focus:** Strategy + Template Method | **Duration:** 25 minutes | **Type:** Algorithm Design

### Scenario
Telekom's pricing system needs to support multiple pricing strategies that change based on market conditions, customer segments, promotional periods, and competitive pressures. The system must be able to switch pricing algorithms dynamically without affecting the rest of the application.

### Current Problem
```java
public class PricingService {
    public double calculatePrice(Customer customer, Service service, LocalDateTime timestamp) {
        double basePrice = service.getBasePrice();
        
        // Hard-coded pricing logic - difficult to change or extend
        if (customer.getSegment() == CustomerSegment.PREMIUM) {
            if (service.getType() == ServiceType.MOBILE) {
                if (isPromotionalPeriod(timestamp)) {
                    return basePrice * 0.8; // 20% promotional discount
                } else if (customer.getLoyaltyYears() > 5) {
                    return basePrice * 0.9; // 10% loyalty discount
                } else {
                    return basePrice * 0.95; // 5% premium customer discount
                }
            } else if (service.getType() == ServiceType.DSL) {
                return basePrice * 0.85; // Premium DSL discount
            }
        } else if (customer.getSegment() == CustomerSegment.BUSINESS) {
            double businessPrice = basePrice;
            if (customer.getContractVolume() > 1000000) {
                businessPrice *= 0.75; // Volume discount
            } else if (customer.getContractVolume() > 500000) {
                businessPrice *= 0.85; // Medium volume discount
            }
            if (service.getType() == ServiceType.FIBER && 
                service.getBandwidth() > 1000) {
                businessPrice *= 0.9; // High bandwidth discount
            }
            return businessPrice;
        } else {
            // Standard customer pricing
            if (isNewCustomer(customer)) {
                return basePrice * 0.95; // New customer discount
            } else if (isLoyalCustomer(customer)) {
                return basePrice * 0.92; // Loyalty discount
            }
        }
        
        return basePrice;
    }
}
```

### Problems Identified
- Multiple nested conditionals make code hard to understand
- Adding new pricing strategies requires modifying existing code
- Impossible to A/B test different pricing algorithms
- Business rules scattered throughout the method
- Difficult to unit test individual pricing strategies

### Tasks

#### Part A: Strategy Pattern Design (10 minutes)
Design a Strategy pattern that encapsulates different pricing algorithms and makes them interchangeable.

**Required Components:**
```java
// Strategy interface
public interface PricingStrategy {
    double calculatePrice(PricingContext context);
    String getStrategyName();
    boolean isApplicable(PricingContext context);
}

// Context object containing all pricing inputs
public class PricingContext {
    private Customer customer;
    private Service service;
    private LocalDateTime timestamp;
    private MarketConditions marketConditions;
    
    // TODO: Add getters, builder pattern
}

// Strategy implementations
public class PremiumCustomerPricingStrategy implements PricingStrategy {
    // TODO: Implement premium customer pricing logic
}

public class BusinessVolumePricingStrategy implements PricingStrategy {
    // TODO: Implement business volume pricing logic
}

public class PromotionalPricingStrategy implements PricingStrategy {
    // TODO: Implement promotional pricing logic
}

// Context class using strategies
public class PricingEngine {
    private List<PricingStrategy> strategies;
    
    public PricingEngine(List<PricingStrategy> strategies) {
        this.strategies = strategies;
    }
    
    public double calculatePrice(PricingContext context) {
        // TODO: Select and apply appropriate strategy
    }
}
```

#### Part B: Template Method Integration (10 minutes)
Many pricing strategies share common steps but differ in specific calculations. Implement a Template Method pattern that standardizes the pricing process.

**Requirements:**
- Base price calculation (common)
- Customer segment analysis (varies)
- Service-specific adjustments (varies)  
- Promotional period checks (varies)
- Final price validation (common)

```java
public abstract class AbstractPricingStrategy implements PricingStrategy {
    
    // Template method defining the pricing algorithm structure
    @Override
    public final double calculatePrice(PricingContext context) {
        double basePrice = getBasePrice(context);
        double customerAdjustment = calculateCustomerAdjustment(context);
        double serviceAdjustment = calculateServiceAdjustment(context);
        double promotionalAdjustment = calculatePromotionalAdjustment(context);
        double finalPrice = applyAdjustments(basePrice, customerAdjustment, serviceAdjustment, promotionalAdjustment);
        
        return validatePrice(finalPrice, context);
    }
    
    // Common implementations
    private double getBasePrice(PricingContext context) {
        return context.getService().getBasePrice();
    }
    
    private double applyAdjustments(double base, double... adjustments) {
        double result = base;
        for (double adjustment : adjustments) {
            result *= (1.0 + adjustment);
        }
        return result;
    }
    
    private double validatePrice(double price, PricingContext context) {
        // Common validation logic
        if (price < 0) throw new IllegalStateException("Negative price calculated");
        if (price > context.getService().getMaxPrice()) {
            return context.getService().getMaxPrice();
        }
        return price;
    }
    
    // Abstract methods - subclasses implement specific behavior
    protected abstract double calculateCustomerAdjustment(PricingContext context);
    protected abstract double calculateServiceAdjustment(PricingContext context);
    protected abstract double calculatePromotionalAdjustment(PricingContext context);
}
```

#### Part C: Dynamic Strategy Selection (5 minutes)
Implement a strategy selector that chooses the best pricing strategy based on context:

```java
public class DynamicPricingEngine {
    private List<PricingStrategy> strategies;
    
    public double calculatePrice(PricingContext context) {
        // Find all applicable strategies
        List<PricingStrategy> applicableStrategies = strategies.stream()
            .filter(strategy -> strategy.isApplicable(context))
            .collect(Collectors.toList());
            
        if (applicableStrategies.isEmpty()) {
            throw new NoPricingStrategyException("No applicable pricing strategy found");
        }
        
        // For this exercise: select first applicable strategy
        // In real systems: might choose best price for customer, or use A/B testing
        return applicableStrategies.get(0).calculatePrice(context);
    }
}
```

### Test Your Implementation
```java
// Test dynamic strategy selection
PricingContext context = PricingContext.builder()
    .customer(premiumCustomer)
    .service(mobileService)
    .timestamp(LocalDateTime.now())
    .build();

double price = pricingEngine.calculatePrice(context);

// Should select PremiumCustomerPricingStrategy and apply appropriate discounts
assertTrue(price < mobileService.getBasePrice()); // Some discount applied
```

### Discussion Points
- How do Strategy and Template Method patterns complement each other?
- When would you choose Strategy vs. simple if-else statements?
- How would you handle A/B testing different pricing strategies?
- What are the performance implications of dynamic strategy selection?

---

## Exercise 2: Event-Driven Network Monitoring 📊
**Pattern Focus:** Observer + Chain of Responsibility | **Duration:** 30 minutes | **Type:** Event Architecture

### Scenario
Telekom's network monitoring system needs to handle various types of network events (outages, performance degradation, security alerts) and notify appropriate stakeholders. Different handlers process events at different levels, and multiple observers need to be notified of important events.

### Part A: Observer Pattern for Event Notifications (15 minutes)

**Challenge:** Design an Observer pattern that handles network event notifications with different priority levels and observer types.

```java
// Event model
public class NetworkEvent {
    private String eventId;
    private EventType type;
    private EventSeverity severity;
    private String networkComponent;
    private String description;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    
    // TODO: Add constructor, getters, builder
}

public enum EventType {
    OUTAGE, PERFORMANCE_DEGRADATION, SECURITY_ALERT, 
    CAPACITY_WARNING, HARDWARE_FAILURE, SOFTWARE_ERROR
}

public enum EventSeverity {
    CRITICAL, HIGH, MEDIUM, LOW, INFO
}

// Observer interface
public interface NetworkEventObserver {
    void handleEvent(NetworkEvent event);
    boolean isInterestedIn(NetworkEvent event);
    String getObserverName();
}

// Subject interface
public interface NetworkEventSubject {
    void addObserver(NetworkEventObserver observer);
    void removeObserver(NetworkEventObserver observer);
    void notifyObservers(NetworkEvent event);
}
```

**Requirements:**
- **Multiple observer types:** Email notifications, SMS alerts, Dashboard updates, Log writers, Ticket creators
- **Selective notification:** Observers should only receive events they're interested in
- **Prioritized notification:** Critical events should be processed first
- **Asynchronous processing:** High-volume events shouldn't block the system

**Observer Implementations to Create:**
```java
public class EmailNotificationObserver implements NetworkEventObserver {
    private List<String> emailAddresses;
    private Set<EventSeverity> subscribedSeverities;
    
    // TODO: Implement email notification for subscribed severities
}

public class SMSAlertObserver implements NetworkEventObserver {
    private List<String> phoneNumbers;
    
    @Override
    public boolean isInterestedIn(NetworkEvent event) {
        // Only interested in CRITICAL events
        return event.getSeverity() == EventSeverity.CRITICAL;
    }
    
    // TODO: Implement SMS alert logic
}

public class DashboardUpdateObserver implements NetworkEventObserver {
    private DashboardService dashboardService;
    
    // TODO: Implement real-time dashboard updates
}

public class TicketCreationObserver implements NetworkEventObserver {
    private TicketingSystem ticketingSystem;
    
    @Override
    public boolean isInterestedIn(NetworkEvent event) {
        // Create tickets for CRITICAL and HIGH severity events
        return event.getSeverity() == EventSeverity.CRITICAL || 
               event.getSeverity() == EventSeverity.HIGH;
    }
    
    // TODO: Implement automatic ticket creation
}
```

#### Part B: Chain of Responsibility for Event Processing (15 minutes)

**Challenge:** Different types of network events need different processing pipelines. Some events can be auto-resolved, others need escalation, and some require immediate human intervention.

**Processing Pipeline Requirements:**
- **Auto-Resolution Handler:** Try to automatically resolve common issues
- **Escalation Handler:** Determine if event needs escalation
- **Notification Handler:** Send appropriate notifications
- **Logging Handler:** Ensure all events are properly logged
- **Audit Handler:** Compliance and audit trail

```java
// Handler interface
public abstract class NetworkEventHandler {
    protected NetworkEventHandler nextHandler;
    
    public void setNext(NetworkEventHandler handler) {
        this.nextHandler = handler;
    }
    
    public final void handle(NetworkEvent event) {
        if (canHandle(event)) {
            processEvent(event);
        }
        
        // Always continue the chain (unless event is marked as handled)
        if (nextHandler != null && !event.isHandled()) {
            nextHandler.handle(event);
        }
    }
    
    protected abstract boolean canHandle(NetworkEvent event);
    protected abstract void processEvent(NetworkEvent event);
}

// Concrete handlers
public class AutoResolutionHandler extends NetworkEventHandler {
    private AutoResolutionService autoResolutionService;
    
    @Override
    protected boolean canHandle(NetworkEvent event) {
        // Can handle performance degradation and capacity warnings
        return event.getType() == EventType.PERFORMANCE_DEGRADATION ||
               event.getType() == EventType.CAPACITY_WARNING;
    }
    
    @Override
    protected void processEvent(NetworkEvent event) {
        boolean resolved = autoResolutionService.attemptAutoResolution(event);
        if (resolved) {
            event.markAsHandled();
            event.addMetadata("resolution", "AUTO_RESOLVED");
        }
    }
}

public class EscalationHandler extends NetworkEventHandler {
    private EscalationService escalationService;
    
    @Override
    protected boolean canHandle(NetworkEvent event) {
        // Handle critical events or events not auto-resolved
        return event.getSeverity() == EventSeverity.CRITICAL || 
               !event.isHandled();
    }
    
    @Override
    protected void processEvent(NetworkEvent event) {
        EscalationLevel level = determineEscalationLevel(event);
        escalationService.escalate(event, level);
        event.addMetadata("escalated_to", level.toString());
    }
    
    private EscalationLevel determineEscalationLevel(NetworkEvent event) {
        // TODO: Implement escalation logic
        return EscalationLevel.LEVEL_1;
    }
}

// TODO: Implement NotificationHandler, LoggingHandler, AuditHandler
```

### Integration Challenge
Combine Observer and Chain of Responsibility patterns in a unified event processing system:

```java
public class NetworkMonitoringSystem implements NetworkEventSubject {
    private List<NetworkEventObserver> observers = new CopyOnWriteArrayList<>();
    private NetworkEventHandler processingChain;
    
    public NetworkMonitoringSystem() {
        setupProcessingChain();
    }
    
    private void setupProcessingChain() {
        // Build the processing chain
        NetworkEventHandler autoResolver = new AutoResolutionHandler();
        NetworkEventHandler escalationHandler = new EscalationHandler();  
        NetworkEventHandler notificationHandler = new NotificationHandler();
        NetworkEventHandler loggingHandler = new LoggingHandler();
        NetworkEventHandler auditHandler = new AuditHandler();
        
        // Chain them together
        autoResolver.setNext(escalationHandler);
        escalationHandler.setNext(notificationHandler);
        notificationHandler.setNext(loggingHandler);
        loggingHandler.setNext(auditHandler);
        
        this.processingChain = autoResolver;
    }
    
    public void reportEvent(NetworkEvent event) {
        // First process through chain of responsibility
        processingChain.handle(event);
        
        // Then notify all interested observers
        notifyObservers(event);
    }
    
    // TODO: Implement NetworkEventSubject methods
}
```

### Test Scenarios
```java
// Test auto-resolution
NetworkEvent performanceEvent = NetworkEvent.builder()
    .type(EventType.PERFORMANCE_DEGRADATION)
    .severity(EventSeverity.MEDIUM)
    .networkComponent("ROUTER-001")
    .build();

monitoringSystem.reportEvent(performanceEvent);
// Should be auto-resolved and not escalated

// Test critical event escalation
NetworkEvent criticalEvent = NetworkEvent.builder()
    .type(EventType.OUTAGE)
    .severity(EventSeverity.CRITICAL)
    .networkComponent("CORE-SWITCH-01")
    .build();

monitoringSystem.reportEvent(criticalEvent);
// Should trigger SMS alerts, create tickets, and escalate
```

---

## Exercise 3: Service Order State Machine 🔄
**Pattern Focus:** State + Command | **Duration:** 30 minutes | **Type:** Workflow Management

### Scenario
Telekom service orders go through complex state transitions with different actions available in each state. The system needs to handle order processing workflows that vary by service type while maintaining strict state transition rules.

### Part A: State Pattern for Order Lifecycle (18 minutes)

**Challenge:** Implement a State pattern for service order management with proper state transition validation.

**Order States:**
- **DRAFT:** Initial state, can edit freely
- **SUBMITTED:** Waiting for validation
- **VALIDATED:** Passed validation checks
- **APPROVED:** Management approved  
- **SCHEDULED:** Installation/activation scheduled
- **IN_PROGRESS:** Work being performed
- **COMPLETED:** Successfully completed
- **REJECTED:** Rejected at any point
- **CANCELLED:** Cancelled by customer

```java
// State interface
public interface OrderState {
    void submit(ServiceOrder order);
    void validate(ServiceOrder order);
    void approve(ServiceOrder order); 
    void schedule(ServiceOrder order);
    void startWork(ServiceOrder order);
    void complete(ServiceOrder order);
    void reject(ServiceOrder order, String reason);
    void cancel(ServiceOrder order, String reason);
    
    String getStateName();
    Set<String> getAllowedTransitions();
}

// Service Order context
public class ServiceOrder {
    private String orderId;
    private String customerId;
    private ServiceType serviceType;
    private OrderState currentState;
    private List<String> stateHistory;
    private Map<String, Object> orderData;
    private String rejectionReason;
    private String cancellationReason;
    
    public ServiceOrder(String orderId, String customerId, ServiceType serviceType) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.serviceType = serviceType;
        this.currentState = new DraftState();
        this.stateHistory = new ArrayList<>();
        this.orderData = new HashMap<>();
    }
    
    // Delegate methods to current state
    public void submit() {
        currentState.submit(this);
    }
    
    public void validate() {
        currentState.validate(this);
    }
    
    // TODO: Implement other delegation methods
    
    // State transition method
    public void setState(OrderState newState) {
        if (currentState != null) {
            stateHistory.add(currentState.getStateName());
        }
        this.currentState = newState;
    }
    
    // TODO: Add getters, setters
}

// Example state implementations
public class DraftState implements OrderState {
    @Override
    public void submit(ServiceOrder order) {
        // Validate order data before submission
        validateOrderData(order);
        order.setState(new SubmittedState());
    }
    
    @Override
    public void validate(ServiceOrder order) {
        throw new IllegalStateException("Cannot validate order in DRAFT state");
    }
    
    @Override
    public void cancel(ServiceOrder order, String reason) {
        order.setCancellationReason(reason);
        order.setState(new CancelledState());
    }
    
    // TODO: Implement other methods (throw IllegalStateException for invalid transitions)
    
    private void validateOrderData(ServiceOrder order) {
        // TODO: Implement validation logic
    }
}

public class SubmittedState implements OrderState {
    @Override
    public void validate(ServiceOrder order) {
        // Perform validation checks
        ValidationResult result = performValidation(order);
        if (result.isValid()) {
            order.setState(new ValidatedState());
        } else {
            order.setState(new RejectedState());
            order.setRejectionReason(result.getErrorMessage());
        }
    }
    
    @Override
    public void reject(ServiceOrder order, String reason) {
        order.setRejectionReason(reason);
        order.setState(new RejectedState());
    }
    
    // TODO: Implement other methods
    
    private ValidationResult performValidation(ServiceOrder order) {
        // TODO: Implement validation logic
        return new ValidationResult(true, null);
    }
}

// TODO: Implement remaining states (ValidatedState, ApprovedState, etc.)
```

#### Part B: Command Pattern for Order Actions (12 minutes)

**Challenge:** Different actions can be performed on orders, and these actions need to be logged, undoable, and possibly queued for batch processing.

```java
// Command interface
public interface OrderCommand {
    void execute();
    void undo();
    String getCommandName();
    String getOrderId();
    LocalDateTime getExecutionTime();
}

// Abstract command implementation
public abstract class AbstractOrderCommand implements OrderCommand {
    protected ServiceOrder order;
    protected LocalDateTime executionTime;
    
    public AbstractOrderCommand(ServiceOrder order) {
        this.order = order;
    }
    
    @Override
    public final void execute() {
        this.executionTime = LocalDateTime.now();
        doExecute();
        logExecution();
    }
    
    protected abstract void doExecute();
    protected abstract void doUndo();
    
    @Override
    public void undo() {
        doUndo();
        logUndo();
    }
    
    private void logExecution() {
        System.out.println("Executed " + getCommandName() + " on order " + order.getOrderId());
    }
    
    private void logUndo() {
        System.out.println("Undid " + getCommandName() + " on order " + order.getOrderId());
    }
    
    @Override
    public String getOrderId() {
        return order.getOrderId();
    }
    
    @Override
    public LocalDateTime getExecutionTime() {
        return executionTime;
    }
}

// Concrete command implementations
public class SubmitOrderCommand extends AbstractOrderCommand {
    private OrderState previousState;
    
    public SubmitOrderCommand(ServiceOrder order) {
        super(order);
    }
    
    @Override
    protected void doExecute() {
        previousState = order.getCurrentState();
        order.submit();
    }
    
    @Override
    protected void doUndo() {
        order.setState(previousState);
    }
    
    @Override
    public String getCommandName() {
        return "SUBMIT_ORDER";
    }
}

public class ApproveOrderCommand extends AbstractOrderCommand {
    private String approverName;
    private OrderState previousState;
    
    public ApproveOrderCommand(ServiceOrder order, String approverName) {
        super(order);
        this.approverName = approverName;
    }
    
    @Override
    protected void doExecute() {
        previousState = order.getCurrentState();
        order.approve();
        order.getOrderData().put("approved_by", approverName);
        order.getOrderData().put("approval_time", executionTime);
    }
    
    @Override
    protected void doUndo() {
        order.setState(previousState);
        order.getOrderData().remove("approved_by");
        order.getOrderData().remove("approval_time");
    }
    
    @Override
    public String getCommandName() {
        return "APPROVE_ORDER";
    }
}

// Command processor with undo capability
public class OrderCommandProcessor {
    private Stack<OrderCommand> executedCommands = new Stack<>();
    
    public void execute(OrderCommand command) {
        command.execute();
        executedCommands.push(command);
    }
    
    public void undoLast() {
        if (!executedCommands.isEmpty()) {
            OrderCommand command = executedCommands.pop();
            command.undo();
        }
    }
    
    public void undoAll() {
        while (!executedCommands.isEmpty()) {
            undoLast();
        }
    }
    
    public List<OrderCommand> getCommandHistory() {
        return new ArrayList<>(executedCommands);
    }
}
```

### Integration and Testing
```java
// Test state transitions with commands
ServiceOrder order = new ServiceOrder("ORDER-001", "CUST-123", ServiceType.DSL);
OrderCommandProcessor processor = new OrderCommandProcessor();

// Execute workflow commands
processor.execute(new SubmitOrderCommand(order));
assertEquals("SUBMITTED", order.getCurrentState().getStateName());

processor.execute(new ValidateOrderCommand(order));
assertEquals("VALIDATED", order.getCurrentState().getStateName());

processor.execute(new ApproveOrderCommand(order, "Manager Smith"));
assertEquals("APPROVED", order.getCurrentState().getStateName());

// Test undo functionality
processor.undoLast(); // Undo approval
assertEquals("VALIDATED", order.getCurrentState().getStateName());

// Test invalid state transition
try {
    processor.execute(new CompleteOrderCommand(order)); // Can't complete from VALIDATED
    fail("Should throw exception for invalid state transition");
} catch (IllegalStateException expected) {
    // Expected behavior
}
```

---

## Exercise 4: Advanced Pattern Integration Challenge 🎯
**Pattern Focus:** Multiple Pattern Integration | **Duration:** 20 minutes | **Type:** Architecture Design

### Scenario
Design a comprehensive Telekom Customer Experience Platform that integrates multiple behavioral patterns to handle complex customer interaction workflows.

### System Requirements
The platform must handle:
- **Dynamic routing** of customer requests based on type, priority, and available resources
- **Real-time event processing** for customer actions and system events
- **Workflow state management** for long-running customer processes
- **Flexible strategy selection** for different customer interaction approaches
- **Command logging and replay** for audit and recovery

### Architecture Challenge (20 minutes)

**Design Task:** Create a system architecture that uses at least 3 behavioral patterns working together.

#### Component Requirements:

1. **Customer Request Router** (Chain of Responsibility)
   - Route requests based on customer type, request complexity, and agent availability
   - Support different routing strategies for peak/off-peak hours

2. **Interaction Strategy Manager** (Strategy Pattern)
   - Different interaction approaches for different customer segments
   - Dynamic strategy selection based on customer history and current context

3. **Workflow State Engine** (State Pattern)
   - Manage complex customer processes (onboarding, problem resolution, contract changes)
   - Support parallel workflows for complex scenarios

4. **Event Processing System** (Observer Pattern)
   - Real-time processing of customer interactions
   - Notification of relevant systems and personnel

5. **Command Audit System** (Command Pattern)
   - Log all customer service actions
   - Support replay and undo operations

#### Design Questions:
1. **How do the patterns interact?** Sketch the relationships between pattern implementations
2. **What are the key interfaces?** Define 2-3 crucial interfaces that enable pattern coordination
3. **How is consistency maintained?** How do you ensure state consistency across patterns?
4. **What are the failure scenarios?** How does the system handle failures in pattern interactions?

#### Implementation Sketch:
```java
// Customer Experience Platform - Integration Point
public class CustomerExperiencePlatform {
    private CustomerRequestRouter router;           // Chain of Responsibility
    private InteractionStrategyManager strategyManager; // Strategy
    private WorkflowStateEngine stateEngine;       // State
    private CustomerEventProcessor eventProcessor;  // Observer
    private CommandAuditSystem auditSystem;        // Command
    
    public void handleCustomerInteraction(CustomerInteraction interaction) {
        // TODO: Orchestrate pattern interactions
        // 1. Route through chain of responsibility
        // 2. Select appropriate interaction strategy
        // 3. Update workflow state
        // 4. Process events
        // 5. Log commands for audit
    }
}

// Define key integration interfaces
public interface CustomerInteraction {
    // TODO: Define customer interaction contract
}

public interface PatternCoordinator {
    // TODO: Define how patterns coordinate with each other
}
```

### Discussion Points
- How do you avoid tight coupling between patterns?
- What happens when patterns have conflicting requirements?
- How do you test integrated pattern systems?
- When is pattern integration beneficial vs. over-engineered?

---

## Summary and Behavioral Pattern Integration

### Pattern Relationships Learned

#### Complementary Patterns
- **Strategy + Template Method:** Algorithm families with shared structure
- **Observer + Chain of Responsibility:** Event notification with processing pipelines
- **State + Command:** Stateful workflows with auditable actions
- **Command + Template Method:** Parameterized operations with standardized execution

#### Architecture Benefits
- **Flexibility:** Easy to modify behavior without changing structure
- **Testability:** Individual behaviors can be tested in isolation
- **Maintainability:** Clear separation of different behavioral concerns
- **Extensibility:** New behaviors added without modifying existing code

### Key Insights from Day 3

1. **Behavioral patterns manage collaboration** between objects and define how responsibilities are distributed
2. **Event-driven architectures** benefit significantly from behavioral pattern application
3. **Complex workflows** are best managed through pattern composition rather than monolithic state machines
4. **Algorithm encapsulation** through Strategy pattern enables A/B testing and dynamic optimization

### Real-World Applications
- **Pricing engines** with multiple strategy implementations
- **Workflow management systems** using State and Command patterns  
- **Event processing pipelines** with Observer and Chain of Responsibility
- **Request routing systems** with dynamic handler selection

### Tomorrow's Preview: Day 4 - Advanced Integration
Day 4 will focus on **enterprise pattern integration** and **architectural decision-making**:
- Combining patterns from all three categories (Creational, Structural, Behavioral)
- Enterprise architecture patterns and best practices
- Performance and scalability considerations
- Pattern selection and trade-off analysis

---

**Instructor Notes:**
- Behavioral patterns often require more architectural thinking - allow extra discussion time
- Emphasize the event-driven nature of modern systems throughout exercises
- Help participants understand when pattern complexity is justified vs. over-engineering
- Use real customer experience scenarios to make behavioral patterns more concrete
- Pattern integration can be complex - break it down into step-by-step relationships