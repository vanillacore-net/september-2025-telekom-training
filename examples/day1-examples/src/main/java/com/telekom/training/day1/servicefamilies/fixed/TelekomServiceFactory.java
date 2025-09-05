package com.telekom.training.day1.servicefamilies.fixed;

import com.telekom.training.day1.servicefamilies.fixed.services.*;

/**
 * Abstract Factory für Telekom Service Familien
 * 
 * Definiert das Interface für die Erstellung von Service-Familien.
 * Jede konkrete Factory erstellt eine kohärente Familie von Services
 * (DSL, Mobile, Fiber) die zusammen funktionieren.
 */
public abstract class TelekomServiceFactory {
    
    /**
     * Factory Methods für Service-Erstellung
     * Diese werden von konkreten Factories implementiert
     */
    public abstract InternetService createInternetService();
    public abstract PhoneService createPhoneService();
    public abstract TvService createTvService();
    
    /**
     * Template Method für Bundle Creation
     */
    public ServiceBundle createCompleteBundle() {
        ServiceBundle bundle = new ServiceBundle(getServiceFamilyName());
        
        bundle.addService(createInternetService());
        bundle.addService(createPhoneService());
        bundle.addService(createTvService());
        
        bundle.setDiscountPercentage(getBundleDiscount());
        
        return bundle;
    }
    
    /**
     * Template Method für Basic Bundle (Internet + Phone)
     */
    public ServiceBundle createBasicBundle() {
        ServiceBundle bundle = new ServiceBundle(getServiceFamilyName() + "_BASIC");
        
        bundle.addService(createInternetService());
        bundle.addService(createPhoneService());
        
        bundle.setDiscountPercentage(getBundleDiscount() * 0.6); // Weniger Discount für weniger Services
        
        return bundle;
    }
    
    /**
     * Validation für Service-Kompatibilität
     */
    public boolean areServicesCompatible(InternetService internet, PhoneService phone, TvService tv) {
        // Alle Services aus derselben Factory sind automatisch kompatibel
        return internet != null && phone != null && tv != null;
    }
    
    /**
     * Abstract Methods für Factory-spezifische Eigenschaften
     */
    protected abstract String getServiceFamilyName();
    protected abstract double getBundleDiscount();
    
    /**
     * Static Factory Method für Factory Selection
     */
    public static TelekomServiceFactory getFactory(String serviceType) {
        switch (serviceType.toUpperCase()) {
            case "DSL":
                return new DslServiceFactory();
            case "MOBILE":
                return new MobileServiceFactory();
            case "FIBER":
                return new FiberServiceFactory();
            default:
                throw new IllegalArgumentException("Unknown service type: " + serviceType);
        }
    }
}