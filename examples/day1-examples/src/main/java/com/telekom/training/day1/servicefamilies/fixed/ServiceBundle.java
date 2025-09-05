package com.telekom.training.day1.servicefamilies.fixed;

import com.telekom.training.day1.servicefamilies.fixed.services.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Bundle - Container für Service-Pakete
 */
public class ServiceBundle {
    private String bundleType;
    private List<Object> services = new ArrayList<>();
    private double discountPercentage;
    
    public ServiceBundle(String bundleType) {
        this.bundleType = bundleType;
    }
    
    public void addService(Object service) {
        services.add(service);
    }
    
    public void setDiscountPercentage(double percentage) {
        this.discountPercentage = percentage;
    }
    
    public String getBundleType() {
        return bundleType;
    }
    
    public List<Object> getServices() {
        return new ArrayList<>(services); // Defensive copy
    }
    
    public double getDiscountPercentage() {
        return discountPercentage;
    }
    
    /**
     * Berechnet den Gesamtpreis des Bundles mit Discount
     */
    public double calculateTotalPrice() {
        double totalPrice = 0.0;
        
        for (Object service : services) {
            if (service instanceof InternetService) {
                totalPrice += ((InternetService) service).getBasicPrice();
            } else if (service instanceof PhoneService) {
                totalPrice += ((PhoneService) service).getBasicPrice();
            } else if (service instanceof TvService) {
                totalPrice += ((TvService) service).getBasicPrice();
            }
        }
        
        // Discount anwenden
        return totalPrice * (1.0 - discountPercentage / 100.0);
    }
    
    public String getBundleInfo() {
        return String.format("Bundle: %s, Services: %d, Discount: %.1f%%, Preis: %.2f EUR/Monat", 
                           bundleType, services.size(), discountPercentage, calculateTotalPrice());
    }
    
    /**
     * Detaillierte Service-Informationen
     */
    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== ").append(bundleType).append(" ===\n");
        
        for (Object service : services) {
            if (service instanceof InternetService) {
                info.append("Internet: ").append(((InternetService) service).getServiceInfo()).append("\n");
            } else if (service instanceof PhoneService) {
                info.append("Phone: ").append(((PhoneService) service).getServiceInfo()).append("\n");
            } else if (service instanceof TvService) {
                info.append("TV: ").append(((TvService) service).getServiceInfo()).append("\n");
            }
        }
        
        info.append(String.format("Bundle Discount: %.1f%%\n", discountPercentage));
        info.append(String.format("Total: %.2f EUR/Monat", calculateTotalPrice()));
        
        return info.toString();
    }
}