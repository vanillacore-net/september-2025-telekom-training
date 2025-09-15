package com.telekom.training.day2.tarifoptions.initial;

import java.math.BigDecimal;

public class BasicTariffWithFlatrateAndRoaming extends BasicTariff {
    private static final BigDecimal FLATRATE_COST = new BigDecimal("15.00");
    private static final BigDecimal ROAMING_COST = new BigDecimal("8.50");

    public BasicTariffWithFlatrateAndRoaming(String name, BigDecimal basePrice, int includedMinutes, 
                                            int includedSms, int includedDataMB) {
        super(name, basePrice, includedMinutes, includedSms, includedDataMB);
    }

    @Override
    public BigDecimal getPrice() {
        return basePrice.add(FLATRATE_COST).add(ROAMING_COST);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Flatrate (+15.00€) + EU-Roaming (+8.50€)";
    }

    @Override
    public int getIncludedMinutes() {
        return Integer.MAX_VALUE; // Unlimited
    }
}