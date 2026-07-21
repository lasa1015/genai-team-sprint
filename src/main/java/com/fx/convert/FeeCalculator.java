package com.fx.convert;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FeeCalculator {

    private static final BigDecimal SMALL_TIER_RATE = new BigDecimal("0.01");
    private static final BigDecimal MID_TIER_RATE = new BigDecimal("0.005");
    private static final BigDecimal LARGE_TIER_RATE = new BigDecimal("0.0025");
    private static final BigDecimal SMALL_TIER_LIMIT = new BigDecimal("1000");
    private static final BigDecimal LARGE_TIER_LIMIT = new BigDecimal("10000");
    private static final BigDecimal MINIMUM_FEE = new BigDecimal("1.00");

    public BigDecimal feeFor(BigDecimal amount) {
        requirePositive(amount);

        BigDecimal fee = amount.multiply(rateFor(amount)).setScale(2, RoundingMode.HALF_UP);
        return fee.max(MINIMUM_FEE).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal rateFor(BigDecimal amount) {
        if (amount.compareTo(SMALL_TIER_LIMIT) < 0) {
            return SMALL_TIER_RATE;
        }
        if (amount.compareTo(LARGE_TIER_LIMIT) < 0) {
            return MID_TIER_RATE;
        }
        return LARGE_TIER_RATE;
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
    }
}