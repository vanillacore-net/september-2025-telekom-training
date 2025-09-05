package com.telekom.training.day2.tarifoptions.fixed;

import java.math.BigDecimal;

/**
 * Decorator Pattern: Component interface
 * Defines the operations that can be performed on both simple and decorated objects
 */
public interface Tariff {
    String getName();
    BigDecimal getPrice();
    String getDescription();
    int getIncludedMinutes();
    int getIncludedSms();
    int getIncludedDataMB();
}