package com.telekom.training.day2.tarifoptions.fixed;

import java.math.BigDecimal;

/**
 * Decorator Pattern: Concrete Component
 * The basic object that can be decorated with additional options
 */
public class BasicTariff implements Tariff {
    private final String name;
    private final BigDecimal basePrice;
    private final int includedMinutes;
    private final int includedSms;
    private final int includedDataMB;

    public BasicTariff(String name, BigDecimal basePrice, int includedMinutes, int includedSms, int includedDataMB) {
        this.name = name;
        this.basePrice = basePrice;
        this.includedMinutes = includedMinutes;
        this.includedSms = includedSms;
        this.includedDataMB = includedDataMB;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getPrice() {
        return basePrice;
    }

    @Override
    public String getDescription() {
        return String.format("Tarif: %s - %d Minuten, %d SMS, %d MB für %.2f€",
            name, includedMinutes, includedSms, includedDataMB, basePrice);
    }

    @Override
    public int getIncludedMinutes() {
        return includedMinutes;
    }

    @Override
    public int getIncludedSms() {
        return includedSms;
    }

    @Override
    public int getIncludedDataMB() {
        return includedDataMB;
    }
}