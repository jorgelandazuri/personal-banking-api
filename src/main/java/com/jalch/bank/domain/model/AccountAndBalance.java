package com.jalch.bank.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class AccountAndBalance {
    private final long accountId;
    private final BigDecimal balance;
}
