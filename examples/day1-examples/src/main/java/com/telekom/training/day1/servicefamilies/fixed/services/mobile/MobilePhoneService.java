package com.telekom.training.day1.servicefamilies.fixed.services.mobile;

import com.telekom.training.day1.servicefamilies.fixed.services.PhoneService;

/**
 * Concrete Product für Mobile Phone Service
 */
public class MobilePhoneService implements PhoneService {
    
    private final String technology = "GSM/LTE/5G Voice";
    private final String features = "SMS,MMS,DATA,ROAMING,WIFI_CALLING";
    private final String installationType = "SIM_ACTIVATION";
    private final String equipment = "Nano/Micro/Standard SIM";
    private final String numberType = "MOBILE";
    private final double basicPrice = 24.95;
    
    @Override
    public String getTechnology() { return technology; }
    
    @Override
    public String getFeatures() { return features; }
    
    @Override
    public String getInstallationType() { return installationType; }
    
    @Override
    public String getEquipment() { return equipment; }
    
    @Override
    public String getNumberType() { return numberType; }
    
    @Override
    public double getBasicPrice() { return basicPrice; }
    
    @Override
    public String getServiceInfo() {
        return String.format("Mobile Phone: %s, Features: %s (%.2f EUR/Monat)", 
                           technology, features, basicPrice);
    }
}