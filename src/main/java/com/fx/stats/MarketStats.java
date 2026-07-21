package com.fx.stats;

import java.time.LocalDate;

public record MarketStats(long totalTransfers, String busiestCurrency, LocalDate latestRateDate) {
}