package com.jalch.bank.domain.validator;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.rest.request.TransferRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static com.jalch.bank.domain.validator.ValidationResult.Result.INVALID;
import static com.jalch.bank.domain.validator.ValidationResult.Result.VALID;
import static java.math.BigDecimal.TEN;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class CappedAmountTransferRequestValidatorTest {

    public static final BigDecimal MAX_TRANSFER = new BigDecimal(10000.00)
            .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    @Mock
    private AmountAndDestinationTransferRequestValidator validationHelper;
    @Mock
    private AccountRepository accountRepository;

    private CappedAmountTransferRequestValidator underTest;

    @Before
    public void setUp() {
        underTest = new CappedAmountTransferRequestValidator(validationHelper, accountRepository);
        ReflectionTestUtils.setField(underTest, "maxAmountPerTransfer", MAX_TRANSFER);
    }

    @Test
    public void invalidTransfer_amountAndDestinationValidationInvalid() {
        when(validationHelper.validate(any())).thenReturn(ValidationResult.builder().result(INVALID)
                .validationMessages(singletonMap("validation", "error")).build());
        when(accountRepository.findById(any())).thenReturn(Optional.of(this.getAccountDTOWithBalance(1, TEN)));

        TransferRequest transferRequest = TransferRequest.builder().amount(TEN).sourceAccountId(1L).build();
        assertSame(INVALID, underTest.validate(transferRequest).getResult());
    }

    @Test
    public void validTransfer_lessOrEqualThanMaxAmount() {
        BigDecimal amount = new BigDecimal(10000.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        when(validationHelper.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());
        AccountDTO sourceAccount = this.getAccountDTOWithBalance(1, amount);
        when(accountRepository.findById(any())).thenReturn(Optional.of(sourceAccount));

        TransferRequest transferWithEqualAmountToCap = TransferRequest.builder().amount(amount).build();
        assertSame(VALID, underTest.validate(transferWithEqualAmountToCap).getResult());

        BigDecimal almostMaxAmount = new BigDecimal(9999.99).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        when(accountRepository.findById(any())).thenReturn(Optional.of(sourceAccount));

        TransferRequest transferWithAmountLessThanCap = TransferRequest.builder().amount(almostMaxAmount).build();
        assertSame(VALID, underTest.validate(transferWithAmountLessThanCap).getResult());
    }

    @Test
    public void invalidTransfer_moreThanMaxAmount() {
        BigDecimal amount = new BigDecimal(10000.01).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        when(validationHelper.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());
        AccountDTO sourceAccount = this.getAccountDTOWithBalance(1, amount);
        when(accountRepository.findById(any())).thenReturn(Optional.of(sourceAccount));

        TransferRequest transferRequest = TransferRequest.builder().amount(amount).build();
        assertSame(INVALID, underTest.validate(transferRequest).getResult());
        assertSame("amount is greater than daily cap",
                underTest.validate(transferRequest).getValidationMessages().get("maxAmount"));
    }

    @Test
    public void invalidTransfer_nonExistingSourceAccount() {
        when(validationHelper.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());
        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        TransferRequest transferRequest = TransferRequest.builder().amount(TEN).sourceAccountId(-1L).build();

        assertSame(INVALID, underTest.validate(transferRequest).getResult());
        assertSame("source account does not exist or has not enough balance",
                underTest.validate(transferRequest).getValidationMessages().get("accountBalance"));
    }

    @Test
    public void validTransfer_exactAmount() {
        when(validationHelper.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());
        BigDecimal amount = new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        TransferRequest transferRequest = TransferRequest.builder().sourceAccountId(1)
                .destinationAccountId(2).amount(amount).build();
        AccountDTO sourceAccount = this.getAccountDTOWithBalance(1, amount);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));

        assertSame(VALID, underTest.validate(transferRequest).getResult());
    }

    @Test
    public void validTransfer_almostAllBalance() {
        when(validationHelper.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());
        BigDecimal amount = new BigDecimal(100.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        TransferRequest transferRequest = TransferRequest.builder().sourceAccountId(1)
                .destinationAccountId(2)
                .amount(amount).build();
        BigDecimal balance = new BigDecimal(100.01).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        AccountDTO sourceAccount = this.getAccountDTOWithBalance(1, balance);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));

        assertSame(VALID, underTest.validate(transferRequest).getResult());
    }

    @Test
    public void invalidTransfer_transferGreaterThanBalance() {
        when(validationHelper.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());
        BigDecimal amount = new BigDecimal(100.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        TransferRequest transferRequest = TransferRequest.builder().sourceAccountId(1)
                .destinationAccountId(2)
                .amount(amount).build();
        BigDecimal balance = new BigDecimal(99.99).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        AccountDTO sourceAccount = this.getAccountDTOWithBalance(1, balance);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));

        assertSame(INVALID, underTest.validate(transferRequest).getResult());
        assertSame("source account does not exist or has not enough balance",
                underTest.validate(transferRequest).getValidationMessages().get("accountBalance"));
    }

    private AccountDTO getAccountDTOWithBalance(long accountId, BigDecimal balance) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setBalance(balance);
        accountDTO.setId(accountId);
        return accountDTO;
    }
}