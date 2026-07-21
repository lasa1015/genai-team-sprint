package com.fx.convert;

import java.math.BigDecimal;

public record ConversionQuote(
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal converted,
        BigDecimal fee,
        BigDecimal total) {
}