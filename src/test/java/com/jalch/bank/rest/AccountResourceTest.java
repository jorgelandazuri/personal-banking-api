package com.jalch.bank.rest;

import com.jalch.bank.domain.model.AccountAndBalance;
import com.jalch.bank.domain.service.account.PersonalAccountService;
import com.jalch.bank.rest.request.AccountCreationRequest;
import io.cucumber.messages.internal.com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class AccountResourceTest {
    @Mock
    private PersonalAccountService accountService;
    private AccountResource underTest;

    private MockMvc mvc;
    private final Gson gson = new Gson();

    @Before
    public void init() {
        underTest = new AccountResource(accountService);
        this.mvc = MockMvcBuilders.standaloneSetup(underTest).build();
    }

    @Test
    public void validRequestAndSuccessfulAccountCreation() throws Exception {
        AccountCreationRequest validRequest = AccountCreationRequest.builder()
                .nameAndSurname("Jorge Landazuri")
                .documentId("AD09647832")
                .initialDeposit(BigDecimal.valueOf(100))
                .build();
        when(accountService.create(any(AccountCreationRequest.class)))
                .thenReturn(ResponseEntity.status(CREATED).body(singletonMap("message", "Account created!")));

        mvc.perform(post("/accounts/personal")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(validRequest)))
                .andExpect(status().is(201))
                .andExpect(content().json("{\"message\": \"Account created!\"}"));
    }

    @Test
    public void validRequestAndUnsuccessfulAccountCreationDueInternalServerError() throws Exception {
        AccountCreationRequest validRequest = AccountCreationRequest.builder()
                .nameAndSurname("Jorge Landazuri")
                .documentId("AD09647832")
                .initialDeposit(BigDecimal.valueOf(100))
                .build();
        when(accountService.create(any(AccountCreationRequest.class))).thenThrow(RuntimeException.class);

        mvc.perform(post("/accounts/personal")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"error\":\"An error has occurred creating the account\"}"));
    }

    @Test
    public void invalidRequestWithEmptyRequestBody() throws Exception {
        mvc.perform(post("/accounts/personal")
                .contentType(APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"nameAndSurname\": \"must not be blank\"}"));
    }

    @Test
    public void invalidRequestWithNullNameAndSurname() throws Exception {
        AccountCreationRequest invalidRequest = AccountCreationRequest.builder()
                .nameAndSurname(null)
                .documentId("AD09647832")
                .initialDeposit(BigDecimal.valueOf(100))
                .build();
        mvc.perform(post("/accounts/personal")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"nameAndSurname\": \"must not be blank\"}"));
    }

    @Test
    public void invalidRequestWithBlankNameAndSurname() throws Exception {
        AccountCreationRequest invalidRequest = AccountCreationRequest.builder()
                .nameAndSurname("")
                .documentId("AD09647832")
                .initialDeposit(BigDecimal.valueOf(100))
                .build();
        mvc.perform(post("/accounts/personal")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"nameAndSurname\": \"must not be blank\"}"));
    }

    @Test
    public void invalidRequestWithNullDocumentId() throws Exception {
        AccountCreationRequest invalidRequest = AccountCreationRequest.builder()
                .nameAndSurname("Jorge Landazuri")
                .documentId(null)
                .initialDeposit(BigDecimal.valueOf(100))
                .build();
        mvc.perform(post("/accounts/personal")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"documentId\": \"must not be blank\"}"));
    }

    @Test
    public void invalidRequestWithBlankDocumentId() throws Exception {
        AccountCreationRequest invalidRequest = AccountCreationRequest.builder()
                .nameAndSurname("Jorge Landazuri")
                .documentId("")
                .initialDeposit(BigDecimal.valueOf(100))
                .build();
        mvc.perform(post("/accounts/personal")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"documentId\": \"must not be blank\"}"));
    }

    @Test
    public void invalidRequestWithBankTreasuryDocumentId() throws Exception {
        AccountCreationRequest invalidRequest = AccountCreationRequest.builder()
                .nameAndSurname("Jorge Landazuri")
                .documentId("§00_1£")
                .initialDeposit(BigDecimal.valueOf(100))
                .build();
        mvc.perform(post("/accounts/personal")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"documentId\": \"Invalid document id\"}"));
    }

    @Test
    public void invalidRequestWithNegativeInitialDeposit() throws Exception {
        AccountCreationRequest invalidRequest = AccountCreationRequest.builder()
                .nameAndSurname("Jorge Landazuri")
                .documentId("")
                .initialDeposit(BigDecimal.valueOf(-0.01))
                .build();
        mvc.perform(post("/accounts/personal")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"initialDeposit\": \"must be greater than or equal to 0\"}"));
    }

    @Test
    public void validBalanceRequest() throws Exception {

        Optional<AccountAndBalance> accountAndBalance = Optional.of(AccountAndBalance.builder()
                .accountId(2L).balance(BigDecimal.valueOf(100)).build());
        when(accountService.getBalance(2L)).thenReturn(accountAndBalance);

        mvc.perform(MockMvcRequestBuilders
                .get("/accounts/personal/2/balance")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.balance").value(100.00));
    }

    @Test
    public void notFoundAccountIdForBalance() throws Exception {
        when(accountService.getBalance(0L)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                .get("/accounts/personal/0/balance")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
