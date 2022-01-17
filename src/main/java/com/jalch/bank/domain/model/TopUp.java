package com.jalch.bank.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Builder
public class TopUp {
    private final long accountId;
    private final BigDecimal amount;
}
