package com.fx.rates;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RateRepository {

    private static final RowMapper<Rate> MAPPER = (rs, rowNum) -> new Rate(
            rs.getString("base"),
            rs.getString("quote"),
            rs.getBigDecimal("rate"),
            rs.getDate("rateDate").toLocalDate());
    private static final RowMapper<RateHistoryPoint> HISTORY_MAPPER = (rs, rowNum) -> new RateHistoryPoint(
            rs.getBigDecimal("rate"),
            rs.getDate("rateDate").toLocalDate());

    private final JdbcTemplate jdbc;

    public RateRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Rate> findLatestRates() {
        return jdbc.query("""
                SELECT base_code AS base,
                       quote_code AS quote,
                       rate,
                       rate_date AS rateDate
                FROM (
                    SELECT base_code,
                           quote_code,
                           rate,
                           rate_date,
                           ROW_NUMBER() OVER (
                               PARTITION BY base_code, quote_code
                               ORDER BY rate_date DESC, id DESC
                           ) AS rn
                    FROM fx_rate
                ) latest
                WHERE rn = 1
                ORDER BY base_code, quote_code
                """, MAPPER);
    }

    public Optional<Rate> findLatestRate(String base, String quote) {
        return jdbc.query("""
                SELECT base,
                       quote,
                       rate,
                       rateDate
                FROM (
                    SELECT base_code AS base,
                           quote_code AS quote,
                           rate,
                           rate_date AS rateDate,
                           ROW_NUMBER() OVER (
                               PARTITION BY base_code, quote_code
                               ORDER BY rate_date DESC, id DESC
                           ) AS rn
                    FROM fx_rate
                    WHERE base_code = ? AND quote_code = ?
                ) latest
                WHERE rn = 1
                """, MAPPER, base, quote).stream().findFirst();
    }

    public List<RateHistoryPoint> findRateHistory(String base, String quote) {
        return jdbc.query("""
                SELECT rate,
                       rate_date AS rateDate
                FROM fx_rate
                WHERE base_code = ? AND quote_code = ?
                ORDER BY rate_date ASC, id ASC
                """, HISTORY_MAPPER, base, quote);
    }
}