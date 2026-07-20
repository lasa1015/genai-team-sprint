package com.fx.rates;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}