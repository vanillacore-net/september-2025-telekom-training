package com.telekom.training.day2.tarifoptions.fixed;

import java.math.BigDecimal;

/**
 * Decorator Pattern: Base Decorator
 * Maintains a reference to a Component object and defines an interface 
 * that conforms to Component's interface
 */
public abstract class TariffDecorator implements Tariff {
    protected final Tariff tariff;

    public TariffDecorator(Tariff tariff) {
        this.tariff = tariff;
    }

    @Override
    public String getName() {
        return tariff.getName();
    }

    @Override
    public BigDecimal getPrice() {
        return tariff.getPrice();
    }

    @Override
    public String getDescription() {
        return tariff.getDescription();
    }

    @Override
    public int getIncludedMinutes() {
        return tariff.getIncludedMinutes();
    }

    @Override
    public int getIncludedSms() {
        return tariff.getIncludedSms();
    }

    @Override
    public int getIncludedDataMB() {
        return tariff.getIncludedDataMB();
    }
}