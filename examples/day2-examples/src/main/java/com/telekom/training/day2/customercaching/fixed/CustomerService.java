package com.telekom.training.day2.customercaching.fixed;

import com.telekom.training.day2.customercaching.initial.Customer;

/**
 * Proxy Pattern: Subject
 * Common interface for RealSubject and Proxy
 */
public interface CustomerService {
    Customer getCustomer(String customerId);
    Customer getCustomerWithTariffHistory(String customerId);
    boolean updateCustomer(Customer customer);
}