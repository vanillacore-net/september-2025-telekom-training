package antipatterns.theblob.good;

import java.util.List;
import java.util.UUID;

/**
 * GOOD EXAMPLE: Separation of Concerns
 * Aufgeteilt in fokussierte Services mit klaren Verantwortlichkeiten.
 * Jede Klasse befolgt das Single Responsibility Principle.
 */
public class OrderServiceGood {
    private final OrderValidator validator;
    private final PricingService pricingService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;
    
    public OrderServiceGood(OrderValidator validator,
                           PricingService pricingService,
                           PaymentService paymentService,
                           InventoryService inventoryService,
                           NotificationService notificationService,
                           OrderRepository orderRepository) {
        this.validator = validator;
        this.pricingService = pricingService;
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
        this.orderRepository = orderRepository;
    }
    
    /**
     * Klare, fokussierte Methode die den Order-Prozess orchestriert
     */
    public OrderResult processOrder(OrderRequest request) {
        try {
            // Schritt 1: Validierung
            ValidationResult validation = validator.validateOrder(request);
            if (!validation.isValid()) {
                return OrderResult.failure(validation.getErrorMessage());
            }
            
            // Schritt 2: Preisberechnung
            PricingResult pricing = pricingService.calculateTotal(
                request.getProducts(),
                request.getCustomer(),
                request.getShipping()
            );
            
            // Schritt 3: Inventory Check
            InventoryCheckResult inventoryCheck = inventoryService.checkAvailability(
                request.getProducts()
            );
            if (!inventoryCheck.isAvailable()) {
                return OrderResult.failure("Products not available: " + 
                    inventoryCheck.getUnavailableProducts());
            }
            
            // Schritt 4: Payment Processing
            PaymentResult paymentResult = paymentService.processPayment(
                request.getPaymentInfo(),
                pricing.getTotalAmount()
            );
            if (!paymentResult.isSuccess()) {
                return OrderResult.failure("Payment failed: " + 
                    paymentResult.getErrorMessage());
            }
            
            // Schritt 5: Order Creation
            Order order = createOrder(request, pricing, paymentResult);
            Order savedOrder = orderRepository.save(order);
            
            // Schritt 6: Inventory Update
            inventoryService.reserveProducts(savedOrder.getItems());
            
            // Schritt 7: Notifications (async)
            notificationService.sendOrderNotifications(savedOrder);
            
            return OrderResult.success(savedOrder.getId());
            
        } catch (Exception e) {
            // Centralized error handling
            return handleOrderError(e, request);
        }
    }
    
    private Order createOrder(OrderRequest request, PricingResult pricing, 
                             PaymentResult payment) {
        return Order.builder()
            .id(UUID.randomUUID().toString())
            .customerId(request.getCustomer().getId())
            .items(request.getProducts())
            .pricing(pricing)
            .payment(payment)
            .shipping(request.getShipping())
            .status(OrderStatus.PROCESSING)
            .build();
    }
    
    private OrderResult handleOrderError(Exception e, OrderRequest request) {
        // Error handling logic
        return OrderResult.failure("Order processing failed: " + e.getMessage());
    }
}

/**
 * Fokussierte Validator-Klasse
 */
class OrderValidator {
    private final CustomerService customerService;
    private final ProductService productService;
    
    public OrderValidator(CustomerService customerService, ProductService productService) {
        this.customerService = customerService;
        this.productService = productService;
    }
    
    public ValidationResult validateOrder(OrderRequest request) {
        // Validate customer
        if (!customerService.isActive(request.getCustomer())) {
            return ValidationResult.invalid("Customer account is not active");
        }
        
        // Validate products
        if (request.getProducts().isEmpty()) {
            return ValidationResult.invalid("No products in order");
        }
        
        for (Product product : request.getProducts()) {
            if (!productService.isAvailable(product)) {
                return ValidationResult.invalid("Product not available: " + product.getId());
            }
        }
        
        // Validate shipping
        if (!isValidShippingAddress(request.getShipping())) {
            return ValidationResult.invalid("Invalid shipping address");
        }
        
        return ValidationResult.valid();
    }
    
    private boolean isValidShippingAddress(ShippingInfo shipping) {
        return shipping != null && 
               shipping.getAddress() != null && 
               shipping.getCountry() != null;
    }
}

/**
 * Dedizierter Pricing Service
 */
class PricingService {
    private final PromotionService promotionService;
    private final TaxCalculator taxCalculator;
    private final ShippingCalculator shippingCalculator;
    
    public PricingService(PromotionService promotionService,
                         TaxCalculator taxCalculator,
                         ShippingCalculator shippingCalculator) {
        this.promotionService = promotionService;
        this.taxCalculator = taxCalculator;
        this.shippingCalculator = shippingCalculator;
    }
    
    public PricingResult calculateTotal(List<Product> products, 
                                       Customer customer,
                                       ShippingInfo shipping) {
        // Calculate subtotal
        double subtotal = products.stream()
            .mapToDouble(Product::getPrice)
            .sum();
        
        // Apply promotions
        double discount = promotionService.calculateDiscount(products, customer);
        double discountedTotal = subtotal - discount;
        
        // Calculate tax
        double tax = taxCalculator.calculateTax(discountedTotal, shipping.getCountry());
        
        // Calculate shipping
        double shippingCost = shippingCalculator.calculateShipping(
            products, shipping
        );
        
        return PricingResult.builder()
            .subtotal(subtotal)
            .discount(discount)
            .tax(tax)
            .shipping(shippingCost)
            .total(discountedTotal + tax + shippingCost)
            .build();
    }
}

/**
 * Payment Service mit Strategy Pattern für verschiedene Payment-Typen
 */
class PaymentService {
    private final PaymentStrategyFactory strategyFactory;
    
    public PaymentService(PaymentStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }
    
    public PaymentResult processPayment(PaymentInfo paymentInfo, double amount) {
        PaymentStrategy strategy = strategyFactory.getStrategy(paymentInfo.getType());
        return strategy.processPayment(paymentInfo, amount);
    }
}

/**
 * Inventory Service für Lagerbestände
 */
class InventoryService {
    private final InventoryRepository repository;
    
    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }
    
    public InventoryCheckResult checkAvailability(List<Product> products) {
        List<Product> unavailable = products.stream()
            .filter(p -> !repository.isInStock(p.getId(), p.getQuantity()))
            .toList();
            
        if (unavailable.isEmpty()) {
            return InventoryCheckResult.available();
        } else {
            return InventoryCheckResult.unavailable(unavailable);
        }
    }
    
    public void reserveProducts(List<OrderItem> items) {
        for (OrderItem item : items) {
            repository.reserve(item.getProductId(), item.getQuantity());
        }
    }
}

/**
 * Async Notification Service
 */
class NotificationService {
    private final EmailService emailService;
    private final SmsService smsService;
    private final EventPublisher eventPublisher;
    
    public NotificationService(EmailService emailService,
                             SmsService smsService,
                             EventPublisher eventPublisher) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.eventPublisher = eventPublisher;
    }
    
    public void sendOrderNotifications(Order order) {
        // Async execution
        CompletableFuture.runAsync(() -> {
            // Send customer email
            emailService.sendOrderConfirmation(order);
            
            // Send SMS if requested
            if (order.getCustomer().isSmsEnabled()) {
                smsService.sendOrderSms(order);
            }
            
            // Publish event for other services
            eventPublisher.publish(new OrderCreatedEvent(order));
        });
    }
}

// Supporting classes with clear responsibilities
class OrderRequest {
    private Customer customer;
    private List<Product> products;
    private PaymentInfo paymentInfo;
    private ShippingInfo shipping;
    
    // getters
    public Customer getCustomer() { return customer; }
    public List<Product> getProducts() { return products; }
    public PaymentInfo getPaymentInfo() { return paymentInfo; }
    public ShippingInfo getShipping() { return shipping; }
}

class OrderResult {
    private boolean success;
    private String orderId;
    private String errorMessage;
    
    public static OrderResult success(String orderId) {
        OrderResult result = new OrderResult();
        result.success = true;
        result.orderId = orderId;
        return result;
    }
    
    public static OrderResult failure(String errorMessage) {
        OrderResult result = new OrderResult();
        result.success = false;
        result.errorMessage = errorMessage;
        return result;
    }
    
    public boolean isSuccess() { return success; }
    public String getOrderId() { return orderId; }
    public String getErrorMessage() { return errorMessage; }
}

class ValidationResult {
    private boolean valid;
    private String errorMessage;
    
    public static ValidationResult valid() {
        ValidationResult result = new ValidationResult();
        result.valid = true;
        return result;
    }
    
    public static ValidationResult invalid(String message) {
        ValidationResult result = new ValidationResult();
        result.valid = false;
        result.errorMessage = message;
        return result;
    }
    
    public boolean isValid() { return valid; }
    public String getErrorMessage() { return errorMessage; }
}

// Weitere Supporting Classes (würden in echten Files sein)
class Customer {
    private String id;
    private boolean smsEnabled;
    public String getId() { return id; }
    public boolean isSmsEnabled() { return smsEnabled; }
}

class Product {
    private String id;
    private double price;
    private int quantity;
    public String getId() { return id; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}

class PaymentInfo {
    private String type;
    public String getType() { return type; }
}

class ShippingInfo {
    private String address;
    private String country;
    public String getAddress() { return address; }
    public String getCountry() { return country; }
}

class Order {
    private String id;
    private String customerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private Customer customer;
    
    public String getId() { return id; }
    public List<OrderItem> getItems() { return items; }
    public Customer getCustomer() { return customer; }
    
    public static OrderBuilder builder() { return new OrderBuilder(); }
    
    static class OrderBuilder {
        private Order order = new Order();
        public OrderBuilder id(String id) { order.id = id; return this; }
        public OrderBuilder customerId(String id) { order.customerId = id; return this; }
        public OrderBuilder items(List<Product> products) { 
            // Convert to OrderItems
            return this; 
        }
        public OrderBuilder pricing(PricingResult pricing) { return this; }
        public OrderBuilder payment(PaymentResult payment) { return this; }
        public OrderBuilder shipping(ShippingInfo shipping) { return this; }
        public OrderBuilder status(OrderStatus status) { order.status = status; return this; }
        public Order build() { return order; }
    }
}

class OrderItem {
    private String productId;
    private int quantity;
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}

enum OrderStatus {
    PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

class PricingResult {
    private double subtotal;
    private double discount;
    private double tax;
    private double shipping;
    private double total;
    
    public double getTotalAmount() { return total; }
    
    public static PricingResultBuilder builder() { return new PricingResultBuilder(); }
    
    static class PricingResultBuilder {
        private PricingResult result = new PricingResult();
        public PricingResultBuilder subtotal(double v) { result.subtotal = v; return this; }
        public PricingResultBuilder discount(double v) { result.discount = v; return this; }
        public PricingResultBuilder tax(double v) { result.tax = v; return this; }
        public PricingResultBuilder shipping(double v) { result.shipping = v; return this; }
        public PricingResultBuilder total(double v) { result.total = v; return this; }
        public PricingResult build() { return result; }
    }
}

class PaymentResult {
    private boolean success;
    private String errorMessage;
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}

class InventoryCheckResult {
    private boolean available;
    private List<Product> unavailableProducts;
    
    public static InventoryCheckResult available() {
        InventoryCheckResult result = new InventoryCheckResult();
        result.available = true;
        return result;
    }
    
    public static InventoryCheckResult unavailable(List<Product> products) {
        InventoryCheckResult result = new InventoryCheckResult();
        result.available = false;
        result.unavailableProducts = products;
        return result;
    }
    
    public boolean isAvailable() { return available; }
    public List<Product> getUnavailableProducts() { return unavailableProducts; }
}

// Interfaces for dependencies
interface CustomerService {
    boolean isActive(Customer customer);
}

interface ProductService {
    boolean isAvailable(Product product);
}

interface OrderRepository {
    Order save(Order order);
}

interface PromotionService {
    double calculateDiscount(List<Product> products, Customer customer);
}

interface TaxCalculator {
    double calculateTax(double amount, String country);
}

interface ShippingCalculator {
    double calculateShipping(List<Product> products, ShippingInfo shipping);
}

interface PaymentStrategy {
    PaymentResult processPayment(PaymentInfo info, double amount);
}

interface PaymentStrategyFactory {
    PaymentStrategy getStrategy(String type);
}

interface InventoryRepository {
    boolean isInStock(String productId, int quantity);
    void reserve(String productId, int quantity);
}

interface EmailService {
    void sendOrderConfirmation(Order order);
}

interface SmsService {
    void sendOrderSms(Order order);
}

interface EventPublisher {
    void publish(OrderCreatedEvent event);
}

class OrderCreatedEvent {
    private Order order;
    public OrderCreatedEvent(Order order) { this.order = order; }
}

// Zusatz für async
class CompletableFuture {
    public static void runAsync(Runnable task) {
        // Async execution
    }
}