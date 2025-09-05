package com.telekom.training.day1.servicefamilies.fixed.services;

/**
 * Abstract Product für TV Services
 */
public interface TvService {
    String getTechnology();
    String getChannelPackage();
    String getInstallationType();
    String getEquipment();
    String getStreamingCapability();
    String getServiceInfo();
    double getBasicPrice();
}