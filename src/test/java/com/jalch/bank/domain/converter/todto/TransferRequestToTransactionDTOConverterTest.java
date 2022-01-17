package com.jalch.bank.domain.converter.todto;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.TransactionDTO;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.rest.request.TransferRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransferRequestToTransactionDTOConverterTest {

    @Mock
    private AccountRepository accountRepository;

    private TransferRequestToTransactionDTOConverter underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new TransferRequestToTransactionDTOConverter(accountRepository);
    }

    @Test
    public void transferNotPresent_nonExistingSourceAccount() {
        when(accountRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertFalse(underTest.convert(mock(TransferRequest.class)).isPresent());
    }

    @Test
    public void transferNotPresent_nonExistingDestinationAccount() {
        assertFalse(underTest.convert(mock(TransferRequest.class)).isPresent());
    }

    @Test
    public void transferPresent() {
        LocalDateTime transferTime = LocalDateTime.now();
        BigDecimal amount = new BigDecimal(100);
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .sourceAccountId(2L)
                .destinationAccountId(3L)
                .build();
        AccountDTO sourceAccount = mock(AccountDTO.class);
        AccountDTO destinationAccount = mock(AccountDTO.class);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(3L)).thenReturn(Optional.of(destinationAccount));

        TransactionDTO result = underTest.convert(transferRequest).get();
        assertThat(result.getAmount(), is(amount));
        assertThat(result.getSourceAccount(), is(sourceAccount));
        assertThat(result.getDestinationAccount(), is(destinationAccount));
        assertThat(result.getDateTime().getYear(), is(transferTime.getYear()));
        assertThat(result.getDateTime().getMonth(), is(transferTime.getMonth()));
        assertThat(result.getDateTime().getDayOfMonth(), is(transferTime.getDayOfMonth()));
        assertThat(result.getDateTime().getHour(), is(transferTime.getHour()));
        assertThat(result.getDateTime().getMinute(), is(transferTime.getMinute()));
        assertThat(result.getDateTime().getSecond(), is(transferTime.getSecond()));
    }
}