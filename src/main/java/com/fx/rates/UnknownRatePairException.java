package com.fx.rates;

public class UnknownRatePairException extends RuntimeException {

    public UnknownRatePairException(String base, String quote) {
        super("unknown currency pair: " + base + "/" + quote);
    }
}