package com.telekom.training.day2.customercaching.initial;

import java.time.LocalDate;
import java.util.List;

/**
 * Customer data model
 */
public class Customer {
    private final String customerId;
    private final String name;
    private final String email;
    private final LocalDate registrationDate;
    private final List<String> tariffHistory;
    private final String creditRating;

    public Customer(String customerId, String name, String email, 
                   LocalDate registrationDate, List<String> tariffHistory, String creditRating) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.registrationDate = registrationDate;
        this.tariffHistory = tariffHistory;
        this.creditRating = creditRating;
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public List<String> getTariffHistory() { return tariffHistory; }
    public String getCreditRating() { return creditRating; }

    @Override
    public String toString() {
        return String.format("Customer{id='%s', name='%s', email='%s', creditRating='%s'}", 
            customerId, name, email, creditRating);
    }
}