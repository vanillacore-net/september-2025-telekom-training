package com.telekom.training.day1.servicefamilies.fixed.services;

/**
 * Abstract Product für Internet Services
 */
public interface InternetService {
    String getTechnology();
    int getMaxSpeed();
    String getInstallationType();
    String getEquipment();
    String getAvailabilityCheck();
    String getServiceInfo();
    double getBasicPrice();
}