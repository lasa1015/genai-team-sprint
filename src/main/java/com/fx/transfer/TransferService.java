package com.fx.transfer;

import com.fx.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class TransferService {

    private static final String COMPLETED = "COMPLETED";

    private final TransferRepository transferRepository;
    private final ConversionService conversionService;

    public TransferService(TransferRepository transferRepository, ConversionService conversionService) {
        this.transferRepository = transferRepository;
        this.conversionService = conversionService;
    }

    public Transfer record(TransferRequest request) {
        String base = normalize(request.base());
        String quote = normalize(request.quote());

        conversionService.convert(base, quote, request.amount());

        int fromAccount = transferRepository.findFirstAccountIdByCurrency(base)
                .orElseThrow(() -> new IllegalArgumentException("no account available for currency " + base));
        int toAccount = transferRepository.findFirstAccountIdByCurrency(quote)
                .orElseThrow(() -> new IllegalArgumentException("no account available for currency " + quote));

        Transfer transfer = new Transfer(
                null,
                fromAccount,
                toAccount,
                request.amount(),
                base,
                LocalDateTime.now(),
                COMPLETED);
        transferRepository.add(transfer);
        return transfer;
    }

    public List<Transfer> history() {
        return transferRepository.findAll();
    }

    private String normalize(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("base and quote are required");
        }
        return currency.toUpperCase(Locale.ROOT);
    }
}