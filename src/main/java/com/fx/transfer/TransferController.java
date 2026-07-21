package com.fx.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/api/transfers")
    public List<Transfer> all() {
        return transferService.history();
    }

    @PostMapping("/api/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public Transfer record(@RequestBody TransferRequest request) {
        if (request == null || request.amount() == null || request.base() == null || request.quote() == null) {
            throw new IllegalArgumentException("base, quote and amount are required");
        }
        return transferService.record(request);
    }
}