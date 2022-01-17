package com.jalch.bank.domain.model;

import com.jalch.bank.data.dto.TransactionDTO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransferInfo {
    private TransactionDTO transactionDTO;
    private long queriedAccountId;
}
