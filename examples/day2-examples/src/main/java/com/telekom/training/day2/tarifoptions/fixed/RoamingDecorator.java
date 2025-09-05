package com.telekom.training.day2.tarifoptions.fixed;

import java.math.BigDecimal;

/**
 * Decorator Pattern: Concrete Decorator
 * Adds roaming option to any tariff
 */
public class RoamingDecorator extends TariffDecorator {
    private static final BigDecimal ROAMING_COST = new BigDecimal("8.50");

    public RoamingDecorator(Tariff tariff) {
        super(tariff);
    }

    @Override
    public BigDecimal getPrice() {
        return tariff.getPrice().add(ROAMING_COST);
    }

    @Override
    public String getDescription() {
        return tariff.getDescription() + " + EU-Roaming (+8.50€)";
    }
}