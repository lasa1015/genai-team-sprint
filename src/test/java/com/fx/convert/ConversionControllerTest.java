package com.fx.convert;

import com.fx.rates.Rate;
import com.fx.rates.RateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversionController.class)
@Import({ConversionService.class, FeeCalculator.class})
class ConversionControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateRepository rateRepository;

    @Test
    void convertsUsingTheLatestRate() throws Exception {
        when(rateRepository.findLatestRate("EUR", "USD")).thenReturn(Optional.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.parse("2026-01-12"))));

        mvc.perform(get("/api/convert")
                        .queryParam("base", "EUR")
                        .queryParam("quote", "USD")
                        .queryParam("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.rate").value(1.0818))
                .andExpect(jsonPath("$.converted").value(108.18))
                .andExpect(jsonPath("$.fee").value(1.00))
                .andExpect(jsonPath("$.total").value(101.00));
    }

    @Test
    void rejectsAmountsThatAreNotPositive() throws Exception {
        mvc.perform(get("/api/convert")
                        .queryParam("base", "EUR")
                        .queryParam("quote", "USD")
                        .queryParam("amount", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("amount must be greater than 0"))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void rejectsMissingAmountWithJson400() throws Exception {
        mvc.perform(get("/api/convert")
                        .queryParam("base", "EUR")
                        .queryParam("quote", "USD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("amount is required"))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void rejectsNonNumericAmountWithJson400() throws Exception {
        mvc.perform(get("/api/convert")
                        .queryParam("base", "EUR")
                        .queryParam("quote", "USD")
                        .queryParam("amount", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("amount must be numeric"))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void returnsJson404ForUnknownPair() throws Exception {
        when(rateRepository.findLatestRate("EUR", "XXX")).thenReturn(Optional.empty());

        mvc.perform(get("/api/convert")
                        .queryParam("base", "EUR")
                        .queryParam("quote", "XXX")
                        .queryParam("amount", "100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("unknown currency pair: EUR/XXX"))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }
}