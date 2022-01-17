package com.jalch.bank.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class Transfer {
    private final long id;
    private final Type type;
    private final long counterpartyAccountId;
    private final String counterpartyAccountHolder;
    private final BigDecimal amount;

    public enum Type {
        RECEIVED,
        SENT,
        DEPOSIT,
        BANK_DEBIT,
        BANK_CREDIT
    }
}


