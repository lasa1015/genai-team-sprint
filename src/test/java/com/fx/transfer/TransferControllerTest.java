package com.fx.transfer;

import com.fx.convert.ConversionService;
import com.fx.convert.FeeCalculator;
import com.fx.rates.Rate;
import com.fx.rates.RateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
@Import({TransferService.class, ConversionService.class, FeeCalculator.class})
class TransferControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TransferRepository transferRepository;

    @MockBean
    RateRepository rateRepository;

    @Test
    void recordsATransferAfterAConversion() throws Exception {
        when(rateRepository.findLatestRate("EUR", "USD")).thenReturn(Optional.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.parse("2026-01-12"))));
        when(transferRepository.findFirstAccountIdByCurrency("EUR")).thenReturn(Optional.of(1));
        when(transferRepository.findFirstAccountIdByCurrency("USD")).thenReturn(Optional.of(4));
        when(transferRepository.add(any(Transfer.class))).thenReturn(1);

        mvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"base\":\"EUR\",\"quote\":\"USD\",\"amount\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fromAccount").value(1))
                .andExpect(jsonPath("$.toAccount").value(4))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(transferRepository).add(any(Transfer.class));
    }

    @Test
    void returnsTransfersNewestFirst() throws Exception {
        when(transferRepository.findAll()).thenReturn(List.of(
                new Transfer(202, 1, 4, new BigDecimal("300.00"), "EUR", LocalDateTime.parse("2026-07-21T11:00:00"), "COMPLETED"),
                new Transfer(201, 1, 4, new BigDecimal("100.00"), "EUR", LocalDateTime.parse("2026-07-21T10:00:00"), "COMPLETED")));

        mvc.perform(get("/api/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(300.00))
                .andExpect(jsonPath("$[0].executedAt").value("2026-07-21T11:00:00"))
                .andExpect(jsonPath("$[1].amount").value(100.00));
    }
}