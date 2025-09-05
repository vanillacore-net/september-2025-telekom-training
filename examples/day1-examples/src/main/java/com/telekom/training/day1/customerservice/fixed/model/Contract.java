package com.telekom.training.day1.customerservice.fixed.model;

import java.time.LocalDateTime;

/**
 * Contract Domain Model
 */
public class Contract {
    private String id;
    private String customerId;
    private String type;
    private double monthlyFee;
    private LocalDateTime activationDate;
    
    public Contract(String id, String customerId, String type, double monthlyFee) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.monthlyFee = monthlyFee;
        this.activationDate = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getType() { return type; }
    public double getMonthlyFee() { return monthlyFee; }
    public LocalDateTime getActivationDate() { return activationDate; }
    
    @Override
    public String toString() {
        return "Contract{id='" + id + "', customerId='" + customerId + "', type='" + type + "', monthlyFee=" + monthlyFee + "}";
    }
}