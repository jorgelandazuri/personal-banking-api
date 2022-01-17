package com.jalch.bank.domain.service.transfer;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.TransactionDTO;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.data.repository.TransactionRepository;
import com.jalch.bank.domain.converter.todto.TransferRequestToTransactionDTOConverter;
import com.jalch.bank.domain.model.TopUp;
import com.jalch.bank.domain.validator.ValidationResult;
import com.jalch.bank.rest.request.TransferRequest;
import com.jalch.bank.domain.validator.AmountAndDestinationTransferRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.util.Collections.singletonMap;

@Service
public class TopUpTransferService implements TransferService<TopUp> {

    private final AmountAndDestinationTransferRequestValidator validator;
    private final TransferRequestToTransactionDTOConverter converter;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TopUpTransferService(AmountAndDestinationTransferRequestValidator validator, TransferRequestToTransactionDTOConverter converter,
                                AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.validator = validator;
        this.converter = converter;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public ResponseEntity make(TopUp topUp) {
        TransferRequest topUpRequest = TransferRequest.builder()
                .sourceAccountId(topUp.getAccountId())
                .destinationAccountId(topUp.getAccountId())
                .amount(topUp.getAmount())
                .build();

        ValidationResult validationResult = this.validator.validate(topUpRequest);
        if(validationResult.getResult() == ValidationResult.Result.INVALID)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(validationResult.getValidationMessages().toString());

        Optional<TransactionDTO> transactionDTO = this.converter.convert(topUpRequest);
        if(!transactionDTO.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(singletonMap("message", "Error: account does not exist."));

        transactionRepository.save(transactionDTO.get());
        AccountDTO toppedUpAccount = accountRepository.findById(topUp.getAccountId()).get();
        toppedUpAccount.setBalance(toppedUpAccount.getBalance().add(topUp.getAmount()));
        accountRepository.save(toppedUpAccount);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
