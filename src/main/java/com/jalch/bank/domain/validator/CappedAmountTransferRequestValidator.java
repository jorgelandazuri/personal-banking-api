package com.jalch.bank.domain.validator;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.rest.request.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.jalch.bank.domain.validator.ValidationResult.Result.INVALID;

@Component
public class CappedAmountTransferRequestValidator implements Validator<TransferRequest> {

    @Value("${bank.max.transfer.amount}")
    private BigDecimal maxAmountPerTransfer;

    private final AmountAndDestinationTransferRequestValidator validationHelper;

    private final AccountRepository accountRepository;

    @Autowired
    public CappedAmountTransferRequestValidator(AmountAndDestinationTransferRequestValidator validationHelper,
                                                AccountRepository accountRepository) {
        this.validationHelper = validationHelper;
        this.accountRepository = accountRepository;
    }

    @Override
    public ValidationResult validate(TransferRequest transferRequest) {
        ValidationResult amountAndDestinationValidation = this.validationHelper.validate(transferRequest);
        boolean amountAndDestinationValid = amountAndDestinationValidation.getResult() == ValidationResult.Result.VALID;
        boolean sourceAccountExistsAndHasEnoughBalance = this.sourceAccountExistsAndHasEnoughBalance(
                transferRequest.getAmount(), transferRequest.getSourceAccountId());
        boolean transferAmountLessOrEqualsThanCap = this.transferAmountLessOrEqualsThanCap(transferRequest.getAmount());

        if (amountAndDestinationValid && sourceAccountExistsAndHasEnoughBalance && transferAmountLessOrEqualsThanCap) {
            return ValidationResult.builder().result(ValidationResult.Result.VALID).build();
        } else {
            ValidationResult.ValidationResultBuilder invalidResult = ValidationResult.builder().result(INVALID);
            Map<String, String> validationMessages = new HashMap<>();
            if(!amountAndDestinationValid) validationMessages.putAll(amountAndDestinationValidation.getValidationMessages());
            if(!sourceAccountExistsAndHasEnoughBalance) validationMessages.put("accountBalance", "source account does not exist or has not enough balance");
            if(!transferAmountLessOrEqualsThanCap) validationMessages.put("maxAmount", "amount is greater than daily cap");

            return invalidResult.validationMessages(validationMessages).build();
        }
    }

    private boolean sourceAccountExistsAndHasEnoughBalance(BigDecimal amount, long sourceAccountId) {
        Optional<AccountDTO> sourceAccount = accountRepository.findById(sourceAccountId);
        return sourceAccount.isPresent() && sourceAccount.get().getBalance().compareTo(amount) >= 0;
    }

    private boolean transferAmountLessOrEqualsThanCap(BigDecimal amount) {
        return maxAmountPerTransfer.compareTo(amount) >= 0;
    }
}
