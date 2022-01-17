package com.jalch.bank.domain.converter.tomodel;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.AccountHolderDTO;
import com.jalch.bank.data.dto.TransactionDTO;
import com.jalch.bank.domain.model.Transfer;
import com.jalch.bank.domain.model.TransferInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransferInfoToTransferConverterTest {


    private TransferInfoToTransferConverter underTest;
    @Before
    public void setUp() {
        underTest = new TransferInfoToTransferConverter();
        ReflectionTestUtils.setField(underTest, "bankTreasuryAccountId", "1");
    }

    @Test
    public void notPresentTransfer() {
        TransactionDTO transactionDTO = this.getTransactionDTO(2, "::two::", 2, "::two::");
        TransferInfo transferInfo = TransferInfo.builder().queriedAccountId(4).transactionDTO(transactionDTO).build();
        assertFalse(underTest.convert(transferInfo).isPresent());
    }

    @Test
    public void presentTransfer_TopUp() {
        TransactionDTO transactionDTO = this.getTransactionDTO(2, "::two::", 2, "::two::");
        TransferInfo transferInfo = TransferInfo.builder().queriedAccountId(2).transactionDTO(transactionDTO).build();
        Transfer result = underTest.convert(transferInfo).get();
        assertThat(result.getId(), is(1L));
        assertThat(result.getAmount(), is(new BigDecimal( 100)));
        assertThat(result.getType(), is(Transfer.Type.DEPOSIT));
        assertThat(result.getCounterpartyAccountHolder(), is("::two::"));
        assertThat(result.getCounterpartyAccountId(), is(2L));
    }

    @Test
    public void presentTransfer_received() {
        TransactionDTO transactionDTO = this.getTransactionDTO(3, "::three::", 2, "::two::");
        TransferInfo transferInfo = TransferInfo.builder().queriedAccountId(2).transactionDTO(transactionDTO).build();
        Transfer result = underTest.convert(transferInfo).get();
        assertThat(result.getId(), is(1L));
        assertThat(result.getAmount(), is(new BigDecimal( 100)));
        assertThat(result.getType(), is(Transfer.Type.RECEIVED));
        assertThat(result.getCounterpartyAccountHolder(), is("::three::"));
        assertThat(result.getCounterpartyAccountId(), is(3L));
    }

    @Test
    public void presentTransfer_sent() {
        TransactionDTO transactionDTO = this.getTransactionDTO(2, "::two::", 3, "::three::");
        TransferInfo transferInfo = TransferInfo.builder().queriedAccountId(2).transactionDTO(transactionDTO).build();
        Transfer result = underTest.convert(transferInfo).get();
        assertThat(result.getId(), is(1L));
        assertThat(result.getAmount(), is(new BigDecimal( 100)));
        assertThat(result.getType(), is(Transfer.Type.SENT));
        assertThat(result.getCounterpartyAccountHolder(), is("::three::"));
        assertThat(result.getCounterpartyAccountId(), is(3L));
    }

    @Test
    public void presentTransfer_bankCredit() {
        TransactionDTO transactionDTO = this.getTransactionDTO(1, "::bank::", 2, "::two::");
        TransferInfo transferInfo = TransferInfo.builder().queriedAccountId(2).transactionDTO(transactionDTO).build();
        Transfer result = underTest.convert(transferInfo).get();
        assertThat(result.getId(), is(1L));
        assertThat(result.getAmount(), is(new BigDecimal( 100)));
        assertThat(result.getType(), is(Transfer.Type.BANK_CREDIT));
        assertThat(result.getCounterpartyAccountHolder(), is("::bank::"));
        assertThat(result.getCounterpartyAccountId(), is(1L));
    }

    @Test
    public void presentTransfer_bankDebit() {
        TransactionDTO transactionDTO = this.getTransactionDTO(2, "::two::", 1, "::bank::");
        TransferInfo transferInfo = TransferInfo.builder().queriedAccountId(2).transactionDTO(transactionDTO).build();
        Transfer result = underTest.convert(transferInfo).get();
        assertThat(result.getId(), is(1L));
        assertThat(result.getAmount(), is(new BigDecimal( 100)));
        assertThat(result.getType(), is(Transfer.Type.BANK_DEBIT));
        assertThat(result.getCounterpartyAccountHolder(), is("::bank::"));
        assertThat(result.getCounterpartyAccountId(), is(1L));
    }

    private TransactionDTO getTransactionDTO(long sourceAccountId, String sourceAccountHolder,
                                             long destinationAccountId, String destinationAccountHolder){
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(1L);
        transactionDTO.setAmount(new BigDecimal( 100));
        transactionDTO.setDateTime(LocalDateTime.now());

        AccountHolderDTO sourceAccountHolderDTO = new AccountHolderDTO();
        sourceAccountHolderDTO.setName(sourceAccountHolder);
        AccountDTO sourceAccount = new AccountDTO();
        sourceAccount.setId(sourceAccountId);
        sourceAccount.setAccountHolderDTO(sourceAccountHolderDTO);

        AccountHolderDTO destinationAccountHolderDTO = new AccountHolderDTO();
        destinationAccountHolderDTO.setName(destinationAccountHolder);
        AccountDTO destinationAccount = new AccountDTO();
        destinationAccount.setId(destinationAccountId);
        destinationAccount.setAccountHolderDTO(destinationAccountHolderDTO);

        transactionDTO.setSourceAccount(sourceAccount);
        transactionDTO.setDestinationAccount(destinationAccount);

        return transactionDTO;
    }
}