// BAD EXAMPLE: Copy-Paste Programming Anti-Pattern
// Massives Duplizieren von Code-Blöcken statt Wiederverwendung.
// Inkonsistente Änderungen, Bugs müssen mehrfach gefixt werden.

#include <iostream>
#include <string>
#include <vector>
#include <fstream>
#include <sstream>
#include <ctime>
#include <iomanip>
#include <mysql/mysql.h>

using namespace std;

class ReportGeneratorBad {
private:
    MYSQL* connection;
    string dbHost;
    string dbUser;
    string dbPassword;
    string dbName;
    
public:
    ReportGeneratorBad(const string& host, const string& user, 
                      const string& password, const string& database) 
        : dbHost(host), dbUser(user), dbPassword(password), dbName(database) {
        connection = mysql_init(NULL);
    }
    
    // ============================================================================
    // PROBLEM: Jede Report-Funktion ist eine Kopie mit minimalen Änderungen
    // ============================================================================
    
    // Sales Report - Original Implementation
    void generateSalesReport() {
        // Database Connection - COPY 1
        MYSQL* conn = mysql_init(NULL);
        if (conn == NULL) {
            cerr << "mysql_init() failed" << endl;
            return;
        }
        
        if (mysql_real_connect(conn, dbHost.c_str(), dbUser.c_str(), 
                              dbPassword.c_str(), dbName.c_str(), 0, NULL, 0) == NULL) {
            cerr << "Connection failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        // Query execution - specific to sales
        string query = "SELECT product_name, quantity, price, sale_date FROM sales "
                      "WHERE sale_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
        
        if (mysql_query(conn, query.c_str())) {
            cerr << "Query failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        MYSQL_RES* result = mysql_store_result(conn);
        if (result == NULL) {
            cerr << "mysql_store_result() failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        // HTML Generation - COPY 1
        stringstream html;
        html << "<!DOCTYPE html>" << endl;
        html << "<html>" << endl;
        html << "<head>" << endl;
        html << "<title>Sales Report</title>" << endl;
        html << "<style>" << endl;
        html << "table { border-collapse: collapse; width: 100%; }" << endl;
        html << "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" << endl;
        html << "th { background-color: #4CAF50; color: white; }" << endl;
        html << "tr:nth-child(even) { background-color: #f2f2f2; }" << endl;
        html << "</style>" << endl;
        html << "</head>" << endl;
        html << "<body>" << endl;
        html << "<h1>Sales Report</h1>" << endl;
        html << "<p>Generated on: " << getCurrentDateTime() << "</p>" << endl;
        html << "<table>" << endl;
        html << "<tr>" << endl;
        html << "<th>Product Name</th>" << endl;
        html << "<th>Quantity</th>" << endl;
        html << "<th>Price</th>" << endl;
        html << "<th>Sale Date</th>" << endl;
        html << "</tr>" << endl;
        
        // Data Processing - specific to sales
        MYSQL_ROW row;
        double totalRevenue = 0.0;
        int totalQuantity = 0;
        
        while ((row = mysql_fetch_row(result))) {
            html << "<tr>" << endl;
            html << "<td>" << (row[0] ? row[0] : "N/A") << "</td>" << endl;
            html << "<td>" << (row[1] ? row[1] : "0") << "</td>" << endl;
            html << "<td>$" << (row[2] ? row[2] : "0.00") << "</td>" << endl;
            html << "<td>" << (row[3] ? row[3] : "N/A") << "</td>" << endl;
            html << "</tr>" << endl;
            
            // Calculate totals
            if (row[1]) totalQuantity += atoi(row[1]);
            if (row[2]) totalRevenue += atof(row[2]) * (row[1] ? atoi(row[1]) : 1);
        }
        
        // Summary section - COPY 1
        html << "</table>" << endl;
        html << "<h2>Summary</h2>" << endl;
        html << "<p>Total Quantity Sold: " << totalQuantity << "</p>" << endl;
        html << "<p>Total Revenue: $" << fixed << setprecision(2) << totalRevenue << "</p>" << endl;
        html << "</body>" << endl;
        html << "</html>" << endl;
        
        // File Writing - COPY 1
        ofstream file("sales_report.html");
        if (file.is_open()) {
            file << html.str();
            file.close();
            cout << "Sales report generated successfully!" << endl;
        } else {
            cerr << "Unable to open file for writing" << endl;
        }
        
        // Cleanup - COPY 1
        mysql_free_result(result);
        mysql_close(conn);
        
        // Email notification - COPY 1
        sendEmailNotification("admin@company.com", "Sales Report Generated", 
                            "The monthly sales report has been generated.");
    }
    
    // Inventory Report - COPY & PASTE mit kleinen Änderungen
    void generateInventoryReport() {
        // Database Connection - COPY 2 (EXAKT GLEICH!)
        MYSQL* conn = mysql_init(NULL);
        if (conn == NULL) {
            cerr << "mysql_init() failed" << endl;
            return;
        }
        
        if (mysql_real_connect(conn, dbHost.c_str(), dbUser.c_str(), 
                              dbPassword.c_str(), dbName.c_str(), 0, NULL, 0) == NULL) {
            cerr << "Connection failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        // Query execution - nur Query anders
        string query = "SELECT product_name, current_stock, min_stock, max_stock FROM inventory";
        
        if (mysql_query(conn, query.c_str())) {
            cerr << "Query failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        MYSQL_RES* result = mysql_store_result(conn);
        if (result == NULL) {
            cerr << "mysql_store_result() failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        // HTML Generation - COPY 2 (Fast identisch, nur Title anders!)
        stringstream html;
        html << "<!DOCTYPE html>" << endl;
        html << "<html>" << endl;
        html << "<head>" << endl;
        html << "<title>Inventory Report</title>" << endl;  // Nur diese Zeile anders
        html << "<style>" << endl;
        html << "table { border-collapse: collapse; width: 100%; }" << endl;
        html << "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" << endl;
        html << "th { background-color: #4CAF50; color: white; }" << endl;
        html << "tr:nth-child(even) { background-color: #f2f2f2; }" << endl;
        html << "</style>" << endl;
        html << "</head>" << endl;
        html << "<body>" << endl;
        html << "<h1>Inventory Report</h1>" << endl;  // Und diese
        html << "<p>Generated on: " << getCurrentDateTime() << "</p>" << endl;
        html << "<table>" << endl;
        html << "<tr>" << endl;
        html << "<th>Product Name</th>" << endl;  // Andere Spalten
        html << "<th>Current Stock</th>" << endl;
        html << "<th>Min Stock</th>" << endl;
        html << "<th>Max Stock</th>" << endl;
        html << "</tr>" << endl;
        
        // Data Processing - leicht anders
        MYSQL_ROW row;
        int lowStockCount = 0;
        int overStockCount = 0;
        
        while ((row = mysql_fetch_row(result))) {
            html << "<tr>" << endl;
            html << "<td>" << (row[0] ? row[0] : "N/A") << "</td>" << endl;
            html << "<td>" << (row[1] ? row[1] : "0") << "</td>" << endl;
            html << "<td>" << (row[2] ? row[2] : "0") << "</td>" << endl;
            html << "<td>" << (row[3] ? row[3] : "0") << "</td>" << endl;
            html << "</tr>" << endl;
            
            // Different calculations
            int current = row[1] ? atoi(row[1]) : 0;
            int min = row[2] ? atoi(row[2]) : 0;
            int max = row[3] ? atoi(row[3]) : 0;
            
            if (current < min) lowStockCount++;
            if (current > max) overStockCount++;
        }
        
        // Summary section - COPY 2 (Struktur gleich, Inhalt anders)
        html << "</table>" << endl;
        html << "<h2>Summary</h2>" << endl;
        html << "<p>Products with Low Stock: " << lowStockCount << "</p>" << endl;
        html << "<p>Products with Overstock: " << overStockCount << "</p>" << endl;
        html << "</body>" << endl;
        html << "</html>" << endl;
        
        // File Writing - COPY 2 (Fast identisch!)
        ofstream file("inventory_report.html");  // Nur Filename anders
        if (file.is_open()) {
            file << html.str();
            file.close();
            cout << "Inventory report generated successfully!" << endl;
        } else {
            cerr << "Unable to open file for writing" << endl;
        }
        
        // Cleanup - COPY 2 (EXAKT GLEICH!)
        mysql_free_result(result);
        mysql_close(conn);
        
        // Email notification - COPY 2 (Fast gleich)
        sendEmailNotification("admin@company.com", "Inventory Report Generated", 
                            "The inventory report has been generated.");
    }
    
    // Customer Report - NOCH EINE KOPIE!
    void generateCustomerReport() {
        // Database Connection - COPY 3 (WIEDER EXAKT GLEICH!)
        MYSQL* conn = mysql_init(NULL);
        if (conn == NULL) {
            cerr << "mysql_init() failed" << endl;
            return;
        }
        
        if (mysql_real_connect(conn, dbHost.c_str(), dbUser.c_str(), 
                              dbPassword.c_str(), dbName.c_str(), 0, NULL, 0) == NULL) {
            cerr << "Connection failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        // Query execution
        string query = "SELECT customer_name, email, total_purchases, last_purchase_date FROM customers";
        
        if (mysql_query(conn, query.c_str())) {
            cerr << "Query failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        MYSQL_RES* result = mysql_store_result(conn);
        if (result == NULL) {
            cerr << "mysql_store_result() failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        // HTML Generation - COPY 3 (Wieder fast identisch!)
        stringstream html;
        html << "<!DOCTYPE html>" << endl;
        html << "<html>" << endl;
        html << "<head>" << endl;
        html << "<title>Customer Report</title>" << endl;
        html << "<style>" << endl;
        html << "table { border-collapse: collapse; width: 100%; }" << endl;
        html << "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" << endl;
        html << "th { background-color: #4CAF50; color: white; }" << endl;
        html << "tr:nth-child(even) { background-color: #f2f2f2; }" << endl;
        html << "</style>" << endl;
        html << "</head>" << endl;
        html << "<body>" << endl;
        html << "<h1>Customer Report</h1>" << endl;
        html << "<p>Generated on: " << getCurrentDateTime() << "</p>" << endl;
        html << "<table>" << endl;
        html << "<tr>" << endl;
        html << "<th>Customer Name</th>" << endl;
        html << "<th>Email</th>" << endl;
        html << "<th>Total Purchases</th>" << endl;
        html << "<th>Last Purchase</th>" << endl;
        html << "</tr>" << endl;
        
        // Data Processing
        MYSQL_ROW row;
        int totalCustomers = 0;
        double totalPurchases = 0.0;
        
        while ((row = mysql_fetch_row(result))) {
            html << "<tr>" << endl;
            html << "<td>" << (row[0] ? row[0] : "N/A") << "</td>" << endl;
            html << "<td>" << (row[1] ? row[1] : "N/A") << "</td>" << endl;
            html << "<td>$" << (row[2] ? row[2] : "0.00") << "</td>" << endl;
            html << "<td>" << (row[3] ? row[3] : "N/A") << "</td>" << endl;
            html << "</tr>" << endl;
            
            totalCustomers++;
            if (row[2]) totalPurchases += atof(row[2]);
        }
        
        // Summary section - COPY 3
        html << "</table>" << endl;
        html << "<h2>Summary</h2>" << endl;
        html << "<p>Total Customers: " << totalCustomers << "</p>" << endl;
        html << "<p>Total Purchase Value: $" << fixed << setprecision(2) << totalPurchases << "</p>" << endl;
        html << "</body>" << endl;
        html << "</html>" << endl;
        
        // File Writing - COPY 3
        ofstream file("customer_report.html");
        if (file.is_open()) {
            file << html.str();
            file.close();
            cout << "Customer report generated successfully!" << endl;
        } else {
            cerr << "Unable to open file for writing" << endl;
        }
        
        // Cleanup - COPY 3
        mysql_free_result(result);
        mysql_close(conn);
        
        // Email notification - COPY 3
        sendEmailNotification("admin@company.com", "Customer Report Generated", 
                            "The customer report has been generated.");
    }
    
    // Financial Report - UND NOCH EINE!
    void generateFinancialReport() {
        // Database Connection - COPY 4 (Immer noch gleich!)
        MYSQL* conn = mysql_init(NULL);
        if (conn == NULL) {
            cerr << "mysql_init() failed" << endl;
            return;
        }
        
        if (mysql_real_connect(conn, dbHost.c_str(), dbUser.c_str(), 
                              dbPassword.c_str(), dbName.c_str(), 0, NULL, 0) == NULL) {
            cerr << "Connection failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        // Query execution
        string query = "SELECT category, revenue, expenses, profit FROM financial_summary";
        
        if (mysql_query(conn, query.c_str())) {
            cerr << "Query failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        MYSQL_RES* result = mysql_store_result(conn);
        if (result == NULL) {
            cerr << "mysql_store_result() failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        // HTML Generation - COPY 4
        stringstream html;
        html << "<!DOCTYPE html>" << endl;
        html << "<html>" << endl;
        html << "<head>" << endl;
        html << "<title>Financial Report</title>" << endl;
        html << "<style>" << endl;
        html << "table { border-collapse: collapse; width: 100%; }" << endl;
        html << "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" << endl;
        html << "th { background-color: #4CAF50; color: white; }" << endl;
        html << "tr:nth-child(even) { background-color: #f2f2f2; }" << endl;
        html << "</style>" << endl;
        html << "</head>" << endl;
        html << "<body>" << endl;
        html << "<h1>Financial Report</h1>" << endl;
        html << "<p>Generated on: " << getCurrentDateTime() << "</p>" << endl;
        html << "<table>" << endl;
        html << "<tr>" << endl;
        html << "<th>Category</th>" << endl;
        html << "<th>Revenue</th>" << endl;
        html << "<th>Expenses</th>" << endl;
        html << "<th>Profit</th>" << endl;
        html << "</tr>" << endl;
        
        // Data Processing
        MYSQL_ROW row;
        double totalRevenue = 0.0;
        double totalExpenses = 0.0;
        double totalProfit = 0.0;
        
        while ((row = mysql_fetch_row(result))) {
            html << "<tr>" << endl;
            html << "<td>" << (row[0] ? row[0] : "N/A") << "</td>" << endl;
            html << "<td>$" << (row[1] ? row[1] : "0.00") << "</td>" << endl;
            html << "<td>$" << (row[2] ? row[2] : "0.00") << "</td>" << endl;
            html << "<td>$" << (row[3] ? row[3] : "0.00") << "</td>" << endl;
            html << "</tr>" << endl;
            
            if (row[1]) totalRevenue += atof(row[1]);
            if (row[2]) totalExpenses += atof(row[2]);
            if (row[3]) totalProfit += atof(row[3]);
        }
        
        // Summary section - COPY 4
        html << "</table>" << endl;
        html << "<h2>Summary</h2>" << endl;
        html << "<p>Total Revenue: $" << fixed << setprecision(2) << totalRevenue << "</p>" << endl;
        html << "<p>Total Expenses: $" << fixed << setprecision(2) << totalExpenses << "</p>" << endl;
        html << "<p>Total Profit: $" << fixed << setprecision(2) << totalProfit << "</p>" << endl;
        html << "</body>" << endl;
        html << "</html>" << endl;
        
        // File Writing - COPY 4
        ofstream file("financial_report.html");
        if (file.is_open()) {
            file << html.str();
            file.close();
            cout << "Financial report generated successfully!" << endl;
        } else {
            cerr << "Unable to open file for writing" << endl;
        }
        
        // Cleanup - COPY 4
        mysql_free_result(result);
        mysql_close(conn);
        
        // Email notification - COPY 4
        sendEmailNotification("admin@company.com", "Financial Report Generated", 
                            "The financial report has been generated.");
    }
    
    // Noch mehr Copy-Paste für CSV Export...
    void exportSalesReportCSV() {
        // Database Connection - COPY 5 (Schon wieder!)
        MYSQL* conn = mysql_init(NULL);
        if (conn == NULL) {
            cerr << "mysql_init() failed" << endl;
            return;
        }
        
        if (mysql_real_connect(conn, dbHost.c_str(), dbUser.c_str(), 
                              dbPassword.c_str(), dbName.c_str(), 0, NULL, 0) == NULL) {
            cerr << "Connection failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        string query = "SELECT product_name, quantity, price, sale_date FROM sales";
        
        if (mysql_query(conn, query.c_str())) {
            cerr << "Query failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        MYSQL_RES* result = mysql_store_result(conn);
        if (result == NULL) {
            cerr << "mysql_store_result() failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        // CSV Generation - Ähnlich wie HTML, aber anderes Format
        ofstream csv("sales_report.csv");
        if (!csv.is_open()) {
            cerr << "Unable to open CSV file" << endl;
            mysql_free_result(result);
            mysql_close(conn);
            return;
        }
        
        // CSV Header
        csv << "Product Name,Quantity,Price,Sale Date" << endl;
        
        // CSV Data
        MYSQL_ROW row;
        while ((row = mysql_fetch_row(result))) {
            csv << (row[0] ? row[0] : "N/A") << ",";
            csv << (row[1] ? row[1] : "0") << ",";
            csv << (row[2] ? row[2] : "0.00") << ",";
            csv << (row[3] ? row[3] : "N/A") << endl;
        }
        
        csv.close();
        cout << "Sales CSV exported successfully!" << endl;
        
        mysql_free_result(result);
        mysql_close(conn);
    }
    
    void exportInventoryReportCSV() {
        // COPY 6 - Fast identisch mit exportSalesReportCSV
        MYSQL* conn = mysql_init(NULL);
        if (conn == NULL) {
            cerr << "mysql_init() failed" << endl;
            return;
        }
        
        if (mysql_real_connect(conn, dbHost.c_str(), dbUser.c_str(), 
                              dbPassword.c_str(), dbName.c_str(), 0, NULL, 0) == NULL) {
            cerr << "Connection failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        string query = "SELECT product_name, current_stock, min_stock, max_stock FROM inventory";
        
        if (mysql_query(conn, query.c_str())) {
            cerr << "Query failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        MYSQL_RES* result = mysql_store_result(conn);
        if (result == NULL) {
            cerr << "mysql_store_result() failed: " << mysql_error(conn) << endl;
            mysql_close(conn);
            return;
        }
        
        ofstream csv("inventory_report.csv");
        if (!csv.is_open()) {
            cerr << "Unable to open CSV file" << endl;
            mysql_free_result(result);
            mysql_close(conn);
            return;
        }
        
        csv << "Product Name,Current Stock,Min Stock,Max Stock" << endl;
        
        MYSQL_ROW row;
        while ((row = mysql_fetch_row(result))) {
            csv << (row[0] ? row[0] : "N/A") << ",";
            csv << (row[1] ? row[1] : "0") << ",";
            csv << (row[2] ? row[2] : "0") << ",";
            csv << (row[3] ? row[3] : "0") << endl;
        }
        
        csv.close();
        cout << "Inventory CSV exported successfully!" << endl;
        
        mysql_free_result(result);
        mysql_close(conn);
    }
    
private:
    // Helper Funktionen - wenigstens diese sind nicht dupliziert
    string getCurrentDateTime() {
        time_t now = time(0);
        char buffer[80];
        strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", localtime(&now));
        return string(buffer);
    }
    
    void sendEmailNotification(const string& to, const string& subject, const string& body) {
        // Email implementation
        cout << "Email sent to: " << to << " - Subject: " << subject << endl;
    }
};

// PROBLEM: Wenn sich etwas ändert (z.B. DB Connection String, HTML Style, Error Handling),
// muss es in ALLEN Funktionen geändert werden!
// Bugs müssen mehrfach gefixt werden!
// Code-Basis explodiert!

int main() {
    ReportGeneratorBad generator("localhost", "root", "password", "company_db");
    
    // Alle Reports generieren
    generator.generateSalesReport();
    generator.generateInventoryReport();
    generator.generateCustomerReport();
    generator.generateFinancialReport();
    
    // CSV Exports
    generator.exportSalesReportCSV();
    generator.exportInventoryReportCSV();
    
    return 0;
}