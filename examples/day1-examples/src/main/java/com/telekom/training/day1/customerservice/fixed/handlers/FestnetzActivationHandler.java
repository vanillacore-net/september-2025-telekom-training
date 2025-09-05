package com.telekom.training.day1.customerservice.fixed.handlers;

import com.telekom.training.day1.customerservice.fixed.model.Customer;
import com.telekom.training.day1.customerservice.fixed.model.Contract;
import java.util.Map;

/**
 * Concrete Handler für Festnetz Aktivierung
 */
public class FestnetzActivationHandler extends RequestHandler {
    
    @Override
    protected String createResponse(Customer customer, Map<String, Object> params) {
        if (!hasExistingLine(customer.getAddress())) {
            return "ERROR: Keine Telefonleitung verfügbar";
        }
        
        String features = (String) params.get("features");
        double fee = calculateFestnetzFee(features);
        
        // Contract erstellen
        Contract contract = new Contract(generateContractId(), customer.getId(), "FESTNETZ", fee);
        
        return String.format("Festnetz Vertrag erstellt (Features: %s, Gebühr: %.2f EUR)", features, fee);
    }
    
    @Override
    protected boolean isValidRequest(Customer customer, Map<String, Object> params) {
        return super.isValidRequest(customer, params) && 
               params.containsKey("features");
    }
    
    private boolean hasExistingLine(String address) {
        // Simplified check
        return address.contains("Straße") || address.contains("Platz");
    }
    
    private double calculateFestnetzFee(String features) {
        double baseFee = 19.95;
        if (features.contains("INTERNATIONAL")) baseFee += 10.0;
        if (features.contains("VOICEMAIL")) baseFee += 5.0;
        return baseFee;
    }
}