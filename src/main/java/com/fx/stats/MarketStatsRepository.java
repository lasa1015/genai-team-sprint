package com.fx.stats;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MarketStatsRepository {

    private final JdbcTemplate jdbc;

    public MarketStatsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public MarketStats fetch() {
        Long totalTransfers = jdbc.queryForObject("SELECT COUNT(*) FROM transfer", Long.class);
        String busiestCurrency = jdbc.queryForObject("""
                SELECT currency_code
                FROM transfer
                GROUP BY currency_code
                ORDER BY COUNT(*) DESC, currency_code
                LIMIT 1
                """, String.class);
        java.sql.Date latestRateDate = jdbc.queryForObject("SELECT MAX(rate_date) FROM fx_rate", java.sql.Date.class);

        return new MarketStats(
                totalTransfers == null ? 0 : totalTransfers,
                busiestCurrency,
                latestRateDate == null ? null : latestRateDate.toLocalDate());
    }
}