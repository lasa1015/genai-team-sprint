package com.fx.convert;

import com.fx.rates.Rate;
import com.fx.rates.RateRepository;
import com.fx.rates.UnknownRatePairException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ConversionService {

    private final RateRepository rateRepository;
    private final FeeCalculator feeCalculator;

    public ConversionService(RateRepository rateRepository, FeeCalculator feeCalculator) {
        this.rateRepository = rateRepository;
        this.feeCalculator = feeCalculator;
    }

    public ConversionQuote convert(String base, String quote, BigDecimal amount) {
        BigDecimal fee = feeCalculator.feeFor(amount);
        Rate rate = rateRepository.findLatestRate(base, quote)
                .orElseThrow(() -> new UnknownRatePairException(base, quote));

        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal converted = amount.multiply(rate.rate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = normalizedAmount.add(fee).setScale(2, RoundingMode.HALF_UP);

        return new ConversionQuote(
                normalizedAmount,
                rate.rate(),
                converted,
                fee,
                total);
    }
}