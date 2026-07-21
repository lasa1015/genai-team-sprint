package com.fx.transfer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class TransferRepository {

    private static final RowMapper<Transfer> MAPPER = (rs, rowNum) -> new Transfer(
            rs.getInt("id"),
            rs.getInt("fromAccount"),
            rs.getInt("toAccount"),
            rs.getBigDecimal("amount"),
            rs.getString("currency"),
            rs.getTimestamp("executedAt").toLocalDateTime(),
            rs.getString("status"));

    private final JdbcTemplate jdbc;

    public TransferRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Transfer> findAll() {
        return jdbc.query("""
                SELECT id,
                       from_account AS fromAccount,
                       to_account AS toAccount,
                       amount,
                       currency_code AS currency,
                       executed_at AS executedAt,
                       status
                FROM transfer
                ORDER BY executed_at DESC, id DESC
                """, MAPPER);
    }

    public int add(Transfer transfer) {
        return jdbc.update("""
                INSERT INTO transfer (from_account, to_account, amount, currency_code, executed_at, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                transfer.fromAccount(),
                transfer.toAccount(),
                transfer.amount(),
                transfer.currency(),
                Timestamp.valueOf(transfer.executedAt()),
                transfer.status());
    }

    public Optional<Integer> findFirstAccountIdByCurrency(String currency) {
        return jdbc.queryForList("""
                SELECT id
                FROM account
                WHERE currency_code = ?
                ORDER BY id
                LIMIT 1
                """, Integer.class, currency).stream().findFirst();
    }
}