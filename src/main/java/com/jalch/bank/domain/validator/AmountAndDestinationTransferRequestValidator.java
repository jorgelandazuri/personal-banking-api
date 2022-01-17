package com.jalch.bank.domain.validator;

import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.rest.request.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.jalch.bank.domain.validator.ValidationResult.Result.INVALID;
import static java.util.Collections.singletonMap;

@Component
public class AmountAndDestinationTransferRequestValidator implements Validator<TransferRequest> {

    private AccountRepository accountRepository;

    @Autowired
    public AmountAndDestinationTransferRequestValidator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public ValidationResult validate(TransferRequest transferRequest) {
        if (transferRequest == null)
            return ValidationResult.builder()
                .result(INVALID)
                .validationMessages(singletonMap("request", "invalid request, it cannot be null"))
                .build();

        BigDecimal amount = transferRequest.getAmount();
        boolean amountIsValid = this.amountIsValid(amount);
        boolean destinationAccountExists = this.destinationAccountExists(transferRequest.getDestinationAccountId());

        if(amountIsValid && destinationAccountExists)  {
            return ValidationResult.builder().result(ValidationResult.Result.VALID).build();
        } else {
            ValidationResult.ValidationResultBuilder invalidResult = ValidationResult.builder().result(INVALID);
            Map<String, String> validationMessages = new HashMap<>();
            if (!amountIsValid) validationMessages.put("amount", "must be greater than zero");
            if (!destinationAccountExists) validationMessages.put("destinationAccountId", "destination account does not exist");
            return invalidResult.validationMessages(validationMessages).build();
        }
    }

    private boolean amountIsValid(BigDecimal amount) {
        BigDecimal zero = new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return amount != null && amount.compareTo(zero) != 0 && amount.signum() != -1;
    }

    private boolean destinationAccountExists(long destinationAccountId) {
        return accountRepository.findById(destinationAccountId).isPresent();
    }
}
