package com.telekom.training.day2.tarifoptions.initial;

import java.math.BigDecimal;

/**
 * Basic tariff - before Decorator Pattern
 * Problem: Need to create subclasses for every combination of options
 */
public class BasicTariff {
    protected String name;
    protected BigDecimal basePrice;
    protected int includedMinutes;
    protected int includedSms;
    protected int includedDataMB;

    public BasicTariff(String name, BigDecimal basePrice, int includedMinutes, int includedSms, int includedDataMB) {
        this.name = name;
        this.basePrice = basePrice;
        this.includedMinutes = includedMinutes;
        this.includedSms = includedSms;
        this.includedDataMB = includedDataMB;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return basePrice;
    }

    public String getDescription() {
        return String.format("Tarif: %s - %d Minuten, %d SMS, %d MB für %.2f€",
            name, includedMinutes, includedSms, includedDataMB, basePrice);
    }

    public int getIncludedMinutes() {
        return includedMinutes;
    }

    public int getIncludedSms() {
        return includedSms;
    }

    public int getIncludedDataMB() {
        return includedDataMB;
    }
}