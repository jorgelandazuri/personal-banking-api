package com.jalch.bank.rest.request;

import lombok.*;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountCreationRequest {

    @NotBlank
    @Pattern(regexp = "^(?!.*§00_1£).*", message = "Invalid document id")
    private String documentId;
    @NotBlank
    private String nameAndSurname;
    @Min(0)
    private BigDecimal initialDeposit;
}
