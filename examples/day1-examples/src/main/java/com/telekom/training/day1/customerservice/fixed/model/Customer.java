package com.telekom.training.day1.customerservice.fixed.model;

/**
 * Customer Domain Model - Clean separation
 */
public class Customer {
    private String id;
    private String name;
    private String address;
    private String email;
    
    public Customer(String id, String name, String address, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    
    @Override
    public String toString() {
        return "Customer{id='" + id + "', name='" + name + "', address='" + address + "'}";
    }
}