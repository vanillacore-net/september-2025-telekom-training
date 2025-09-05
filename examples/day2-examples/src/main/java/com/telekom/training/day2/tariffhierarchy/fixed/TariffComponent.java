package com.telekom.training.day2.tariffhierarchy.fixed;

import java.math.BigDecimal;
import java.util.List;

/**
 * Composite Pattern: Component
 * Declares interface for objects in composition and implements default behavior
 * common to all classes as appropriate
 */
public abstract class TariffComponent {
    protected String name;

    public TariffComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Common interface for all components
    public abstract BigDecimal getPrice();
    public abstract void printStructure(String prefix);
    
    // Default implementation for composite operations
    public void add(TariffComponent component) {
        throw new UnsupportedOperationException("Cannot add to " + getClass().getSimpleName());
    }
    
    public void remove(TariffComponent component) {
        throw new UnsupportedOperationException("Cannot remove from " + getClass().getSimpleName());
    }
    
    public List<TariffComponent> getComponents() {
        throw new UnsupportedOperationException("Cannot get components from " + getClass().getSimpleName());
    }
}