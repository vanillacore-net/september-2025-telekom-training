package com.telekom.training.day1.customerservice.fixed.handlers;

import com.telekom.training.day1.customerservice.fixed.model.Customer;
import com.telekom.training.day1.customerservice.fixed.model.Contract;
import java.util.Map;

/**
 * Concrete Handler für Mobile Aktivierung
 */
public class MobileActivationHandler extends RequestHandler {
    
    @Override
    protected String createResponse(Customer customer, Map<String, Object> params) {
        String dataVolume = (String) params.get("dataVolume");
        String simType = (String) params.get("simType");
        double fee = calculateMobileFee(dataVolume);
        
        // Contract erstellen
        Contract contract = new Contract(generateContractId(), customer.getId(), "MOBILE", fee);
        
        // Mobile-spezifische Aktionen
        sendSimCard(customer, simType);
        
        return String.format("Mobile Vertrag erstellt (Datenvolumen: %s, Gebühr: %.2f EUR). SIM-Karte wird verschickt.", 
                           dataVolume, fee);
    }
    
    @Override
    protected boolean isValidRequest(Customer customer, Map<String, Object> params) {
        return super.isValidRequest(customer, params) && 
               params.containsKey("dataVolume") && 
               params.containsKey("simType");
    }
    
    private double calculateMobileFee(String dataVolume) {
        switch (dataVolume) {
            case "5GB": return 19.95;
            case "10GB": return 29.95;
            case "20GB": return 39.95;
            case "UNLIMITED": return 59.95;
            default: return 9.95;
        }
    }
    
    private void sendSimCard(Customer customer, String simType) {
        System.out.println(String.format("%s SIM wird verschickt an: %s", 
                          simType.toLowerCase(), customer.getAddress()));
    }
}