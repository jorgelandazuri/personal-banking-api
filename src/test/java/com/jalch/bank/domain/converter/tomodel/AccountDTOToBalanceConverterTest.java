package com.jalch.bank.domain.converter.tomodel;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.AccountHolderDTO;
import com.jalch.bank.domain.model.AccountAndBalance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AccountDTOToBalanceConverterTest {

    private AccountDTOToBalanceConverter underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new AccountDTOToBalanceConverter();
    }

    @Test
    public void convert() {
        BigDecimal balance = new BigDecimal(100.63274).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Long accountId = Long.valueOf(1111);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setBalance(balance);
        accountDTO.setAccountHolderDTO(mock(AccountHolderDTO.class));
        accountDTO.setId(accountId);
        AccountAndBalance result = underTest.convert(accountDTO);
        assertThat(result.getAccountId(), is(accountId));
        assertThat(result.getBalance(), is(balance));
    }
}