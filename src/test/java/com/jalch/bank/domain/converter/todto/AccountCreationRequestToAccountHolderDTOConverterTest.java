package com.jalch.bank.domain.converter.todto;

import com.jalch.bank.data.dto.AccountHolderDTO;
import com.jalch.bank.rest.request.AccountCreationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class AccountCreationRequestToAccountHolderDTOConverterTest {

    private AccountCreationRequestToAccountHolderDTOConverter underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new AccountCreationRequestToAccountHolderDTOConverter();
    }

    @Test
    public void convert() {
        AccountCreationRequest accountCreationRequest = AccountCreationRequest.builder()
                .documentId("::doc_id::")
                .nameAndSurname("::a_name::")
                .initialDeposit(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_EVEN))
                .build();
        AccountHolderDTO result = underTest.convert(accountCreationRequest);

        assertNull(result.getId());
        assertThat(result.getDocumentId(), is("::doc_id::"));
        assertThat(result.getName(), is("::a_name::"));
    }
}