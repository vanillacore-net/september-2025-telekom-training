package com.telekom.training.day2.tariffhierarchy.fixed;

import java.math.BigDecimal;

/**
 * Composite Pattern: Leaf
 * Represents leaf objects in composition. Has no children and defines behavior for primitive objects
 */
public class SingleTariff extends TariffComponent {
    private final BigDecimal price;
    private final String category;

    public SingleTariff(String name, BigDecimal price, String category) {
        super(name);
        this.price = price;
        this.category = category;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public void printStructure(String prefix) {
        System.out.println(prefix + "- " + name + " (" + category + "): " + price + "€");
    }
}