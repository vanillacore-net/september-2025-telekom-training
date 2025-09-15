#!/usr/bin/env python3
"""
BAD EXAMPLE: Spaghetti Code Anti-Pattern
Unstrukturierter Code ohne klare Architektur, hohe Kopplung, fehlende Abstraktion.
Alles ist vermischt: Datenbank, Business Logic, Präsentation, HTML Generation.
"""

import mysql.connector
import smtplib
import json
import random
import datetime
import hashlib
import os

# Globale Variablen überall
db_connection = None
current_user = None
shopping_cart = []
config = {}
last_error = ""
products_cache = {}
admin_logged_in = False
temp_data = {}

def init_system():
    """Alles in einer Funktion initialisieren - keine Struktur"""
    global db_connection, config, products_cache
    
    # Hardcoded Konfiguration
    config['db_host'] = 'localhost'
    config['db_user'] = 'root'
    config['db_pass'] = 'password123'
    config['smtp_server'] = 'smtp.gmail.com'
    config['admin_email'] = 'admin@shop.com'
    
    try:
        db_connection = mysql.connector.connect(
            host=config['db_host'],
            user=config['db_user'],
            password=config['db_pass'],
            database='ecommerce'
        )
    except:
        print("Database error!")
        # Fortsetzung trotz Fehler...
    
    # Produkte cachen - warum hier?
    load_all_products()

def process_request(request_data):
    """Monster-Funktion die alles macht - über 1000 Zeilen in echter Welt"""
    global current_user, shopping_cart, last_error, admin_logged_in, temp_data
    
    # Parse request - verschiedene Formate gemischt
    if isinstance(request_data, str):
        try:
            data = json.loads(request_data)
        except:
            # Vielleicht ist es URL encoded?
            parts = request_data.split('&')
            data = {}
            for part in parts:
                if '=' in part:
                    key, value = part.split('=')
                    data[key] = value
    else:
        data = request_data
    
    action = data.get('action', '')
    
    # Riesiger if-else Block für alle Actions
    if action == 'login':
        username = data.get('username', '')
        password = data.get('password', '')
        
        # SQL Injection anfällig!
        cursor = db_connection.cursor()
        query = f"SELECT * FROM users WHERE username='{username}' AND password='{password}'"
        cursor.execute(query)
        user = cursor.fetchone()
        
        if user:
            current_user = user
            # Session Management? Fehlanzeige!
            temp_data['login_time'] = datetime.datetime.now()
            
            # Admin Check mit hardcoded Werten
            if username == 'admin' or user[0] == 1:
                admin_logged_in = True
                
            # HTML direkt hier generieren
            html = "<html><body>"
            html += f"<h1>Welcome {username}!</h1>"
            html += "<div>Login successful</div>"
            
            # Produkte anzeigen - warum hier?
            html += "<h2>Our Products:</h2>"
            cursor.execute("SELECT * FROM products")
            for product in cursor.fetchall():
                # Preis berechnung mitten im HTML
                price = product[2]
                if datetime.datetime.now().weekday() == 4:  # Freitag
                    price = price * 0.9  # 10% Rabatt
                
                html += f"<div>"
                html += f"<h3>{product[1]}</h3>"
                html += f"<p>Price: ${price}</p>"
                html += f"<button onclick='addToCart({product[0]})'>Add to Cart</button>"
                html += f"</div>"
                
            html += "</body></html>"
            return html
        else:
            last_error = "Login failed"
            return "<html><body>Login failed</body></html>"
            
    elif action == 'add_to_cart':
        if not current_user:
            return "Not logged in"
            
        product_id = data.get('product_id')
        
        # Produkt laden - inkonsistent mit oben
        cursor = db_connection.cursor()
        cursor.execute(f"SELECT * FROM products WHERE id={product_id}")
        product = cursor.fetchone()
        
        if product:
            # Cart Management ohne Struktur
            shopping_cart.append({
                'id': product[0],
                'name': product[1],
                'price': product[2],
                'quantity': 1  # Quantity Management fehlt
            })
            
            # Warum Email hier senden?
            send_email(current_user[2], "Product added", 
                      f"You added {product[1]} to cart")
            
            # Analytics tracking - random eingefügt
            with open('analytics.txt', 'a') as f:
                f.write(f"{datetime.datetime.now()}: User {current_user[1]} added {product[1]}\n")
                
            return "Added to cart"
            
    elif action == 'checkout':
        if not shopping_cart:
            return "Cart empty"
            
        total = 0
        order_html = "<h1>Order Summary</h1>"
        
        # Berechnung und HTML Generation vermischt
        for item in shopping_cart:
            price = item['price']
            
            # Business Logic mitten in der Präsentation
            # Steuer berechnen - hardcoded
            if current_user[5] == 'DE':  # Country aus User Tabelle
                tax = price * 0.19
            elif current_user[5] == 'US':
                tax = price * 0.08
            else:
                tax = 0
                
            price_with_tax = price + tax
            total += price_with_tax
            
            order_html += f"<div>{item['name']}: ${price_with_tax}</div>"
            
        # Payment Processing inline
        payment_method = data.get('payment', 'credit_card')
        
        if payment_method == 'credit_card':
            card_number = data.get('card')
            # Kreditkarte validieren - unsicher!
            if len(card_number) == 16:
                # "Process" payment - fake implementation
                payment_id = random.randint(1000, 9999)
                
                # Order in DB speichern
                cursor = db_connection.cursor()
                order_date = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
                
                # SQL Injection wieder möglich
                cursor.execute(f"""
                    INSERT INTO orders (user_id, total, date, payment_id) 
                    VALUES ({current_user[0]}, {total}, '{order_date}', {payment_id})
                """)
                
                order_id = cursor.lastrowid
                
                # Order Items speichern - inkonsistent
                for item in shopping_cart:
                    cursor.execute(f"""
                        INSERT INTO order_items (order_id, product_id, quantity, price)
                        VALUES ({order_id}, {item['id']}, 1, {item['price']})
                    """)
                    
                    # Inventory Update vergessen!
                    
                db_connection.commit()
                
                # Email senden
                email_body = f"Order {order_id} confirmed. Total: ${total}"
                send_email(current_user[2], "Order Confirmation", email_body)
                
                # Admin benachrichtigen - warum hier?
                send_email(config['admin_email'], "New Order", f"Order {order_id} received")
                
                # Report generieren - völlig deplatziert
                generate_daily_report()
                
                # Cart leeren
                shopping_cart = []
                
                return f"<html><body>Order {order_id} placed successfully!</body></html>"
            else:
                return "Invalid card"
                
        elif payment_method == 'paypal':
            # PayPal implementation - copy&paste vom Credit Card Code
            # ... 200 Zeilen duplizierter Code
            pass
            
    elif action == 'admin':
        if not admin_logged_in:
            return "Not authorized"
            
        # Admin functions - alles hier drin
        sub_action = data.get('sub_action')
        
        if sub_action == 'add_product':
            name = data.get('name')
            price = data.get('price')
            
            # Direkte DB Manipulation
            cursor = db_connection.cursor()
            cursor.execute(f"INSERT INTO products (name, price) VALUES ('{name}', {price})")
            db_connection.commit()
            
            # Cache invalidieren - vergessen!
            
            return "Product added"
            
        elif sub_action == 'delete_user':
            user_id = data.get('user_id')
            
            # Keine Überprüfung ob User Orders hat!
            cursor = db_connection.cursor()
            cursor.execute(f"DELETE FROM users WHERE id={user_id}")
            db_connection.commit()
            
            return "User deleted"
            
        elif sub_action == 'view_logs':
            # Logs direkt aus File lesen und returnen
            with open('analytics.txt', 'r') as f:
                logs = f.read()
            return f"<pre>{logs}</pre>"
            
    elif action == 'search':
        query = data.get('q', '')
        
        # Search implementation - SQL Injection wieder
        cursor = db_connection.cursor()
        cursor.execute(f"SELECT * FROM products WHERE name LIKE '%{query}%'")
        
        results_html = "<h1>Search Results</h1>"
        for product in cursor.fetchall():
            # Wieder Preis-Logik dupliziert
            price = product[2]
            if datetime.datetime.now().weekday() == 4:
                price = price * 0.9
                
            results_html += f"<div>{product[1]} - ${price}</div>"
            
        return results_html
        
    # Noch 50 weitere elif Blöcke...
    
    return "Unknown action"

def send_email(to, subject, body):
    """Email Funktion - keine Error Handling"""
    global config
    
    # SMTP direkt hier
    server = smtplib.SMTP(config['smtp_server'], 587)
    server.starttls()
    server.login('shop@gmail.com', 'password123')  # Hardcoded credentials!
    
    message = f"Subject: {subject}\n\n{body}"
    server.sendmail('shop@gmail.com', to, message)
    server.quit()

def load_all_products():
    """Lädt alle Produkte in globalen Cache"""
    global products_cache, db_connection
    
    if not db_connection:
        return
        
    cursor = db_connection.cursor()
    cursor.execute("SELECT * FROM products")
    
    for product in cursor.fetchall():
        products_cache[product[0]] = {
            'name': product[1],
            'price': product[2]
        }

def generate_daily_report():
    """Report Generation - deplatziert und unstrukturiert"""
    global db_connection
    
    cursor = db_connection.cursor()
    cursor.execute("SELECT COUNT(*) FROM orders WHERE DATE(date) = CURDATE()")
    order_count = cursor.fetchone()[0]
    
    cursor.execute("SELECT SUM(total) FROM orders WHERE DATE(date) = CURDATE()")
    total_revenue = cursor.fetchone()[0] or 0
    
    # Report als HTML File speichern - warum HTML?
    with open('daily_report.html', 'w') as f:
        f.write(f"<html><body>")
        f.write(f"<h1>Daily Report</h1>")
        f.write(f"<p>Orders: {order_count}</p>")
        f.write(f"<p>Revenue: ${total_revenue}</p>")
        f.write(f"</body></html>")
    
    # Und nochmal als JSON - Duplikation
    with open('daily_report.json', 'w') as f:
        json.dump({
            'orders': order_count,
            'revenue': total_revenue
        }, f)

# Hilfsfunktionen wild verteilt
def hash_password(password):
    """Passwort hashen - aber wird nicht verwendet!"""
    return hashlib.md5(password.encode()).hexdigest()

def validate_email(email):
    """Email validierung - aber wird nicht verwendet!"""
    return '@' in email

def calculate_shipping(weight, country):
    """Shipping calculation - wird nirgends aufgerufen"""
    if country == 'DE':
        return weight * 2.5
    else:
        return weight * 5.0

# Main execution
if __name__ == "__main__":
    init_system()
    
    # Pseudo Web Server - keine echte Request Handling
    while True:
        try:
            # Simulate request
            request = input("Enter request: ")
            response = process_request(request)
            print(response)
        except KeyboardInterrupt:
            break
        except Exception as e:
            print(f"Error: {e}")
            # Error handling? Fehlanzeige!
            continue