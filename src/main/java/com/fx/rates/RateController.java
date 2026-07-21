package com.fx.rates;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
public class RateController {

    private final RateRepository repo;

    public RateController(RateRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/rates")
    public List<Rate> all() {
        return repo.findLatestRates();
    }

    @GetMapping("/api/rates/{base}/{quote}")
    public Rate one(@PathVariable String base, @PathVariable String quote) {
        String normalizedBase = base.toUpperCase(Locale.ROOT);
        String normalizedQuote = quote.toUpperCase(Locale.ROOT);
        return repo.findLatestRate(normalizedBase, normalizedQuote)
                .orElseThrow(() -> new UnknownRatePairException(normalizedBase, normalizedQuote));
    }

    @GetMapping("/api/rates/{base}/{quote}/history")
    public List<RateHistoryPoint> history(@PathVariable String base, @PathVariable String quote) {
        String normalizedBase = base.toUpperCase(Locale.ROOT);
        String normalizedQuote = quote.toUpperCase(Locale.ROOT);
        return repo.findRateHistory(normalizedBase, normalizedQuote);
    }
}