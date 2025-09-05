package com.telekom.training.day1.prototype.initial;

/**
 * ANTI-PATTERN: Expensive Object Creation für ähnliche Konfigurationen
 * 
 * Problem: Jede createXXXConfiguration() Methode wiederholt die gleichen
 * teuren Operations:
 * - Database Discovery (200ms)
 * - Security Loading (200ms) 
 * - Service Discovery (300ms)
 * - SSL Validation (500ms)
 * 
 * Total: 1.2+ Sekunden für jede Konfiguration!
 * 
 * Bei 100 ähnlichen Konfigurationen = 120 Sekunden verschwendete Zeit!
 */
public class ServiceConfigurationManager {
    
    public ServiceConfiguration createDevConfiguration() {
        System.out.println("Creating DEV configuration... (this takes 1.2+ seconds)");
        long start = System.currentTimeMillis();
        
        // EXPENSIVE: Alle Operations werden wiederholt!
        ServiceConfiguration config = new ServiceConfiguration("DEV", "DEBUG", "dev-db-url");
        
        long duration = System.currentTimeMillis() - start;
        System.out.printf("DEV config created in %d ms%n", duration);
        return config;
    }
    
    public ServiceConfiguration createTestConfiguration() {
        System.out.println("Creating TEST configuration... (this takes 1.2+ seconds)");
        long start = System.currentTimeMillis();
        
        // EXPENSIVE: Gleiche Operations wiederholt!
        ServiceConfiguration config = new ServiceConfiguration("TEST", "INFO", "test-db-url");
        
        long duration = System.currentTimeMillis() - start;
        System.out.printf("TEST config created in %d ms%n", duration);
        return config;
    }
    
    public ServiceConfiguration createProdConfiguration() {
        System.out.println("Creating PROD configuration... (this takes 1.2+ seconds)");
        long start = System.currentTimeMillis();
        
        // EXPENSIVE: Noch mehr Wiederholung!
        ServiceConfiguration config = new ServiceConfiguration("PROD", "WARN", "prod-db-url");
        
        long duration = System.currentTimeMillis() - start;
        System.out.printf("PROD config created in %d ms%n", duration);
        return config;
    }
    
    // Demonstration der Performance-Probleme
    public static void main(String[] args) {
        ServiceConfigurationManager manager = new ServiceConfigurationManager();
        
        System.out.println("=== WITHOUT PROTOTYPE PATTERN ===");
        System.out.println("Creating multiple similar configurations...\n");
        
        long totalStart = System.currentTimeMillis();
        
        // Erstelle 5 ähnliche Konfigurationen
        ServiceConfiguration dev1 = manager.createDevConfiguration();
        ServiceConfiguration dev2 = manager.createDevConfiguration(); // DUPLICATE WORK!
        ServiceConfiguration test1 = manager.createTestConfiguration();
        ServiceConfiguration test2 = manager.createTestConfiguration(); // DUPLICATE WORK!
        ServiceConfiguration prod1 = manager.createProdConfiguration();
        
        long totalDuration = System.currentTimeMillis() - totalStart;
        
        System.out.printf("%nTotal time for 5 configurations: %d ms%n", totalDuration);
        System.out.printf("Average per configuration: %d ms%n", totalDuration / 5);
        System.out.println("\n💸 WASTE: ~4.8 seconds of identical expensive operations repeated!");
        System.out.println("💡 SOLUTION: Use Prototype Pattern to clone pre-initialized objects!");
    }
}