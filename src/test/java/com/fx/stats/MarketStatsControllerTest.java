package com.fx.stats;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarketStatsController.class)
class MarketStatsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MarketStatsRepository repo;

    @Test
    void returnsMarketStatsAsJson() throws Exception {
        when(repo.fetch()).thenReturn(new MarketStats(200, "EUR", LocalDate.parse("2026-01-12")));

        mvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransfers").value(200))
                .andExpect(jsonPath("$.busiestCurrency").value("EUR"))
                .andExpect(jsonPath("$.latestRateDate").value("2026-01-12"));
    }
}