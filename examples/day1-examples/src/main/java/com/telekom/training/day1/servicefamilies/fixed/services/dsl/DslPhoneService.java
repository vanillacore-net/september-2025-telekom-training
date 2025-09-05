package com.telekom.training.day1.servicefamilies.fixed.services.dsl;

import com.telekom.training.day1.servicefamilies.fixed.services.PhoneService;

/**
 * Concrete Product für DSL Phone Service
 */
public class DslPhoneService implements PhoneService {
    
    private final String technology = "ISDN/VoIP über DSL";
    private final String features = "CALLER_ID,VOICEMAIL,CALL_WAITING,3WAY_CONFERENCE";
    private final String installationType = "WITH_DSL_INSTALLATION";
    private final String equipment = "Included in Fritz!Box";
    private final String numberType = "LANDLINE";
    private final double basicPrice = 19.95;
    
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
        return String.format("DSL Phone: %s, Features: %s (%.2f EUR/Monat)", 
                           technology, features, basicPrice);
    }
}