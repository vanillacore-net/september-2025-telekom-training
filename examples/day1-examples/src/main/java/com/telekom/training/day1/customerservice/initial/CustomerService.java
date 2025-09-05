package com.telekom.training.day1.customerservice.initial;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * ANTI-PATTERN: God Class
 * 
 * Diese Klasse verletzt das Single Responsibility Principle massiv.
 * Sie ist für alles zuständig: Kundenbetreuung, Vertragsmanagement,
 * Tarifberechnungen, Rechnungsstellung, Support-Tickets, etc.
 * 
 * Code-Smells:
 * - God Class (über 150 Zeilen)
 * - Long Methods (handleCustomerRequest über 50 Zeilen)
 * - Switch Statements überall
 * - Hardcoded Business Logic
 * - Keine Abstraktion
 * - Schwer testbar
 * - Schwer erweiterbar
 */
public class CustomerService {
    
    private Map<String, Customer> customers = new HashMap<>();
    private Map<String, Contract> contracts = new HashMap<>();
    private List<Ticket> tickets = new ArrayList<>();
    private List<Invoice> invoices = new ArrayList<>();
    
    // LONG METHOD - über 50 Zeilen!
    public String handleCustomerRequest(String customerId, String requestType, Map<String, Object> params) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            return "ERROR: Customer not found";
        }
        
        // GIANT SWITCH STATEMENT - Violation of Open/Closed Principle
        switch (requestType.toUpperCase()) {
            case "DSL_ACTIVATION":
                // Hardcoded DSL Logic - sollte abstrahiert sein
                if (!customer.getAddress().contains("Berlin") && !customer.getAddress().contains("Hamburg")) {
                    return "ERROR: DSL nicht verfügbar in Ihrer Region";
                }
                Contract dslContract = new Contract();
                dslContract.setCustomerId(customerId);
                dslContract.setType("DSL");
                dslContract.setSpeed((String) params.get("speed"));
                dslContract.setMonthlyFee(calculateDslFee((String) params.get("speed")));
                dslContract.setActivationDate(LocalDateTime.now());
                contracts.put(generateContractId(), dslContract);
                
                // Noch mehr hardcoded Logic für DSL Setup
                if ("100".equals(params.get("speed"))) {
                    // Setup Router
                    sendRouterToCustomer(customer);
                    scheduleInstallation(customer, "DSL_TECHNIKER");
                } else if ("250".equals(params.get("speed"))) {
                    // Premium Setup
                    sendPremiumRouterToCustomer(customer);
                    scheduleInstallation(customer, "DSL_PREMIUM_TECHNIKER");
                }
                return "DSL Vertrag erstellt und Techniker beauftragt";
                
            case "MOBILE_ACTIVATION":
                // Hardcoded Mobile Logic
                Contract mobileContract = new Contract();
                mobileContract.setCustomerId(customerId);
                mobileContract.setType("MOBILE");
                mobileContract.setDataVolume((String) params.get("dataVolume"));
                mobileContract.setMonthlyFee(calculateMobileFee((String) params.get("dataVolume")));
                mobileContract.setActivationDate(LocalDateTime.now());
                contracts.put(generateContractId(), mobileContract);
                
                // SIM Karte Logic hardcoded
                String simType = (String) params.get("simType");
                if ("NANO".equals(simType)) {
                    sendNanoSimToCustomer(customer);
                } else if ("MICRO".equals(simType)) {
                    sendMicroSimToCustomer(customer);
                } else {
                    sendStandardSimToCustomer(customer);
                }
                return "Mobile Vertrag erstellt und SIM verschickt";
                
            case "FESTNETZ_ACTIVATION":
                // Hardcoded Festnetz Logic
                if (!hasExistingLine(customer.getAddress())) {
                    return "ERROR: Keine Telefonleitung verfügbar";
                }
                Contract festnetzContract = new Contract();
                festnetzContract.setCustomerId(customerId);
                festnetzContract.setType("FESTNETZ");
                festnetzContract.setFeatures((String) params.get("features"));
                festnetzContract.setMonthlyFee(calculateFestnetzFee((String) params.get("features")));
                festnetzContract.setActivationDate(LocalDateTime.now());
                contracts.put(generateContractId(), festnetzContract);
                
                return "Festnetz Vertrag erstellt";
                
            case "BILLING_INQUIRY":
                return handleBillingInquiry(customerId, (String) params.get("month"));
                
            case "TECHNICAL_SUPPORT":
                return handleTechnicalSupport(customerId, (String) params.get("issue"));
                
            case "CONTRACT_CHANGE":
                return handleContractChange(customerId, (String) params.get("changeType"), params);
                
            default:
                return "ERROR: Unknown request type";
        }
    }
    
    // Mehr hardcoded Business Logic - sollte abstrahiert werden
    private double calculateDslFee(String speed) {
        // HARDCODED - sollte konfigurierbar sein
        switch (speed) {
            case "50": return 29.95;
            case "100": return 39.95;
            case "250": return 49.95;
            default: return 19.95;
        }
    }
    
    private double calculateMobileFee(String dataVolume) {
        // HARDCODED - sollte konfigurierbar sein
        switch (dataVolume) {
            case "5GB": return 19.95;
            case "10GB": return 29.95;
            case "20GB": return 39.95;
            case "UNLIMITED": return 59.95;
            default: return 9.95;
        }
    }
    
    private double calculateFestnetzFee(String features) {
        // HARDCODED - sollte konfigurierbar sein
        double baseFee = 19.95;
        if (features.contains("INTERNATIONAL")) baseFee += 10.0;
        if (features.contains("VOICEMAIL")) baseFee += 5.0;
        return baseFee;
    }
    
    // Noch mehr Methods die eigentlich nicht hierhin gehören
    private void sendRouterToCustomer(Customer customer) {
        System.out.println("Standard Router verschickt an: " + customer.getAddress());
    }
    
    private void sendPremiumRouterToCustomer(Customer customer) {
        System.out.println("Premium Router verschickt an: " + customer.getAddress());
    }
    
    private void scheduleInstallation(Customer customer, String technicianType) {
        System.out.println("Installation geplant für: " + customer.getName() + " mit " + technicianType);
    }
    
    private void sendNanoSimToCustomer(Customer customer) {
        System.out.println("Nano SIM verschickt an: " + customer.getAddress());
    }
    
    private void sendMicroSimToCustomer(Customer customer) {
        System.out.println("Micro SIM verschickt an: " + customer.getAddress());
    }
    
    private void sendStandardSimToCustomer(Customer customer) {
        System.out.println("Standard SIM verschickt an: " + customer.getAddress());
    }
    
    private boolean hasExistingLine(String address) {
        // Simplified check
        return address.contains("Straße") || address.contains("Platz");
    }
    
    private String handleBillingInquiry(String customerId, String month) {
        // Billing Logic hardcoded
        return "Rechnung für " + month + " beträgt 45.99 EUR";
    }
    
    private String handleTechnicalSupport(String customerId, String issue) {
        // Support Logic hardcoded
        Ticket ticket = new Ticket();
        ticket.setCustomerId(customerId);
        ticket.setIssue(issue);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus("OPEN");
        tickets.add(ticket);
        return "Support Ticket #" + ticket.getId() + " erstellt";
    }
    
    private String handleContractChange(String customerId, String changeType, Map<String, Object> params) {
        // Contract Change Logic hardcoded
        return "Vertragsänderung " + changeType + " bearbeitet";
    }
    
    private String generateContractId() {
        return "CTR-" + System.currentTimeMillis();
    }
    
    // CRUD Operations - sollten separiert sein
    public void addCustomer(String customerId, String name, String address, String email) {
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName(name);
        customer.setAddress(address);
        customer.setEmail(email);
        customers.put(customerId, customer);
    }
    
    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }
    
    public List<Contract> getCustomerContracts(String customerId) {
        List<Contract> customerContracts = new ArrayList<>();
        for (Contract contract : contracts.values()) {
            if (contract.getCustomerId().equals(customerId)) {
                customerContracts.add(contract);
            }
        }
        return customerContracts;
    }
    
    public List<Ticket> getCustomerTickets(String customerId) {
        List<Ticket> customerTickets = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getCustomerId().equals(customerId)) {
                customerTickets.add(ticket);
            }
        }
        return customerTickets;
    }
}

// Data Classes - gehören in separate Files
class Customer {
    private String id;
    private String name;
    private String address;
    private String email;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

class Contract {
    private String id;
    private String customerId;
    private String type;
    private String speed;
    private String dataVolume;
    private String features;
    private double monthlyFee;
    private LocalDateTime activationDate;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSpeed() { return speed; }
    public void setSpeed(String speed) { this.speed = speed; }
    public String getDataVolume() { return dataVolume; }
    public void setDataVolume(String dataVolume) { this.dataVolume = dataVolume; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    public double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(double monthlyFee) { this.monthlyFee = monthlyFee; }
    public LocalDateTime getActivationDate() { return activationDate; }
    public void setActivationDate(LocalDateTime activationDate) { this.activationDate = activationDate; }
}

class Ticket {
    private String id;
    private String customerId;
    private String issue;
    private LocalDateTime createdAt;
    private String status;
    
    public Ticket() {
        this.id = "TIC-" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

class Invoice {
    private String id;
    private String customerId;
    private double amount;
    private LocalDateTime dueDate;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}