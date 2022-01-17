package com.jalch.bank.rest;

import com.jalch.bank.domain.model.Transfer;
import com.jalch.bank.domain.model.Transfers;
import com.jalch.bank.domain.service.transfer.PersonalAccountTransfersService;
import com.jalch.bank.rest.request.TransferRequest;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.TEN;
import static java.util.Collections.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransferResourceTest {

    @Mock
    private PersonalAccountTransfersService accountTransfersService;

    private TransactionResource underTest;

    private MockMvc mvc;
    private final Gson gson = new Gson();

    @Before
    public void init(){
        underTest = new TransactionResource(accountTransfersService);
        this.mvc = MockMvcBuilders.standaloneSetup(underTest).build();
    }

    @Test
    public void transferResultFromService() throws Exception {
        when(accountTransfersService.make(any(TransferRequest.class)))
                .thenReturn(ResponseEntity.status(CREATED).body(singletonMap("message", "Transfer completed.")));
        mvc.perform(post("/transactions/transfers")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(TransferRequest.builder().amount(TEN).build())))
                .andExpect(status().is(CREATED.value()))
                .andExpect(content().json("{\"message\": \"Transfer completed.\"}"));
    }

    @Test
    public void transferWithInternalServerErrorFromService() throws Exception {
        when(accountTransfersService.make(any(TransferRequest.class))).thenThrow(RuntimeException.class);

        mvc.perform(post("/transactions/transfers")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(TransferRequest.builder().amount(TEN).build())))
                .andExpect(status().is(INTERNAL_SERVER_ERROR.value()))
                .andExpect(content().json("{\"error\": \"An error has occurred making the transfer\"}"));
    }

    @Test
    public void badRequestDueToNullAmount() throws Exception {
        when(accountTransfersService.make(any(TransferRequest.class))).thenThrow(RuntimeException.class);

        mvc.perform(post("/transactions/transfers")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(TransferRequest.builder().amount(null).build())))
                .andExpect(status().is(BAD_REQUEST.value()))
                .andExpect(content().json("{\"amount\": \"must not be null\"}"));
    }

    @Test
    public void getTransfersFromExistingAccountSuccess() throws Exception {
        Transfer aTransfer = Transfer.builder().type(Transfer.Type.SENT)
                .id(12345L)
                .counterpartyAccountId(3L).counterpartyAccountHolder("Jorge Landazuri")
                .amount(TEN).build();

        List<Transfer> transferList = singletonList(aTransfer);
        Transfers transfers = Transfers.builder()
                .transfers(Optional.of(transferList)).build();
        when(accountTransfersService.getAll(2L)).thenReturn(transfers);

        mvc.perform(
                MockMvcRequestBuilders
                        .get("/transactions/2/transfers")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transfers[0].id").value(12345))
                .andExpect(jsonPath("$.transfers[0].type").value("SENT"))
                .andExpect(jsonPath("$.transfers[0].counterpartyAccountId").value(3))
                .andExpect(jsonPath("$.transfers[0].counterpartyAccountHolder").value("Jorge Landazuri"))
                .andExpect(jsonPath("$.transfers[0].amount").value(10.00));

    }

    @Test
    public void getTransfersFromNotExistingAccountBadRequest() throws Exception {
        Transfers transfers = Transfers.builder().transfers(Optional.empty()).build();
        when(accountTransfersService.getAll(-2L)).thenReturn(transfers);

        mvc.perform(
                MockMvcRequestBuilders
                        .get("/transactions/-2/transfers")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
