package com.telekom.training.day2.tarifoptions.fixed;

import java.math.BigDecimal;

/**
 * Decorator Pattern: Concrete Decorator
 * Adds insurance option to any tariff - demonstrating easy extensibility
 */
public class InsuranceDecorator extends TariffDecorator {
    private static final BigDecimal INSURANCE_COST = new BigDecimal("4.99");

    public InsuranceDecorator(Tariff tariff) {
        super(tariff);
    }

    @Override
    public BigDecimal getPrice() {
        return tariff.getPrice().add(INSURANCE_COST);
    }

    @Override
    public String getDescription() {
        return tariff.getDescription() + " + Geräte-Versicherung (+4.99€)";
    }
}