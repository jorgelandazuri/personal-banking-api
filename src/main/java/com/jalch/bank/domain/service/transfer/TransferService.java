package com.jalch.bank.domain.service.transfer;

import org.springframework.http.ResponseEntity;

public interface TransferService<T> {
    ResponseEntity<?> make(T transferRequest);
}
