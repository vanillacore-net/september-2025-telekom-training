package com.telekom.training.day2.customercaching.fixed;

import com.telekom.training.day2.customercaching.initial.Customer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy Pattern: RealSubject
 * The actual service that does the real work (database access)
 */
public class DatabaseCustomerService implements CustomerService {
    
    private final Map<String, Customer> database;
    private long dbCallCounter = 0;

    public DatabaseCustomerService() {
        this.database = initializeDatabase();
    }

    @Override
    public Customer getCustomer(String customerId) {
        simulateSlowDatabaseAccess();
        dbCallCounter++;
        
        System.out.println("🗄️  REAL DB CALL #" + dbCallCounter + " for customer: " + customerId);
        
        Customer customer = database.get(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found: " + customerId);
        }
        return customer;
    }

    @Override
    public Customer getCustomerWithTariffHistory(String customerId) {
        simulateSlowDatabaseAccess();
        simulateSlowDatabaseAccess(); // Complex query is slower
        dbCallCounter++;
        
        System.out.println("🗄️  REAL DB CALL #" + dbCallCounter + " with tariff history for: " + customerId);
        
        return getCustomer(customerId);
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        simulateSlowDatabaseAccess();
        dbCallCounter++;
        
        System.out.println("🗄️  REAL DB UPDATE #" + dbCallCounter + " for customer: " + customer.getCustomerId());
        
        database.put(customer.getCustomerId(), customer);
        return true;
    }

    public long getDbCallCount() {
        return dbCallCounter;
    }

    private void simulateSlowDatabaseAccess() {
        try {
            Thread.sleep(100); // Simulate 100ms database latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Map<String, Customer> initializeDatabase() {
        Map<String, Customer> db = new HashMap<>();
        
        db.put("CUST001", new Customer("CUST001", "Max Mustermann", "max@example.com",
            LocalDate.of(2020, 1, 15), Arrays.asList("Basic", "Premium"), "A"));
            
        db.put("CUST002", new Customer("CUST002", "Anna Schmidt", "anna@example.com",
            LocalDate.of(2019, 6, 10), Arrays.asList("Premium", "Business"), "B"));
            
        db.put("CUST003", new Customer("CUST003", "Peter Weber", "peter@example.com",
            LocalDate.of(2021, 3, 20), Arrays.asList("Basic"), "A"));
            
        return db;
    }
}