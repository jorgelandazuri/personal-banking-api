package com.jalch.bank.domain.converter.tomodel;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.TransactionDTO;
import com.jalch.bank.domain.model.Transfer;
import com.jalch.bank.domain.model.TransferInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TransferInfoToTransferConverter implements Converter<TransferInfo, Optional<Transfer>> {

    @Value("${bank.treasury.account.id}")
    private String bankTreasuryAccountId;

    @Override
    public Optional<Transfer> convert(TransferInfo transferInfo) {
        TransactionDTO transactionDTO = transferInfo.getTransactionDTO();
        long queriedAccountId = transferInfo.getQueriedAccountId();

        Optional<Transfer.Type> transferType = this.getTransferType(queriedAccountId,
                transactionDTO.getSourceAccount().getId(),
                transactionDTO.getDestinationAccount().getId());

        if(!transferType.isPresent()) return Optional.empty();

        AccountDTO counterPartyAccount = this.getCounterpartyAccount(transferType.get(), transactionDTO);
        return Optional.of(Transfer.builder()
                .id(transactionDTO.getId())
                .amount(transactionDTO.getAmount())
                .type(transferType.get())
                .counterpartyAccountHolder(counterPartyAccount.getAccountHolderDTO().getName())
                .counterpartyAccountId(counterPartyAccount.getId())
                .build());
    }

    private Optional<Transfer.Type> getTransferType(long queriedAccountId, long sourceAccountId, long destinationAccountId) {
        long bankAccountId = Long.parseLong(this.bankTreasuryAccountId);

        if(queriedAccountId == sourceAccountId && queriedAccountId == destinationAccountId)
            return Optional.of(Transfer.Type.DEPOSIT);
        if(queriedAccountId == sourceAccountId && bankAccountId == destinationAccountId)
            return Optional.of(Transfer.Type.BANK_DEBIT);
        if(queriedAccountId == destinationAccountId && bankAccountId == sourceAccountId)
            return Optional.of(Transfer.Type.BANK_CREDIT);
        if(queriedAccountId == sourceAccountId)
            return Optional.of(Transfer.Type.SENT);
        if(queriedAccountId == destinationAccountId)
            return Optional.of(Transfer.Type.RECEIVED);

        return Optional.empty();
    }

    private AccountDTO getCounterpartyAccount(Transfer.Type transferType, TransactionDTO transactionDTO) {
        if(transferType == Transfer.Type.DEPOSIT
                || transferType == Transfer.Type.RECEIVED
                || transferType == Transfer.Type.BANK_CREDIT) {
            return transactionDTO.getSourceAccount();
        } else {
            return transactionDTO.getDestinationAccount();
        }
    }
}
