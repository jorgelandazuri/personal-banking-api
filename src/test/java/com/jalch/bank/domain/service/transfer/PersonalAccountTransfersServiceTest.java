package com.jalch.bank.domain.service.transfer;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.TransactionDTO;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.data.repository.TransactionRepository;
import com.jalch.bank.domain.converter.todto.TransferRequestToTransactionDTOConverter;
import com.jalch.bank.domain.converter.tomodel.TransferInfoToTransferConverter;
import com.jalch.bank.domain.model.Transfer;
import com.jalch.bank.domain.model.Transfers;
import com.jalch.bank.domain.validator.ValidationResult;
import com.jalch.bank.rest.request.TransferRequest;
import com.jalch.bank.domain.validator.CappedAmountTransferRequestValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.jalch.bank.domain.validator.ValidationResult.Result.INVALID;
import static com.jalch.bank.domain.validator.ValidationResult.Result.VALID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonalAccountTransfersServiceTest {

    @Mock
    private CappedAmountTransferRequestValidator validator;
    @Mock
    private TransferRequestToTransactionDTOConverter converter;
    @Mock
    private TransferInfoToTransferConverter toTransferConverter;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private PersonalAccountTransfersService underTest;

    @Before
    public void setUp() {
        underTest = new PersonalAccountTransfersService(validator, converter, toTransferConverter,
                accountRepository, transactionRepository);
    }

    @Test
    public void transferSuccess() {
        BigDecimal amount = new BigDecimal(100.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        TransferRequest transferRequest = TransferRequest.builder().amount(amount)
                .sourceAccountId(2).destinationAccountId(3).build();
        when(validator.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());

        TransactionDTO convertedTransaction = mock(TransactionDTO.class);
        when(converter.convert(any())).thenReturn(Optional.of(convertedTransaction));

        BigDecimal sourceBalance = new BigDecimal(300.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal destinationBalance = new BigDecimal(30.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        AccountDTO sourceAccount = this.getAccount(2L, sourceBalance);
        AccountDTO destinationAccount = this.getAccount(3L, destinationBalance);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(3L)).thenReturn(Optional.of(destinationAccount));

        assertSame(HttpStatus.CREATED, underTest.make(transferRequest).getStatusCode());
        assertThat(sourceAccount.getBalance().toPlainString(), is("200.00"));
        assertThat(destinationAccount.getBalance().toPlainString(), is("130.00"));
        Mockito.verify(transactionRepository, times(1)).save(convertedTransaction);
        Mockito.verify(accountRepository, times(1)).save(sourceAccount);
        Mockito.verify(accountRepository, times(1)).save(destinationAccount);
    }

    @Test
    public void transferFailure_invalidRequest() {
        when(validator.validate(any())).thenReturn(ValidationResult.builder().result(INVALID).build());
        assertSame(HttpStatus.BAD_REQUEST, underTest.make(mock(TransferRequest.class)).getStatusCode());
    }

    @Test
    public void transferFailure_requestConversionFailure() {
        when(validator.validate(any())).thenReturn(ValidationResult.builder().result(VALID).build());
        when(converter.convert(any())).thenReturn(Optional.empty());
        assertSame(HttpStatus.NOT_FOUND, underTest.make(mock(TransferRequest.class)).getStatusCode());
    }

    @Test
    public void someTransfers() {
        BigDecimal balance = new BigDecimal(300.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        AccountDTO account = this.getAccount(2L, balance);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));

        TransactionDTO normalTransaction = mock(TransactionDTO.class);
        List<TransactionDTO> transactions = Arrays.asList(normalTransaction, normalTransaction);
        when(transactionRepository.findBySourceAccountOrDestinationAccountOrderByIdAsc(account, account)).thenReturn(transactions);
        when(toTransferConverter.convert(any())).thenReturn(Optional.of(mock(Transfer.class)));

        Transfers result = underTest.getAll(2L);

        assertThat(result.getTransfers().get().size(), is(2));
        verify(accountRepository, times(1)).findById(2L);
        verify(transactionRepository, times(1))
                .findBySourceAccountOrDestinationAccountOrderByIdAsc(account, account);
        verify(toTransferConverter, times(2)).convert(any());
    }

    @Test
    public void emptyTransfers_nonExistingAccount() {
        BigDecimal balance = new BigDecimal(300.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());
        assertThat(underTest.getAll(2L).getTransfers(), is(Optional.empty()));
    }

    @Test
    public void noTransfers_accountWithNoInitialDepositOrTransactions() {
        BigDecimal balance = new BigDecimal(300.00).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        AccountDTO account = this.getAccount(2L, balance);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));
        List<TransactionDTO> transactions = Collections.emptyList();
        when(transactionRepository.findBySourceAccountOrDestinationAccountOrderByIdAsc(account, account)).thenReturn(transactions);

        Transfers result = underTest.getAll(2L);

        assertThat(result.getTransfers().get().size(), is(0));
        verify(accountRepository, times(1)).findById(2L);
        verify(transactionRepository, times(1))
                .findBySourceAccountOrDestinationAccountOrderByIdAsc(account, account);
        verify(toTransferConverter, never()).convert(any());
    }

    private AccountDTO getAccount(long accountId, BigDecimal balance) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(accountId);
        accountDTO.setBalance(balance);
        return accountDTO;
    }


}