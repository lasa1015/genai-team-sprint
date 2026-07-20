package com.fx.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * STUB — hardcoded exchange rates so the API answers from day one.
 *
 * These values are frozen copies of the 2026-01-12 rates that will later
 * live in the fxdb database. In Week 2 Day 2 you will DELETE this class
 * and replace it with a real, layered API that reads from MySQL.
 * Until then: run the app, curl it, and leave the internals alone.
 */
@RestController
public class StubRateController {

    @GetMapping("/api/rates")
    public List<Map<String, Object>> rates() {
        return List.of(
                Map.of("base", "EUR", "quote", "USD", "rate", 1.0818, "rateDate", "2026-01-12", "source", "stub"),
                Map.of("base", "GBP", "quote", "USD", "rate", 1.2705, "rateDate", "2026-01-12", "source", "stub"),
                Map.of("base", "USD", "quote", "JPY", "rate", 147.52, "rateDate", "2026-01-12", "source", "stub")
        );
    }
}
