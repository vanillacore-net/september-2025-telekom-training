package com.telekom.training.day2.tariffhierarchy.initial;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates the problem with inconsistent handling
 */
public class ProblemDemonstrator {
    
    // PROBLEM: Need different methods for different types
    public BigDecimal calculateTotalCost(List<Object> items) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Object item : items) {
            // Ugly type checking and casting!
            if (item instanceof SingleTariff) {
                SingleTariff tariff = (SingleTariff) item;
                total = total.add(tariff.getPrice());
            } else if (item instanceof TariffBundle) {
                TariffBundle bundle = (TariffBundle) item;
                total = total.add(bundle.getTotalPrice()); // Different method name!
            }
            // What if we add more types? More if-else!
        }
        
        return total;
    }
    
    public void printAllItems(List<Object> items) {
        for (Object item : items) {
            // More ugly type checking!
            if (item instanceof SingleTariff) {
                ((SingleTariff) item).printStructure("");
            } else if (item instanceof TariffBundle) {
                ((TariffBundle) item).printStructure("");
            }
        }
    }
    
    public static void main(String[] args) {
        ProblemDemonstrator demo = new ProblemDemonstrator();
        
        SingleTariff mobile = new SingleTariff("Mobile Basic", new BigDecimal("29.99"), "Mobilfunk");
        SingleTariff internet = new SingleTariff("DSL 100", new BigDecimal("39.99"), "Internet");
        
        TariffBundle familyBundle = new TariffBundle("Familie Komplett", new BigDecimal("5.00"));
        familyBundle.addTariff(mobile);
        familyBundle.addTariff(internet);
        
        // Problem: Mixed list requires Object type and ugly casting
        List<Object> items = Arrays.asList(mobile, familyBundle);
        
        System.out.println("Total cost: " + demo.calculateTotalCost(items));
        demo.printAllItems(items);
    }
}