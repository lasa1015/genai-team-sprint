package com.fx.convert;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Locale;

@RestController
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/api/convert")
    public ConversionQuote convert(
            @RequestParam String base,
            @RequestParam String quote,
            @RequestParam BigDecimal amount) {
        return conversionService.convert(
                base.toUpperCase(Locale.ROOT),
                quote.toUpperCase(Locale.ROOT),
                amount);
    }
}