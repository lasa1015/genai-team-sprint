package com.fx.rates;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Rate(String base, String quote, BigDecimal rate, LocalDate rateDate) {
}