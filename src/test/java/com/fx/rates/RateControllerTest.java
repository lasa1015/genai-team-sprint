package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RateController.class)
class RateControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateRepository repo;

    @Test
    void returnsLatestRatesAsJson() throws Exception {
        when(repo.findLatestRates()).thenReturn(List.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.parse("2026-01-12")),
                new Rate("GBP", "USD", new BigDecimal("1.2629"), LocalDate.parse("2026-01-12"))));

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].base").value("EUR"))
                .andExpect(jsonPath("$[0].quote").value("USD"))
                .andExpect(jsonPath("$[0].rate").value(1.0818))
                .andExpect(jsonPath("$[0].rateDate").value("2026-01-12"));
    }

    @Test
    void returnsEmptyArrayWhenDatabaseHasNoRates() throws Exception {
        when(repo.findLatestRates()).thenReturn(List.of());

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void returnsSinglePairRateAsJson() throws Exception {
        when(repo.findLatestRate("EUR", "USD")).thenReturn(Optional.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.parse("2026-01-12"))));

        mvc.perform(get("/api/rates/EUR/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("EUR"))
                .andExpect(jsonPath("$.quote").value("USD"))
                .andExpect(jsonPath("$.rate").value(1.0818))
                .andExpect(jsonPath("$.rateDate").value("2026-01-12"));
    }

    @Test
    void returnsJson404ForUnknownPair() throws Exception {
        when(repo.findLatestRate("EUR", "XXX")).thenReturn(Optional.empty());

        mvc.perform(get("/api/rates/EUR/XXX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("unknown currency pair: EUR/XXX"));
    }

        @Test
        void returnsPairHistoryOldestToNewest() throws Exception {
                when(repo.findRateHistory("EUR", "USD")).thenReturn(List.of(
                                new RateHistoryPoint(new BigDecimal("1.0812"), LocalDate.parse("2026-01-10")),
                                new RateHistoryPoint(new BigDecimal("1.0881"), LocalDate.parse("2026-01-11")),
                                new RateHistoryPoint(new BigDecimal("1.0818"), LocalDate.parse("2026-01-12"))));

                mvc.perform(get("/api/rates/EUR/USD/history"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].rate").value(1.0812))
                                .andExpect(jsonPath("$[0].rateDate").value("2026-01-10"))
                                .andExpect(jsonPath("$[2].rate").value(1.0818))
                                .andExpect(jsonPath("$[2].rateDate").value("2026-01-12"));
        }

        @Test
        void returnsEmptyHistoryForUnknownPair() throws Exception {
                when(repo.findRateHistory("EUR", "XXX")).thenReturn(List.of());

                mvc.perform(get("/api/rates/EUR/XXX/history"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$").isEmpty());
        }
}