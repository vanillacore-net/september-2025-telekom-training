package com.telekom.training.day1.servicefamilies.fixed.services;

/**
 * Abstract Product für Phone Services
 */
public interface PhoneService {
    String getTechnology();
    String getFeatures();
    String getInstallationType();
    String getEquipment();
    String getNumberType();
    String getServiceInfo();
    double getBasicPrice();
}