package com.telekom.training.day1.servicefamilies.fixed.services.fiber;

import com.telekom.training.day1.servicefamilies.fixed.services.PhoneService;

/**
 * Concrete Product für Fiber Phone Service
 */
public class FiberPhoneService implements PhoneService {
    
    private final String technology = "VoIP über Glasfaser";
    private final String features = "HD_VOICE,VIDEO_CALLS,CONFERENCE,CALLER_ID,VOICEMAIL";
    private final String installationType = "WITH_FIBER_INSTALLATION";
    private final String equipment = "VoIP Adapter + HD-Telefon";
    private final String numberType = "VIRTUAL_NUMBER";
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
        return String.format("Fiber Phone: %s, Features: %s (%.2f EUR/Monat)", 
                           technology, features, basicPrice);
    }
}