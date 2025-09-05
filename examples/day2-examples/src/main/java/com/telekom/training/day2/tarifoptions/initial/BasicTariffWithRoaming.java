package com.telekom.training.day2.tarifoptions.initial;

import java.math.BigDecimal;

/**
 * PROBLEM: More class explosion
 * What if we want Flatrate AND Roaming? Need another class!
 */
public class BasicTariffWithRoaming extends BasicTariff {
    private static final BigDecimal ROAMING_COST = new BigDecimal("8.50");

    public BasicTariffWithRoaming(String name, BigDecimal basePrice, int includedMinutes, 
                                 int includedSms, int includedDataMB) {
        super(name, basePrice, includedMinutes, includedSms, includedDataMB);
    }

    @Override
    public BigDecimal getPrice() {
        return basePrice.add(ROAMING_COST);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + EU-Roaming (+8.50€)";
    }
}