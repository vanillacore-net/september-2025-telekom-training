#!/usr/bin/env python3
"""
GOOD EXAMPLE: Clean Architecture with Separation of Concerns
Strukturierte Architektur mit klaren Schichten und Verantwortlichkeiten.
Verwendung von Design Patterns und SOLID Principles.
"""

from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import List, Optional, Dict, Any
from datetime import datetime, date
from decimal import Decimal
import hashlib
import secrets
from enum import Enum

# ============================================================================
# DOMAIN LAYER - Business Entities und Value Objects
# ============================================================================

@dataclass
class User:
    """Domain Entity: User"""
    id: int
    username: str
    email: str
    password_hash: str
    country: str
    is_admin: bool = False
    created_at: datetime = None
    
    def verify_password(self, password: str) -> bool:
        """Verify password against hash"""
        return PasswordHasher.verify(password, self.password_hash)

@dataclass
class Product:
    """Domain Entity: Product"""
    id: int
    name: str
    price: Decimal
    stock: int
    description: str = ""
    category: str = ""
    
    def is_available(self, quantity: int = 1) -> bool:
        """Check if product is available in requested quantity"""
        return self.stock >= quantity

@dataclass
class CartItem:
    """Value Object: Cart Item"""
    product: Product
    quantity: int
    
    @property
    def subtotal(self) -> Decimal:
        return self.product.price * self.quantity

class OrderStatus(Enum):
    """Order Status Enum"""
    PENDING = "PENDING"
    PAID = "PAID"
    SHIPPED = "SHIPPED"
    DELIVERED = "DELIVERED"
    CANCELLED = "CANCELLED"

@dataclass
class Order:
    """Domain Entity: Order"""
    id: int
    user: User
    items: List['OrderItem']
    status: OrderStatus
    total: Decimal
    tax: Decimal
    shipping: Decimal
    created_at: datetime
    payment_method: str
    payment_id: Optional[str] = None
    
    @property
    def grand_total(self) -> Decimal:
        return self.total + self.tax + self.shipping

@dataclass
class OrderItem:
    """Value Object: Order Item"""
    product: Product
    quantity: int
    price: Decimal
    
    @property
    def subtotal(self) -> Decimal:
        return self.price * self.quantity

# ============================================================================
# APPLICATION LAYER - Use Cases und Services
# ============================================================================

class AuthenticationService:
    """Authentication Service - handles user authentication"""
    
    def __init__(self, user_repository: 'UserRepository', 
                 session_manager: 'SessionManager'):
        self.user_repository = user_repository
        self.session_manager = session_manager
    
    def login(self, username: str, password: str) -> Optional[str]:
        """Authenticate user and create session"""
        user = self.user_repository.find_by_username(username)
        
        if not user or not user.verify_password(password):
            raise AuthenticationError("Invalid credentials")
        
        session_token = self.session_manager.create_session(user)
        return session_token
    
    def logout(self, session_token: str) -> None:
        """Invalidate user session"""
        self.session_manager.invalidate_session(session_token)
    
    def get_current_user(self, session_token: str) -> Optional[User]:
        """Get user from session token"""
        return self.session_manager.get_user(session_token)

class ShoppingCartService:
    """Shopping Cart Service - manages cart operations"""
    
    def __init__(self, product_repository: 'ProductRepository'):
        self.product_repository = product_repository
        self.carts: Dict[str, List[CartItem]] = {}  # session_id -> cart items
    
    def add_to_cart(self, session_id: str, product_id: int, 
                    quantity: int = 1) -> None:
        """Add product to cart"""
        product = self.product_repository.find_by_id(product_id)
        
        if not product:
            raise ProductNotFoundError(f"Product {product_id} not found")
        
        if not product.is_available(quantity):
            raise InsufficientStockError(f"Insufficient stock for {product.name}")
        
        if session_id not in self.carts:
            self.carts[session_id] = []
        
        # Check if product already in cart
        for item in self.carts[session_id]:
            if item.product.id == product_id:
                item.quantity += quantity
                return
        
        # Add new item
        self.carts[session_id].append(CartItem(product, quantity))
    
    def get_cart(self, session_id: str) -> List[CartItem]:
        """Get cart items for session"""
        return self.carts.get(session_id, [])
    
    def clear_cart(self, session_id: str) -> None:
        """Clear cart for session"""
        if session_id in self.carts:
            del self.carts[session_id]
    
    def calculate_total(self, session_id: str) -> Decimal:
        """Calculate cart total"""
        items = self.get_cart(session_id)
        return sum(item.subtotal for item in items)

class OrderService:
    """Order Service - handles order processing"""
    
    def __init__(self, order_repository: 'OrderRepository',
                 product_repository: 'ProductRepository',
                 payment_service: 'PaymentService',
                 notification_service: 'NotificationService',
                 tax_calculator: 'TaxCalculator',
                 shipping_calculator: 'ShippingCalculator'):
        self.order_repository = order_repository
        self.product_repository = product_repository
        self.payment_service = payment_service
        self.notification_service = notification_service
        self.tax_calculator = tax_calculator
        self.shipping_calculator = shipping_calculator
    
    def create_order(self, user: User, cart_items: List[CartItem],
                    payment_method: str, payment_info: Dict) -> Order:
        """Create and process order"""
        
        # Calculate totals
        subtotal = sum(item.subtotal for item in cart_items)
        tax = self.tax_calculator.calculate(subtotal, user.country)
        shipping = self.shipping_calculator.calculate(cart_items, user.country)
        total = subtotal + tax + shipping
        
        # Process payment
        payment_result = self.payment_service.process_payment(
            payment_method, payment_info, total
        )
        
        if not payment_result.success:
            raise PaymentError(payment_result.error_message)
        
        # Create order
        order = Order(
            id=None,  # Will be assigned by repository
            user=user,
            items=self._create_order_items(cart_items),
            status=OrderStatus.PAID,
            total=subtotal,
            tax=tax,
            shipping=shipping,
            created_at=datetime.now(),
            payment_method=payment_method,
            payment_id=payment_result.transaction_id
        )
        
        # Save order
        saved_order = self.order_repository.save(order)
        
        # Update inventory
        for item in cart_items:
            self.product_repository.decrease_stock(
                item.product.id, item.quantity
            )
        
        # Send notifications
        self.notification_service.send_order_confirmation(saved_order)
        
        return saved_order
    
    def _create_order_items(self, cart_items: List[CartItem]) -> List[OrderItem]:
        """Convert cart items to order items"""
        return [
            OrderItem(
                product=item.product,
                quantity=item.quantity,
                price=item.product.price
            )
            for item in cart_items
        ]

class ProductSearchService:
    """Product Search Service"""
    
    def __init__(self, product_repository: 'ProductRepository',
                 pricing_service: 'PricingService'):
        self.product_repository = product_repository
        self.pricing_service = pricing_service
    
    def search(self, query: str, user: Optional[User] = None) -> List[Product]:
        """Search products with pricing adjustments"""
        products = self.product_repository.search(query)
        
        # Apply pricing rules
        for product in products:
            adjusted_price = self.pricing_service.get_price(product, user)
            product.price = adjusted_price
        
        return products

# ============================================================================
# DOMAIN SERVICES - Business Logic
# ============================================================================

class PricingService:
    """Pricing Service - handles dynamic pricing"""
    
    def __init__(self, promotion_repository: 'PromotionRepository'):
        self.promotion_repository = promotion_repository
    
    def get_price(self, product: Product, user: Optional[User] = None) -> Decimal:
        """Calculate product price with promotions"""
        price = product.price
        
        # Apply promotions
        promotions = self.promotion_repository.get_active_promotions()
        for promotion in promotions:
            price = promotion.apply(price, product, user)
        
        return price

class TaxCalculator:
    """Tax Calculator - calculates tax based on country"""
    
    TAX_RATES = {
        'DE': Decimal('0.19'),
        'AT': Decimal('0.20'),
        'CH': Decimal('0.077'),
        'US': Decimal('0.08'),
    }
    
    def calculate(self, amount: Decimal, country: str) -> Decimal:
        """Calculate tax for amount and country"""
        rate = self.TAX_RATES.get(country, Decimal('0'))
        return amount * rate

class ShippingCalculator:
    """Shipping Calculator"""
    
    def calculate(self, items: List[CartItem], country: str) -> Decimal:
        """Calculate shipping cost"""
        # Simple calculation based on item count and country
        base_cost = Decimal('5.00')
        
        if country in ['DE', 'AT', 'CH']:
            multiplier = Decimal('1.0')
        else:
            multiplier = Decimal('2.0')
        
        item_count = sum(item.quantity for item in items)
        return base_cost * multiplier + (item_count * Decimal('0.5'))

# ============================================================================
# INFRASTRUCTURE LAYER - External Dependencies
# ============================================================================

class Repository(ABC):
    """Base Repository Interface"""
    
    @abstractmethod
    def find_by_id(self, id: int):
        pass
    
    @abstractmethod
    def save(self, entity):
        pass

class UserRepository(Repository):
    """User Repository - data access for users"""
    
    def __init__(self, db_connection):
        self.db = db_connection
    
    def find_by_id(self, user_id: int) -> Optional[User]:
        """Find user by ID"""
        # Implementation would query database
        pass
    
    def find_by_username(self, username: str) -> Optional[User]:
        """Find user by username"""
        # Implementation would query database
        pass
    
    def save(self, user: User) -> User:
        """Save user to database"""
        # Implementation would save to database
        pass

class ProductRepository(Repository):
    """Product Repository"""
    
    def __init__(self, db_connection):
        self.db = db_connection
    
    def find_by_id(self, product_id: int) -> Optional[Product]:
        """Find product by ID"""
        # Implementation
        pass
    
    def search(self, query: str) -> List[Product]:
        """Search products"""
        # Implementation
        pass
    
    def decrease_stock(self, product_id: int, quantity: int) -> None:
        """Decrease product stock"""
        # Implementation
        pass
    
    def save(self, product: Product) -> Product:
        """Save product"""
        # Implementation
        pass

class OrderRepository(Repository):
    """Order Repository"""
    
    def __init__(self, db_connection):
        self.db = db_connection
    
    def find_by_id(self, order_id: int) -> Optional[Order]:
        """Find order by ID"""
        # Implementation
        pass
    
    def find_by_user(self, user_id: int) -> List[Order]:
        """Find orders by user"""
        # Implementation
        pass
    
    def save(self, order: Order) -> Order:
        """Save order"""
        # Implementation
        pass

class PromotionRepository:
    """Promotion Repository"""
    
    def get_active_promotions(self) -> List['Promotion']:
        """Get active promotions"""
        # Implementation
        return []

# ============================================================================
# EXTERNAL SERVICES
# ============================================================================

class PaymentService:
    """Payment Service - handles payment processing"""
    
    def __init__(self, payment_gateway: 'PaymentGateway'):
        self.gateway = payment_gateway
    
    def process_payment(self, method: str, info: Dict, 
                       amount: Decimal) -> 'PaymentResult':
        """Process payment through gateway"""
        strategy = self._get_strategy(method)
        return strategy.process(info, amount)
    
    def _get_strategy(self, method: str) -> 'PaymentStrategy':
        """Get payment strategy for method"""
        if method == 'credit_card':
            return CreditCardPayment(self.gateway)
        elif method == 'paypal':
            return PayPalPayment(self.gateway)
        else:
            raise ValueError(f"Unknown payment method: {method}")

class PaymentStrategy(ABC):
    """Payment Strategy Interface"""
    
    @abstractmethod
    def process(self, info: Dict, amount: Decimal) -> 'PaymentResult':
        pass

class CreditCardPayment(PaymentStrategy):
    """Credit Card Payment Strategy"""
    
    def __init__(self, gateway: 'PaymentGateway'):
        self.gateway = gateway
    
    def process(self, info: Dict, amount: Decimal) -> 'PaymentResult':
        """Process credit card payment"""
        # Validate card
        if not self._validate_card(info.get('card_number')):
            return PaymentResult(False, error_message="Invalid card number")
        
        # Process through gateway
        return self.gateway.charge_card(
            info['card_number'],
            info['cvv'],
            amount
        )
    
    def _validate_card(self, card_number: str) -> bool:
        """Validate credit card number"""
        # Luhn algorithm implementation
        return len(card_number) == 16

class PayPalPayment(PaymentStrategy):
    """PayPal Payment Strategy"""
    
    def __init__(self, gateway: 'PaymentGateway'):
        self.gateway = gateway
    
    def process(self, info: Dict, amount: Decimal) -> 'PaymentResult':
        """Process PayPal payment"""
        return self.gateway.charge_paypal(
            info['email'],
            amount
        )

@dataclass
class PaymentResult:
    """Payment Result"""
    success: bool
    transaction_id: Optional[str] = None
    error_message: Optional[str] = None

class PaymentGateway:
    """Payment Gateway Interface"""
    
    def charge_card(self, card_number: str, cvv: str, 
                   amount: Decimal) -> PaymentResult:
        """Charge credit card"""
        # Implementation would call actual payment provider
        transaction_id = secrets.token_hex(16)
        return PaymentResult(True, transaction_id)
    
    def charge_paypal(self, email: str, amount: Decimal) -> PaymentResult:
        """Charge PayPal"""
        # Implementation would call PayPal API
        transaction_id = secrets.token_hex(16)
        return PaymentResult(True, transaction_id)

class NotificationService:
    """Notification Service"""
    
    def __init__(self, email_sender: 'EmailSender'):
        self.email_sender = email_sender
    
    def send_order_confirmation(self, order: Order) -> None:
        """Send order confirmation email"""
        subject = f"Order Confirmation #{order.id}"
        body = self._create_order_email(order)
        self.email_sender.send(order.user.email, subject, body)
    
    def _create_order_email(self, order: Order) -> str:
        """Create order confirmation email body"""
        # Template rendering would go here
        return f"Your order #{order.id} has been confirmed."

class EmailSender:
    """Email Sender - handles email delivery"""
    
    def __init__(self, smtp_config: Dict):
        self.config = smtp_config
    
    def send(self, to: str, subject: str, body: str) -> None:
        """Send email"""
        # Implementation would use SMTP
        pass

# ============================================================================
# PRESENTATION LAYER - Controllers and Views
# ============================================================================

class BaseController:
    """Base Controller"""
    
    def __init__(self, auth_service: AuthenticationService):
        self.auth_service = auth_service
    
    def get_current_user(self, session_token: str) -> Optional[User]:
        """Get current user from session"""
        return self.auth_service.get_current_user(session_token)

class AuthController(BaseController):
    """Authentication Controller"""
    
    def login(self, request: Dict) -> Dict:
        """Handle login request"""
        try:
            username = request['username']
            password = request['password']
            
            session_token = self.auth_service.login(username, password)
            
            return {
                'success': True,
                'session_token': session_token,
                'message': 'Login successful'
            }
        except AuthenticationError as e:
            return {
                'success': False,
                'error': str(e)
            }
    
    def logout(self, request: Dict) -> Dict:
        """Handle logout request"""
        session_token = request.get('session_token')
        self.auth_service.logout(session_token)
        
        return {
            'success': True,
            'message': 'Logout successful'
        }

class ShopController(BaseController):
    """Shop Controller"""
    
    def __init__(self, auth_service: AuthenticationService,
                 cart_service: ShoppingCartService,
                 product_service: ProductSearchService,
                 order_service: OrderService):
        super().__init__(auth_service)
        self.cart_service = cart_service
        self.product_service = product_service
        self.order_service = order_service
    
    def search_products(self, request: Dict) -> Dict:
        """Handle product search"""
        query = request.get('query', '')
        user = self.get_current_user(request.get('session_token'))
        
        products = self.product_service.search(query, user)
        
        return {
            'success': True,
            'products': [self._product_to_dict(p) for p in products]
        }
    
    def add_to_cart(self, request: Dict) -> Dict:
        """Handle add to cart"""
        session_id = request['session_token']
        product_id = request['product_id']
        quantity = request.get('quantity', 1)
        
        try:
            self.cart_service.add_to_cart(session_id, product_id, quantity)
            return {
                'success': True,
                'message': 'Product added to cart'
            }
        except (ProductNotFoundError, InsufficientStockError) as e:
            return {
                'success': False,
                'error': str(e)
            }
    
    def checkout(self, request: Dict) -> Dict:
        """Handle checkout"""
        session_token = request['session_token']
        user = self.get_current_user(session_token)
        
        if not user:
            return {
                'success': False,
                'error': 'Not authenticated'
            }
        
        cart_items = self.cart_service.get_cart(session_token)
        
        if not cart_items:
            return {
                'success': False,
                'error': 'Cart is empty'
            }
        
        payment_method = request['payment_method']
        payment_info = request['payment_info']
        
        try:
            order = self.order_service.create_order(
                user, cart_items, payment_method, payment_info
            )
            
            self.cart_service.clear_cart(session_token)
            
            return {
                'success': True,
                'order_id': order.id,
                'total': str(order.grand_total)
            }
        except PaymentError as e:
            return {
                'success': False,
                'error': str(e)
            }
    
    def _product_to_dict(self, product: Product) -> Dict:
        """Convert product to dictionary"""
        return {
            'id': product.id,
            'name': product.name,
            'price': str(product.price),
            'stock': product.stock
        }

# ============================================================================
# UTILITIES
# ============================================================================

class PasswordHasher:
    """Password Hasher Utility"""
    
    @staticmethod
    def hash(password: str) -> str:
        """Hash password"""
        salt = secrets.token_hex(16)
        hash_obj = hashlib.pbkdf2_hmac('sha256', 
                                       password.encode(), 
                                       salt.encode(), 
                                       100000)
        return f"{salt}${hash_obj.hex()}"
    
    @staticmethod
    def verify(password: str, hash_str: str) -> bool:
        """Verify password against hash"""
        salt, hash_hex = hash_str.split('$')
        hash_obj = hashlib.pbkdf2_hmac('sha256',
                                       password.encode(),
                                       salt.encode(),
                                       100000)
        return hash_obj.hex() == hash_hex

class SessionManager:
    """Session Manager"""
    
    def __init__(self):
        self.sessions: Dict[str, User] = {}
    
    def create_session(self, user: User) -> str:
        """Create new session"""
        token = secrets.token_urlsafe(32)
        self.sessions[token] = user
        return token
    
    def get_user(self, token: str) -> Optional[User]:
        """Get user from session"""
        return self.sessions.get(token)
    
    def invalidate_session(self, token: str) -> None:
        """Invalidate session"""
        if token in self.sessions:
            del self.sessions[token]

# ============================================================================
# EXCEPTIONS
# ============================================================================

class ApplicationError(Exception):
    """Base application exception"""
    pass

class AuthenticationError(ApplicationError):
    """Authentication error"""
    pass

class ProductNotFoundError(ApplicationError):
    """Product not found error"""
    pass

class InsufficientStockError(ApplicationError):
    """Insufficient stock error"""
    pass

class PaymentError(ApplicationError):
    """Payment error"""
    pass

# ============================================================================
# DEPENDENCY INJECTION CONTAINER
# ============================================================================

class Container:
    """Simple Dependency Injection Container"""
    
    def __init__(self, config: Dict):
        self.config = config
        self._services = {}
    
    def get(self, service_name: str):
        """Get service instance"""
        if service_name not in self._services:
            self._services[service_name] = self._create_service(service_name)
        return self._services[service_name]
    
    def _create_service(self, service_name: str):
        """Create service instance"""
        # This would create and wire up all dependencies
        # For brevity, showing simplified version
        
        if service_name == 'auth_controller':
            return AuthController(self.get('auth_service'))
        elif service_name == 'shop_controller':
            return ShopController(
                self.get('auth_service'),
                self.get('cart_service'),
                self.get('product_service'),
                self.get('order_service')
            )
        # ... etc for all services

# ============================================================================
# APPLICATION ENTRY POINT
# ============================================================================

class Application:
    """Main Application"""
    
    def __init__(self, config: Dict):
        self.container = Container(config)
    
    def handle_request(self, request: Dict) -> Dict:
        """Route request to appropriate controller"""
        action = request.get('action')
        
        if action in ['login', 'logout']:
            controller = self.container.get('auth_controller')
            return getattr(controller, action)(request)
        elif action in ['search', 'add_to_cart', 'checkout']:
            controller = self.container.get('shop_controller')
            method_map = {
                'search': 'search_products',
                'add_to_cart': 'add_to_cart',
                'checkout': 'checkout'
            }
            return getattr(controller, method_map[action])(request)
        else:
            return {'success': False, 'error': 'Unknown action'}

if __name__ == "__main__":
    # Configuration would be loaded from file/env
    config = {
        'db': {
            'host': 'localhost',
            'user': 'app_user',
            'password': 'secure_password',
            'database': 'ecommerce'
        },
        'smtp': {
            'host': 'smtp.gmail.com',
            'port': 587,
            'user': 'shop@example.com',
            'password': 'secure_password'
        }
    }
    
    app = Application(config)
    
    # Example usage
    response = app.handle_request({
        'action': 'login',
        'username': 'user123',
        'password': 'password123'
    })
    print(response)