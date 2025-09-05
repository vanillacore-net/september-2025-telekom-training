package com.telekom.training.day1.servicefamilies.fixed.services.mobile;

import com.telekom.training.day1.servicefamilies.fixed.services.InternetService;

/**
 * Concrete Product für Mobile Internet Service
 */
public class MobileInternetService implements InternetService {
    
    private final String technology = "LTE/5G";
    private final int maxSpeed = 1000;
    private final String installationType = "SIM_ACTIVATION";
    private final String equipment = "SIM Card + Mobile Device";
    private final String availabilityCheck = "COVERAGE_BASED";
    private final double basicPrice = 29.95;
    
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
        return String.format("Mobile Internet: %s, bis zu %d Mbps, Equipment: %s (%.2f EUR/Monat)", 
                           technology, maxSpeed, equipment, basicPrice);
    }
}