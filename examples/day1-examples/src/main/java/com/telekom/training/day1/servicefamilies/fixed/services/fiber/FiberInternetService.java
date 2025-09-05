package com.telekom.training.day1.servicefamilies.fixed.services.fiber;

import com.telekom.training.day1.servicefamilies.fixed.services.InternetService;

/**
 * Concrete Product für Fiber Internet Service
 */
public class FiberInternetService implements InternetService {
    
    private final String technology = "FTTH (Fiber to the Home)";
    private final int maxSpeed = 1000;
    private final String installationType = "FIBER_INSTALLATION";
    private final String equipment = "Glasfaser Modem ONT";
    private final String availabilityCheck = "FIBER_INFRASTRUCTURE_REQUIRED";
    private final double basicPrice = 49.95;
    
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
        return String.format("Fiber Internet: %s, bis zu %d Mbps, Equipment: %s (%.2f EUR/Monat)", 
                           technology, maxSpeed, equipment, basicPrice);
    }
}