package com.telekom.training.day1.customerservice.fixed;

import com.telekom.training.day1.customerservice.fixed.handlers.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory für Request Handler
 * 
 * Zentrale Fabrik die bestimmt, welcher Handler für welchen Request Type
 * verwendet wird. Ermöglicht einfache Erweiterung ohne Änderung bestehender Klassen.
 */
public class RequestHandlerFactory {
    
    private static final Map<String, RequestHandler> handlers = new HashMap<>();
    
    static {
        // Handler registrieren
        handlers.put("DSL_ACTIVATION", new DslActivationHandler());
        handlers.put("MOBILE_ACTIVATION", new MobileActivationHandler());
        handlers.put("FESTNETZ_ACTIVATION", new FestnetzActivationHandler());
        // Weitere Handler können einfach hinzugefügt werden:
        // handlers.put("BILLING_INQUIRY", new BillingInquiryHandler());
        // handlers.put("TECHNICAL_SUPPORT", new TechnicalSupportHandler());
    }
    
    /**
     * Factory Method - erstellt den passenden Handler
     */
    public static RequestHandler createHandler(String requestType) {
        RequestHandler handler = handlers.get(requestType.toUpperCase());
        if (handler == null) {
            throw new IllegalArgumentException("Unknown request type: " + requestType);
        }
        return handler;
    }
    
    /**
     * Prüft ob ein Request Type unterstützt wird
     */
    public static boolean isSupported(String requestType) {
        return handlers.containsKey(requestType.toUpperCase());
    }
    
    /**
     * Registriert einen neuen Handler - für Erweiterungen zur Laufzeit
     */
    public static void registerHandler(String requestType, RequestHandler handler) {
        handlers.put(requestType.toUpperCase(), handler);
    }
}