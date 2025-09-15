// GOOD EXAMPLE: DRY Principle - Don't Repeat Yourself
// Abstraction, Template Method Pattern, und Wiederverwendung statt Copy-Paste.

#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <memory>
#include <fstream>
#include <sstream>
#include <ctime>
#include <iomanip>
#include <functional>
#include <mysql/mysql.h>

using namespace std;

// ============================================================================
// Database Connection Manager - Einmal implementiert, überall verwendet
// ============================================================================

class DatabaseConnection {
private:
    MYSQL* connection;
    string host;
    string user;
    string password;
    string database;
    
public:
    DatabaseConnection(const string& h, const string& u, const string& p, const string& db)
        : host(h), user(u), password(p), database(db), connection(nullptr) {}
    
    ~DatabaseConnection() {
        if (connection) {
            mysql_close(connection);
        }
    }
    
    bool connect() {
        connection = mysql_init(NULL);
        if (!connection) {
            cerr << "mysql_init() failed" << endl;
            return false;
        }
        
        if (!mysql_real_connect(connection, host.c_str(), user.c_str(), 
                               password.c_str(), database.c_str(), 0, NULL, 0)) {
            cerr << "Connection failed: " << mysql_error(connection) << endl;
            mysql_close(connection);
            connection = nullptr;
            return false;
        }
        
        return true;
    }
    
    MYSQL_RES* executeQuery(const string& query) {
        if (!connection) {
            cerr << "Not connected to database" << endl;
            return nullptr;
        }
        
        if (mysql_query(connection, query.c_str())) {
            cerr << "Query failed: " << mysql_error(connection) << endl;
            return nullptr;
        }
        
        return mysql_store_result(connection);
    }
};

// ============================================================================
// Report Data Structure - Gemeinsame Datenstruktur
// ============================================================================

struct ReportColumn {
    string name;
    string type; // "string", "number", "currency", "date"
    
    ReportColumn(const string& n, const string& t = "string") 
        : name(n), type(t) {}
};

struct ReportRow {
    vector<string> values;
};

struct ReportData {
    string title;
    vector<ReportColumn> columns;
    vector<ReportRow> rows;
    map<string, string> summary;
};

// ============================================================================
// Abstract Report Generator - Template Method Pattern
// ============================================================================

class ReportGenerator {
protected:
    shared_ptr<DatabaseConnection> db;
    
    // Template Method Pattern - definiert den Ablauf
    virtual string getReportTitle() const = 0;
    virtual string getQuery() const = 0;
    virtual vector<ReportColumn> getColumns() const = 0;
    virtual void processRow(MYSQL_ROW row, ReportRow& reportRow) = 0;
    virtual map<string, string> calculateSummary(const vector<ReportRow>& rows) = 0;
    
public:
    ReportGenerator(shared_ptr<DatabaseConnection> database) : db(database) {}
    
    // Template Method - der gemeinsame Ablauf
    ReportData generateReport() {
        ReportData report;
        report.title = getReportTitle();
        report.columns = getColumns();
        
        // Execute query
        MYSQL_RES* result = db->executeQuery(getQuery());
        if (!result) {
            return report;
        }
        
        // Process results
        MYSQL_ROW row;
        while ((row = mysql_fetch_row(result))) {
            ReportRow reportRow;
            processRow(row, reportRow);
            report.rows.push_back(reportRow);
        }
        
        mysql_free_result(result);
        
        // Calculate summary
        report.summary = calculateSummary(report.rows);
        
        return report;
    }
};

// ============================================================================
// Report Formatters - Strategy Pattern für verschiedene Ausgabeformate
// ============================================================================

class ReportFormatter {
public:
    virtual ~ReportFormatter() = default;
    virtual void format(const ReportData& data, const string& filename) = 0;
    
protected:
    string getCurrentDateTime() {
        time_t now = time(0);
        char buffer[80];
        strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", localtime(&now));
        return string(buffer);
    }
};

// HTML Formatter - Einmal implementiert für alle Reports
class HtmlFormatter : public ReportFormatter {
private:
    string getHtmlStyle() {
        return R"(
            <style>
                table { border-collapse: collapse; width: 100%; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #4CAF50; color: white; }
                tr:nth-child(even) { background-color: #f2f2f2; }
                .summary { margin-top: 20px; padding: 10px; background-color: #f9f9f9; }
            </style>
        )";
    }
    
public:
    void format(const ReportData& data, const string& filename) override {
        ofstream file(filename);
        if (!file.is_open()) {
            cerr << "Unable to open file: " << filename << endl;
            return;
        }
        
        file << "<!DOCTYPE html>\n";
        file << "<html>\n";
        file << "<head>\n";
        file << "<title>" << data.title << "</title>\n";
        file << getHtmlStyle() << "\n";
        file << "</head>\n";
        file << "<body>\n";
        file << "<h1>" << data.title << "</h1>\n";
        file << "<p>Generated on: " << getCurrentDateTime() << "</p>\n";
        
        // Table
        file << "<table>\n";
        file << "<tr>\n";
        for (const auto& col : data.columns) {
            file << "<th>" << col.name << "</th>\n";
        }
        file << "</tr>\n";
        
        for (const auto& row : data.rows) {
            file << "<tr>\n";
            for (size_t i = 0; i < row.values.size(); ++i) {
                file << "<td>";
                if (i < data.columns.size() && data.columns[i].type == "currency") {
                    file << "$";
                }
                file << row.values[i] << "</td>\n";
            }
            file << "</tr>\n";
        }
        file << "</table>\n";
        
        // Summary
        if (!data.summary.empty()) {
            file << "<div class='summary'>\n";
            file << "<h2>Summary</h2>\n";
            for (const auto& [key, value] : data.summary) {
                file << "<p>" << key << ": " << value << "</p>\n";
            }
            file << "</div>\n";
        }
        
        file << "</body>\n";
        file << "</html>\n";
        
        file.close();
        cout << data.title << " generated successfully in " << filename << endl;
    }
};

// CSV Formatter - Auch wiederverwendbar
class CsvFormatter : public ReportFormatter {
public:
    void format(const ReportData& data, const string& filename) override {
        ofstream file(filename);
        if (!file.is_open()) {
            cerr << "Unable to open file: " << filename << endl;
            return;
        }
        
        // Header
        for (size_t i = 0; i < data.columns.size(); ++i) {
            file << data.columns[i].name;
            if (i < data.columns.size() - 1) file << ",";
        }
        file << "\n";
        
        // Data
        for (const auto& row : data.rows) {
            for (size_t i = 0; i < row.values.size(); ++i) {
                file << row.values[i];
                if (i < row.values.size() - 1) file << ",";
            }
            file << "\n";
        }
        
        file.close();
        cout << data.title << " exported to " << filename << endl;
    }
};

// ============================================================================
// Konkrete Report Implementierungen - Nur die Unterschiede
// ============================================================================

class SalesReport : public ReportGenerator {
private:
    double totalRevenue = 0.0;
    int totalQuantity = 0;
    
public:
    using ReportGenerator::ReportGenerator;
    
    string getReportTitle() const override {
        return "Sales Report";
    }
    
    string getQuery() const override {
        return "SELECT product_name, quantity, price, sale_date FROM sales "
               "WHERE sale_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
    }
    
    vector<ReportColumn> getColumns() const override {
        return {
            {"Product Name", "string"},
            {"Quantity", "number"},
            {"Price", "currency"},
            {"Sale Date", "date"}
        };
    }
    
    void processRow(MYSQL_ROW row, ReportRow& reportRow) override {
        reportRow.values.push_back(row[0] ? row[0] : "N/A");
        reportRow.values.push_back(row[1] ? row[1] : "0");
        reportRow.values.push_back(row[2] ? row[2] : "0.00");
        reportRow.values.push_back(row[3] ? row[3] : "N/A");
        
        // Update totals
        if (row[1]) totalQuantity += atoi(row[1]);
        if (row[2] && row[1]) {
            totalRevenue += atof(row[2]) * atoi(row[1]);
        }
    }
    
    map<string, string> calculateSummary(const vector<ReportRow>& rows) override {
        stringstream ss;
        ss << fixed << setprecision(2);
        
        map<string, string> summary;
        summary["Total Quantity Sold"] = to_string(totalQuantity);
        ss << totalRevenue;
        summary["Total Revenue"] = "$" + ss.str();
        
        return summary;
    }
};

class InventoryReport : public ReportGenerator {
private:
    int lowStockCount = 0;
    int overStockCount = 0;
    
public:
    using ReportGenerator::ReportGenerator;
    
    string getReportTitle() const override {
        return "Inventory Report";
    }
    
    string getQuery() const override {
        return "SELECT product_name, current_stock, min_stock, max_stock FROM inventory";
    }
    
    vector<ReportColumn> getColumns() const override {
        return {
            {"Product Name", "string"},
            {"Current Stock", "number"},
            {"Min Stock", "number"},
            {"Max Stock", "number"}
        };
    }
    
    void processRow(MYSQL_ROW row, ReportRow& reportRow) override {
        reportRow.values.push_back(row[0] ? row[0] : "N/A");
        reportRow.values.push_back(row[1] ? row[1] : "0");
        reportRow.values.push_back(row[2] ? row[2] : "0");
        reportRow.values.push_back(row[3] ? row[3] : "0");
        
        // Check stock levels
        int current = row[1] ? atoi(row[1]) : 0;
        int min = row[2] ? atoi(row[2]) : 0;
        int max = row[3] ? atoi(row[3]) : 0;
        
        if (current < min) lowStockCount++;
        if (current > max) overStockCount++;
    }
    
    map<string, string> calculateSummary(const vector<ReportRow>& rows) override {
        map<string, string> summary;
        summary["Products with Low Stock"] = to_string(lowStockCount);
        summary["Products with Overstock"] = to_string(overStockCount);
        summary["Total Products"] = to_string(rows.size());
        
        return summary;
    }
};

class CustomerReport : public ReportGenerator {
private:
    int totalCustomers = 0;
    double totalPurchases = 0.0;
    
public:
    using ReportGenerator::ReportGenerator;
    
    string getReportTitle() const override {
        return "Customer Report";
    }
    
    string getQuery() const override {
        return "SELECT customer_name, email, total_purchases, last_purchase_date FROM customers";
    }
    
    vector<ReportColumn> getColumns() const override {
        return {
            {"Customer Name", "string"},
            {"Email", "string"},
            {"Total Purchases", "currency"},
            {"Last Purchase", "date"}
        };
    }
    
    void processRow(MYSQL_ROW row, ReportRow& reportRow) override {
        reportRow.values.push_back(row[0] ? row[0] : "N/A");
        reportRow.values.push_back(row[1] ? row[1] : "N/A");
        reportRow.values.push_back(row[2] ? row[2] : "0.00");
        reportRow.values.push_back(row[3] ? row[3] : "N/A");
        
        totalCustomers++;
        if (row[2]) totalPurchases += atof(row[2]);
    }
    
    map<string, string> calculateSummary(const vector<ReportRow>& rows) override {
        stringstream ss;
        ss << fixed << setprecision(2);
        
        map<string, string> summary;
        summary["Total Customers"] = to_string(totalCustomers);
        ss << totalPurchases;
        summary["Total Purchase Value"] = "$" + ss.str();
        
        return summary;
    }
};

// ============================================================================
// Report Manager - Facade Pattern
// ============================================================================

class ReportManager {
private:
    shared_ptr<DatabaseConnection> db;
    unique_ptr<ReportFormatter> htmlFormatter;
    unique_ptr<ReportFormatter> csvFormatter;
    
    void sendEmailNotification(const string& subject, const string& body) {
        cout << "Email sent - Subject: " << subject << endl;
        // Email implementation würde hier stehen
    }
    
public:
    ReportManager(const string& host, const string& user, 
                 const string& password, const string& database) {
        db = make_shared<DatabaseConnection>(host, user, password, database);
        htmlFormatter = make_unique<HtmlFormatter>();
        csvFormatter = make_unique<CsvFormatter>();
    }
    
    bool initialize() {
        return db->connect();
    }
    
    void generateAllReports() {
        // Sales Report
        generateReport<SalesReport>("sales_report.html", "sales_report.csv");
        
        // Inventory Report
        generateReport<InventoryReport>("inventory_report.html", "inventory_report.csv");
        
        // Customer Report
        generateReport<CustomerReport>("customer_report.html", "customer_report.csv");
    }
    
    template<typename ReportType>
    void generateReport(const string& htmlFile, const string& csvFile) {
        ReportType report(db);
        ReportData data = report.generateReport();
        
        // Generate both formats
        htmlFormatter->format(data, htmlFile);
        csvFormatter->format(data, csvFile);
        
        // Send notification
        sendEmailNotification(
            data.title + " Generated",
            "The " + data.title + " has been generated successfully."
        );
    }
};

// ============================================================================
// Main - Clean and Simple
// ============================================================================

int main() {
    ReportManager manager("localhost", "root", "password", "company_db");
    
    if (!manager.initialize()) {
        cerr << "Failed to initialize report manager" << endl;
        return 1;
    }
    
    manager.generateAllReports();
    
    return 0;
}

// VORTEILE:
// - Keine Code-Duplikation
// - Änderungen müssen nur an einer Stelle gemacht werden
// - Neue Reports können einfach hinzugefügt werden
// - Neue Ausgabeformate (JSON, XML) können einfach ergänzt werden
// - Testbar und wartbar
// - SOLID Principles befolgt