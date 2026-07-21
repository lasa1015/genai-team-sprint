package com.fx.convert;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeeCalculatorTest {

    private final FeeCalculator feeCalculator = new FeeCalculator();

    @Test
    void amountBelowThousandPaysOnePercent() {
        assertThat(feeCalculator.feeFor(new BigDecimal("500")))
                .isEqualByComparingTo("5.00");
    }

    @Test
    void exactlyOneThousandStartsTheMidTier() {
        assertThat(feeCalculator.feeFor(new BigDecimal("1000")))
                .isEqualByComparingTo("5.00");
    }

    @Test
    void checkpointAmountFiveThousandPaysTwentyFive() {
        assertThat(feeCalculator.feeFor(new BigDecimal("5000")))
                .isEqualByComparingTo("25.00");
    }

    @Test
    void exactlyTenThousandStartsTheLargeTier() {
        assertThat(feeCalculator.feeFor(new BigDecimal("10000")))
                .isEqualByComparingTo("25.00");
    }

    @Test
    void checkpointAmountOneHundredPaysOne() {
        assertThat(feeCalculator.feeFor(new BigDecimal("100")))
                .isEqualByComparingTo("1.00");
    }

    @Test
    void minimumFeeFloorAppliesToSmallAmounts() {
        assertThat(feeCalculator.feeFor(new BigDecimal("50")))
                .isEqualByComparingTo("1.00");
    }

    @Test
    void rejectsZeroOrNegativeAmounts() {
        assertThatThrownBy(() -> feeCalculator.feeFor(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("amount must be greater than 0");

        assertThatThrownBy(() -> feeCalculator.feeFor(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("amount must be greater than 0");
    }
}