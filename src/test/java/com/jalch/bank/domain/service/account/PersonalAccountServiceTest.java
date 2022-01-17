package com.jalch.bank.domain.service.account;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.AccountHolderDTO;
import com.jalch.bank.data.repository.AccountHolderRepository;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.domain.converter.todto.AccountCreationRequestToAccountDTOConverter;
import com.jalch.bank.domain.converter.todto.AccountCreationRequestToAccountHolderDTOConverter;
import com.jalch.bank.domain.converter.tomodel.AccountDTOToBalanceConverter;
import com.jalch.bank.domain.model.AccountAndBalance;
import com.jalch.bank.domain.model.TopUp;
import com.jalch.bank.domain.service.transfer.TopUpTransferService;
import com.jalch.bank.rest.request.AccountCreationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonalAccountServiceTest {

    @Mock
    private AccountHolderRepository accountHolderRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountCreationRequestToAccountHolderDTOConverter accountHolderConverter;
    @Mock
    private AccountCreationRequestToAccountDTOConverter accountConverter;
    @Mock
    private AccountDTOToBalanceConverter accountDTOToBalanceConverter;
    @Mock
    private TopUpTransferService topUpTransferService;
    @Mock
    private AccountHolderDTO accountHolder;
    @Mock
    private AccountDTO account;
    @Mock
    private AccountAndBalance accountAndBalance;
    @Mock
    private AccountCreationRequest request;

    private PersonalAccountService underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new PersonalAccountService(accountHolderRepository, accountRepository,
                accountHolderConverter, accountConverter, accountDTOToBalanceConverter,
                topUpTransferService);
    }

    @Test
    public void createAccountSuccess_existingAccountHolder() {
        when(accountConverter.convert(request)).thenReturn(account);
        when(accountRepository.save(any())).thenReturn(this.getSavedAccount());
        when(topUpTransferService.make(any())).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        assertSame(HttpStatus.CREATED, underTest.create(request).getStatusCode());

        verify(accountHolderRepository, never()).save(accountHolder);
        verify(accountRepository, times(1)).save(account);
        verify(topUpTransferService, times(1)).make(any(TopUp.class));
    }

    @Test
    public void createAccountSuccess_nonExistingAccountHolder() {
        when(accountConverter.convert(request)).thenReturn(account);
        when(accountHolderConverter.convert(request)).thenReturn(accountHolder);
        when(accountRepository.save(any())).thenReturn(this.getSavedAccount());
        when(topUpTransferService.make(any())).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        assertSame(HttpStatus.CREATED, underTest.create(request).getStatusCode());

        verify(accountHolderRepository, times(1)).save(accountHolder);
        verify(accountRepository, times(1)).save(account);
        verify(topUpTransferService, times(1)).make(any(TopUp.class));
    }

    @Test
    public void getBalance_existingAccountId() {
        long accountId = 1111L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountDTOToBalanceConverter.convert(account)).thenReturn(accountAndBalance);
        Optional<AccountAndBalance> balance = underTest.getBalance(accountId);
        assertThat(balance, is(Optional.of(accountAndBalance)));
    }

    @Test
    public void getBalance_nonExistingAccountId() {
        long accountId = 1111L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        Optional<AccountAndBalance> balance = underTest.getBalance(accountId);
        assertThat(balance, is(Optional.empty()));
    }

    private AccountDTO getSavedAccount() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(2L);
        return accountDTO;
    }
}