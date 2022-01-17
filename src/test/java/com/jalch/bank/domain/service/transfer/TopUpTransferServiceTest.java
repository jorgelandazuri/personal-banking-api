package com.jalch.bank.domain.service.transfer;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.TransactionDTO;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.data.repository.TransactionRepository;
import com.jalch.bank.domain.converter.todto.TransferRequestToTransactionDTOConverter;
import com.jalch.bank.domain.model.TopUp;
import com.jalch.bank.domain.validator.AmountAndDestinationTransferRequestValidator;
import com.jalch.bank.domain.validator.ValidationResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.jalch.bank.domain.validator.ValidationResult.Result.INVALID;
import static com.jalch.bank.domain.validator.ValidationResult.Result.VALID;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.ResponseEntity.status;

@RunWith(MockitoJUnitRunner.class)
public class TopUpTransferServiceTest {

    @Mock
    private AmountAndDestinationTransferRequestValidator validator;
    @Mock
    private TransferRequestToTransactionDTOConverter converter;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private TopUpTransferService underTest;

    @Before
    public void setUp() {
        underTest = new TopUpTransferService(validator, converter, accountRepository,
                transactionRepository);
    }

    @Test
    public void topUpSuccess() {
        BigDecimal amount = new BigDecimal(100.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        TopUp topUp = TopUp.builder().amount(amount).accountId(2).build();
        when(validator.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());

        TransactionDTO convertedTransaction = mock(TransactionDTO.class);
        when(converter.convert(any())).thenReturn(Optional.of(convertedTransaction));

        BigDecimal balance = new BigDecimal(30.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        AccountDTO account = this.getAccount(2L, balance);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));

        assertSame(HttpStatus.OK, underTest.make(topUp).getStatusCode());
        assertThat(account.getBalance().toPlainString(), is("130.00"));
        Mockito.verify(transactionRepository, times(1)).save(convertedTransaction);
        Mockito.verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void topUpFailure_invalidRequest() {
        when(validator.validate(any())).thenReturn(ValidationResult.builder().result(INVALID)
                .validationMessages(singletonMap("validation", "error")).build());
        assertSame(HttpStatus.BAD_REQUEST, underTest.make(mock(TopUp.class)).getStatusCode());
    }

    @Test
    public void topUpFailure_requestConversionFailure() {
        when(validator.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());
        when(converter.convert(any())).thenReturn(Optional.empty());
        assertSame(HttpStatus.NOT_FOUND, underTest.make(mock(TopUp.class)).getStatusCode());
    }

    private AccountDTO getAccount(long accountId, BigDecimal balance) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(accountId);
        accountDTO.setBalance(balance);
        return accountDTO;
    }
}