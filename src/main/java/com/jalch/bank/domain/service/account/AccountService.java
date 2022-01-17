package com.jalch.bank.domain.service.account;

import org.springframework.http.ResponseEntity;

public interface AccountService<R,B> {
    ResponseEntity create(R accountCreationRequest);
    B getBalance(long accountId);
}
