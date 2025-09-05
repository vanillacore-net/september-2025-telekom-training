package com.telekom.training.day1.servicefamilies.fixed.services.dsl;

import com.telekom.training.day1.servicefamilies.fixed.services.InternetService;

/**
 * Concrete Product für DSL Internet Service
 */
public class DslInternetService implements InternetService {
    
    private final String technology = "ADSL2+/VDSL2";
    private final int maxSpeed = 250;
    private final String installationType = "TECHNICIAN_REQUIRED";
    private final String equipment = "Fritz!Box 7590";
    private final String availabilityCheck = "LINE_BASED";
    private final double basicPrice = 39.95;
    
    @Override
    public String getTechnology() { return technology; }
    
    @Override
    public int getMaxSpeed() { return maxSpeed; }
    
    @Override
    public String getInstallationType() { return installationType; }
    
    @Override
    public String getEquipment() { return equipment; }
    
    @Override
    public String getAvailabilityCheck() { return availabilityCheck; }
    
    @Override
    public double getBasicPrice() { return basicPrice; }
    
    @Override
    public String getServiceInfo() {
        return String.format("DSL Internet: %s, bis zu %d Mbps, Equipment: %s (%.2f EUR/Monat)", 
                           technology, maxSpeed, equipment, basicPrice);
    }
}