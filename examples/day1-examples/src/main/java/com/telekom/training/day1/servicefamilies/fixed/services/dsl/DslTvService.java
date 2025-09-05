package com.telekom.training.day1.servicefamilies.fixed.services.dsl;

import com.telekom.training.day1.servicefamilies.fixed.services.TvService;

/**
 * Concrete Product für DSL TV Service
 */
public class DslTvService implements TvService {
    
    private final String technology = "IPTV über DSL";
    private final String channelPackage = "MagentaTV Basic HD (100+ Kanäle)";
    private final String installationType = "MAGENTA_TV_BOX";
    private final String equipment = "MagentaTV Box (HD-Receiver)";
    private final String streamingCapability = "LIMITED_BY_DSL_BANDWIDTH";
    private final double basicPrice = 24.95;
    
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
        return String.format("DSL TV: %s, Package: %s, Equipment: %s (%.2f EUR/Monat)", 
                           technology, channelPackage, equipment, basicPrice);
    }
}