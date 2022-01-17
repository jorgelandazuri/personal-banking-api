package com.jalch.bank.domain.converter.todto;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.AccountHolderDTO;
import com.jalch.bank.rest.request.AccountCreationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AccountCreationRequestToAccountDTOConverterTest {

    @Mock
    private AccountCreationRequestToAccountHolderDTOConverter converterHelper;

    private AccountCreationRequestToAccountDTOConverter underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new AccountCreationRequestToAccountDTOConverter(converterHelper);
    }

    @Test
    public void convert() {
        AccountCreationRequest accountCreationRequest = AccountCreationRequest.builder()
                .documentId("::doc_id::")
                .nameAndSurname("::a_name::")
                .build();
        AccountHolderDTO expectedAccountHolder = mock(AccountHolderDTO.class);
        Mockito.when(converterHelper.convert(accountCreationRequest)).thenReturn(expectedAccountHolder);
        AccountDTO result = underTest.convert(accountCreationRequest);

        assertNull(result.getId());
        assertThat(result.getAccountHolderDTO(), is(expectedAccountHolder));
        assertThat(result.getBalance(), is(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
    }
}