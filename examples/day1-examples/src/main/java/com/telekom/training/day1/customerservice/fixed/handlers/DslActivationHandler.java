package com.telekom.training.day1.customerservice.fixed.handlers;

import com.telekom.training.day1.customerservice.fixed.model.Customer;
import com.telekom.training.day1.customerservice.fixed.model.Contract;
import java.util.Map;

/**
 * Concrete Handler für DSL Aktivierung
 * Implementiert die Factory Method für DSL-spezifische Behandlung
 */
public class DslActivationHandler extends RequestHandler {
    
    @Override
    protected String createResponse(Customer customer, Map<String, Object> params) {
        // DSL-spezifische Validierung
        if (!isDslAvailable(customer.getAddress())) {
            return "ERROR: DSL nicht verfügbar in Ihrer Region";
        }
        
        String speed = (String) params.get("speed");
        double fee = calculateDslFee(speed);
        
        // Contract erstellen
        Contract contract = new Contract(generateContractId(), customer.getId(), "DSL", fee);
        
        // DSL-spezifische Aktionen
        handleEquipmentDelivery(customer, speed);
        scheduleInstallation(customer, speed);
        
        return String.format("DSL Vertrag erstellt (Speed: %s, Gebühr: %.2f EUR). Techniker wird beauftragt.", speed, fee);
    }
    
    @Override
    protected boolean isValidRequest(Customer customer, Map<String, Object> params) {
        return super.isValidRequest(customer, params) && 
               params.containsKey("speed") && 
               params.get("speed") instanceof String;
    }
    
    private boolean isDslAvailable(String address) {
        // Simplified availability check
        return address.contains("Berlin") || address.contains("Hamburg") || address.contains("München");
    }
    
    private double calculateDslFee(String speed) {
        switch (speed) {
            case "50": return 29.95;
            case "100": return 39.95;
            case "250": return 49.95;
            default: return 19.95;
        }
    }
    
    private void handleEquipmentDelivery(Customer customer, String speed) {
        if ("250".equals(speed)) {
            System.out.println("Premium Router wird verschickt an: " + customer.getAddress());
        } else {
            System.out.println("Standard Router wird verschickt an: " + customer.getAddress());
        }
    }
    
    private void scheduleInstallation(Customer customer, String speed) {
        String technicianType = "250".equals(speed) ? "DSL_PREMIUM_TECHNIKER" : "DSL_TECHNIKER";
        System.out.println("Installation geplant für: " + customer.getName() + " mit " + technicianType);
    }
}