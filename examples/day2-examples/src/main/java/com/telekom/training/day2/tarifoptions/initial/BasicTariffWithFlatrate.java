package com.telekom.training.day2.tarifoptions.initial;

import java.math.BigDecimal;

/**
 * PROBLEM: Explosion of subclasses
 * We need a separate class for every combination of options
 */
public class BasicTariffWithFlatrate extends BasicTariff {
    private static final BigDecimal FLATRATE_COST = new BigDecimal("15.00");

    public BasicTariffWithFlatrate(String name, BigDecimal basePrice, int includedMinutes, 
                                  int includedSms, int includedDataMB) {
        super(name, basePrice, includedMinutes, includedSms, includedDataMB);
    }

    @Override
    public BigDecimal getPrice() {
        return basePrice.add(FLATRATE_COST);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Flatrate (+15.00€)";
    }

    @Override
    public int getIncludedMinutes() {
        return Integer.MAX_VALUE; // Unlimited
    }
}