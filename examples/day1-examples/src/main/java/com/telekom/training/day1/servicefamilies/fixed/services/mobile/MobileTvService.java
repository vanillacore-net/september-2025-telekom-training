package com.telekom.training.day1.servicefamilies.fixed.services.mobile;

import com.telekom.training.day1.servicefamilies.fixed.services.TvService;

/**
 * Concrete Product für Mobile TV Service
 */
public class MobileTvService implements TvService {
    
    private final String technology = "Streaming über Mobile Data";
    private final String channelPackage = "MagentaTV Mobile (50+ Kanäle optimiert)";
    private final String installationType = "APP_INSTALLATION";
    private final String equipment = "Smartphone/Tablet App";
    private final String streamingCapability = "MOBILE_DATA_DEPENDENT";
    private final double basicPrice = 9.95;
    
    @Override
    public String getTechnology() { return technology; }
    
    @Override
    public String getChannelPackage() { return channelPackage; }
    
    @Override
    public String getInstallationType() { return installationType; }
    
    @Override
    public String getEquipment() { return equipment; }
    
    @Override
    public String getStreamingCapability() { return streamingCapability; }
    
    @Override
    public double getBasicPrice() { return basicPrice; }
    
    @Override
    public String getServiceInfo() {
        return String.format("Mobile TV: %s, Package: %s (%.2f EUR/Monat)", 
                           technology, channelPackage, basicPrice);
    }
}