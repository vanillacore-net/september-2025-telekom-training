package com.telekom.training.day2.tarifoptions.fixed;

import java.math.BigDecimal;

/**
 * Decorator Pattern: Concrete Decorator
 * Adds flatrate option to any tariff without changing the original tariff
 */
public class FlatrateDecorator extends TariffDecorator {
    private static final BigDecimal FLATRATE_COST = new BigDecimal("15.00");

    public FlatrateDecorator(Tariff tariff) {
        super(tariff);
    }

    @Override
    public BigDecimal getPrice() {
        return tariff.getPrice().add(FLATRATE_COST);
    }

    @Override
    public String getDescription() {
        return tariff.getDescription() + " + Flatrate (+15.00€)";
    }

    @Override
    public int getIncludedMinutes() {
        return Integer.MAX_VALUE; // Unlimited with flatrate
    }
}