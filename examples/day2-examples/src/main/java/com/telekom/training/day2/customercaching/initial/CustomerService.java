package com.telekom.training.day2.customercaching.initial;

/**
 * Interface for customer service
 */
public interface CustomerService {
    Customer getCustomer(String customerId);
    Customer getCustomerWithTariffHistory(String customerId);
    boolean updateCustomer(Customer customer);
}