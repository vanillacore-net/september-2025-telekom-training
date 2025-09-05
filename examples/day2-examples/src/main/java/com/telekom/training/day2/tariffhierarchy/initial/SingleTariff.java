package com.telekom.training.day2.tariffhierarchy.initial;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * PROBLEM: Inconsistent handling of single tariffs vs bundles
 * Client code needs to check type and handle differently
 */
public class SingleTariff {
    private final String name;
    private final BigDecimal price;
    private final String category;

    public SingleTariff(String name, BigDecimal price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    // Problem: Single tariffs don't have children, but bundle does
    public List<SingleTariff> getComponents() {
        return Collections.emptyList(); // Awkward - single has no components
    }

    public void printStructure(String prefix) {
        System.out.println(prefix + "- " + name + " (" + category + "): " + price + "€");
    }
}