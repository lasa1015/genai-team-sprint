package com.fx.rates;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RateHistoryPoint(BigDecimal rate, LocalDate rateDate) {
}