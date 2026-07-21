package com.fx.transfer;

import java.math.BigDecimal;

public record TransferRequest(String base, String quote, BigDecimal amount) {
}