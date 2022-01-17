package com.jalch.bank.rest.request;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransferRequest {
    private long sourceAccountId;
    private long destinationAccountId;
    @NotNull
    private BigDecimal amount;
}
