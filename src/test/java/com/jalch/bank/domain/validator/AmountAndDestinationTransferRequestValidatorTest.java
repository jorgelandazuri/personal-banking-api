package com.jalch.bank.domain.validator;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.rest.request.TransferRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static com.jalch.bank.domain.validator.ValidationResult.Result.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AmountAndDestinationTransferRequestValidatorTest {

    @Mock
    private AccountRepository accountRepository;

    private AmountAndDestinationTransferRequestValidator underTest;

    @Before
    public void setUp() {
        underTest = new AmountAndDestinationTransferRequestValidator(accountRepository);
    }

    @Test
    public void invalidTransfer_nullRequest() {
        assertSame(INVALID, underTest.validate(null).getResult());
    }

    @Test
    public void validTransfer() {
        BigDecimal amount = new BigDecimal(100.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        TransferRequest transferRequest = TransferRequest.builder().sourceAccountId(1)
                .destinationAccountId(2)
                .amount(amount).build();
        when(accountRepository.findById(2L)).thenReturn(Optional.of(mock(AccountDTO.class)));
        assertSame(VALID, underTest.validate(transferRequest).getResult());
    }
    @Test
    public void invalidTransfer_invalidAmount() {
        when(accountRepository.findById(2L)).thenReturn(Optional.of(mock(AccountDTO.class)));

        TransferRequest nullAmountTransferRequest = TransferRequest.builder().sourceAccountId(1)
                .destinationAccountId(2L)
                .amount(null).build();
        ValidationResult nullAmountValidation = underTest.validate(nullAmountTransferRequest);
        assertSame(INVALID, nullAmountValidation.getResult());
        assertSame(1, nullAmountValidation.getValidationMessages().size());
        assertSame("must be greater than zero", nullAmountValidation.getValidationMessages().get("amount"));

        TransferRequest negativeAmountTransferRequest = TransferRequest.builder().sourceAccountId(1)
                .destinationAccountId(2)
                .amount(BigDecimal.valueOf(-0.01).setScale(2, BigDecimal.ROUND_HALF_EVEN)).build();
        ValidationResult negativeAmountValidation = underTest.validate(negativeAmountTransferRequest);
        assertSame(INVALID, negativeAmountValidation.getResult());
        assertSame(1, negativeAmountValidation.getValidationMessages().size());
        assertSame("must be greater than zero", negativeAmountValidation.getValidationMessages().get("amount") );

        TransferRequest zeroAmountTransferRequest = TransferRequest.builder().sourceAccountId(1)
                .destinationAccountId(2)
                .amount(BigDecimal.valueOf(0.00).setScale(2, BigDecimal.ROUND_HALF_EVEN)).build();
        ValidationResult zeroAmountValidation = underTest.validate(zeroAmountTransferRequest);
        assertSame(INVALID, zeroAmountValidation.getResult());
        assertSame(1, zeroAmountValidation.getValidationMessages().size());
        assertSame("must be greater than zero", zeroAmountValidation.getValidationMessages().get("amount"));
    }

    @Test
    public void invalidTransfer_nonExistingDestinationAccount() {
        TransferRequest transferRequest = TransferRequest.builder().sourceAccountId(1)
                .destinationAccountId(2)
                .amount(new BigDecimal(200.99).setScale(2, BigDecimal.ROUND_HALF_EVEN)).build();

        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        ValidationResult validationResult = underTest.validate(transferRequest);
        assertSame(INVALID, validationResult.getResult());
        assertSame("destination account does not exist", validationResult.getValidationMessages().get("destinationAccountId") );
    }

}