package com.telekom.training.day1.servicefamilies.fixed.services.fiber;

import com.telekom.training.day1.servicefamilies.fixed.services.TvService;

/**
 * Concrete Product für Fiber TV Service
 */
public class FiberTvService implements TvService {
    
    private final String technology = "IPTV über Glasfaser";
    private final String channelPackage = "MagentaTV Premium 4K (200+ Kanäle)";
    private final String installationType = "ADVANCED_4K_SET_TOP_BOX";
    private final String equipment = "MagentaTV 4K Box + Media Receiver";
    private final String streamingCapability = "UNLIMITED_4K_STREAMING";
    private final double basicPrice = 34.95;
    
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
        return String.format("Fiber TV: %s, Package: %s, Equipment: %s (%.2f EUR/Monat)", 
                           technology, channelPackage, equipment, basicPrice);
    }
}