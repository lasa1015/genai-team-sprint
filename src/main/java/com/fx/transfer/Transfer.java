package com.fx.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transfer(
        Integer id,
        int fromAccount,
        int toAccount,
        BigDecimal amount,
        String currency,
        LocalDateTime executedAt,
        String status) {
}