package com.telekom.training.day2.tariffhierarchy.fixed;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite Pattern: Composite
 * Defines behavior for components having children and stores child components
 */
public class TariffBundle extends TariffComponent {
    private final List<TariffComponent> components;
    private final BigDecimal discount;

    public TariffBundle(String name, BigDecimal discount) {
        super(name);
        this.components = new ArrayList<>();
        this.discount = discount;
    }

    @Override
    public void add(TariffComponent component) {
        components.add(component);
    }

    @Override
    public void remove(TariffComponent component) {
        components.remove(component);
    }

    @Override
    public List<TariffComponent> getComponents() {
        return new ArrayList<>(components);
    }

    @Override
    public BigDecimal getPrice() {
        BigDecimal total = BigDecimal.ZERO;
        
        // Uniform treatment: all components have same interface
        for (TariffComponent component : components) {
            total = total.add(component.getPrice());
        }
        
        return total.subtract(discount);
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    @Override
    public void printStructure(String prefix) {
        System.out.println(prefix + "+ Bundle: " + name + " (Rabatt: " + discount + "€, Gesamt: " + getPrice() + "€)");
        
        // Uniform handling of all components
        for (TariffComponent component : components) {
            component.printStructure(prefix + "  ");
        }
    }
}