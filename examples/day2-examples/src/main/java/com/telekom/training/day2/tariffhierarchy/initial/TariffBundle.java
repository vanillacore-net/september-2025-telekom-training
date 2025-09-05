package com.telekom.training.day2.tariffhierarchy.initial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * PROBLEM: Different interface than SingleTariff
 * Client must handle SingleTariff and TariffBundle differently
 */
public class TariffBundle {
    private final String name;
    private final List<SingleTariff> tariffs;
    private final List<TariffBundle> subBundles; // Even more complexity!
    private final BigDecimal discount;

    public TariffBundle(String name, BigDecimal discount) {
        this.name = name;
        this.tariffs = new ArrayList<>();
        this.subBundles = new ArrayList<>();
        this.discount = discount;
    }

    public void addTariff(SingleTariff tariff) {
        tariffs.add(tariff);
    }

    public void addBundle(TariffBundle bundle) {
        subBundles.add(bundle);
    }

    public String getName() {
        return name;
    }

    // Different method signature than SingleTariff - inconsistent!
    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        
        for (SingleTariff tariff : tariffs) {
            total = total.add(tariff.getPrice());
        }
        
        for (TariffBundle bundle : subBundles) {
            total = total.add(bundle.getTotalPrice());
        }
        
        return total.subtract(discount);
    }

    public List<SingleTariff> getTariffs() {
        return new ArrayList<>(tariffs);
    }

    public List<TariffBundle> getSubBundles() {
        return new ArrayList<>(subBundles);
    }

    public void printStructure(String prefix) {
        System.out.println(prefix + "+ Bundle: " + name + " (Rabatt: " + discount + "€)");
        
        for (SingleTariff tariff : tariffs) {
            tariff.printStructure(prefix + "  ");
        }
        
        for (TariffBundle bundle : subBundles) {
            bundle.printStructure(prefix + "  ");
        }
    }
}