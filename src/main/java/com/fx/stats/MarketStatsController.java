package com.fx.stats;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarketStatsController {

    private final MarketStatsRepository repo;

    public MarketStatsController(MarketStatsRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/stats")
    public MarketStats stats() {
        return repo.fetch();
    }
}