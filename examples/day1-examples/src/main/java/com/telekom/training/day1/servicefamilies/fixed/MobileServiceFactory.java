package com.telekom.training.day1.servicefamilies.fixed;

import com.telekom.training.day1.servicefamilies.fixed.services.*;
import com.telekom.training.day1.servicefamilies.fixed.services.mobile.*;

/**
 * Concrete Factory für Mobile Service Familie
 */
public class MobileServiceFactory extends TelekomServiceFactory {
    
    @Override
    public InternetService createInternetService() {
        return new MobileInternetService();
    }
    
    @Override
    public PhoneService createPhoneService() {
        return new MobilePhoneService();
    }
    
    @Override
    public TvService createTvService() {
        return new MobileTvService();
    }
    
    @Override
    protected String getServiceFamilyName() {
        return "MOBILE_FAMILY";
    }
    
    @Override
    protected double getBundleDiscount() {
        return 10.0; // 10% Discount für Mobile Bundle
    }
}