package com.jalch.bank.domain.converter.todto;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.TransactionDTO;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.rest.request.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TransferRequestToTransactionDTOConverter implements Converter<TransferRequest, Optional<TransactionDTO>>{

    private AccountRepository accountRepository;

    @Autowired
    public TransferRequestToTransactionDTOConverter(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<TransactionDTO> convert(TransferRequest transferRequest) {
        Optional<AccountDTO> sourceAccount = accountRepository.findById(transferRequest.getSourceAccountId());
        Optional<AccountDTO> destinationAccount = accountRepository.findById(transferRequest.getDestinationAccountId());

        if(!sourceAccount.isPresent() || !destinationAccount.isPresent()) return Optional.empty();

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSourceAccount(sourceAccount.get());
        transactionDTO.setDestinationAccount(destinationAccount.get());
        transactionDTO.setDateTime(LocalDateTime.now());
        transactionDTO.setAmount(transferRequest.getAmount());
        return Optional.of(transactionDTO);
    }
}
