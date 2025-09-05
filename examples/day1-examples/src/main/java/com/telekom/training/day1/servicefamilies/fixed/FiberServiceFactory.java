package com.telekom.training.day1.servicefamilies.fixed;

import com.telekom.training.day1.servicefamilies.fixed.services.*;
import com.telekom.training.day1.servicefamilies.fixed.services.fiber.*;

/**
 * Concrete Factory für Fiber Service Familie
 */
public class FiberServiceFactory extends TelekomServiceFactory {
    
    @Override
    public InternetService createInternetService() {
        return new FiberInternetService();
    }
    
    @Override
    public PhoneService createPhoneService() {
        return new FiberPhoneService();
    }
    
    @Override
    public TvService createTvService() {
        return new FiberTvService();
    }
    
    @Override
    protected String getServiceFamilyName() {
        return "FIBER_FAMILY";
    }
    
    @Override
    protected double getBundleDiscount() {
        return 20.0; // 20% Discount für Premium Fiber Bundle
    }
}