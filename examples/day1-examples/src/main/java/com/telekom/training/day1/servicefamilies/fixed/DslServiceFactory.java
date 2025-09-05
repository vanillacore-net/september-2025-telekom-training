package com.telekom.training.day1.servicefamilies.fixed;

import com.telekom.training.day1.servicefamilies.fixed.services.*;
import com.telekom.training.day1.servicefamilies.fixed.services.dsl.*;

/**
 * Concrete Factory für DSL Service Familie
 * 
 * Erstellt kohärente DSL-Services die alle zusammen funktionieren
 * und die technischen Eigenschaften der DSL-Infrastruktur teilen.
 */
public class DslServiceFactory extends TelekomServiceFactory {
    
    @Override
    public InternetService createInternetService() {
        return new DslInternetService();
    }
    
    @Override
    public PhoneService createPhoneService() {
        return new DslPhoneService();
    }
    
    @Override
    public TvService createTvService() {
        return new DslTvService();
    }
    
    @Override
    protected String getServiceFamilyName() {
        return "DSL_FAMILY";
    }
    
    @Override
    protected double getBundleDiscount() {
        return 15.0; // 15% Discount für DSL Bundle
    }
}