package com.jalch.bank.domain.service.transfer;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.TransactionDTO;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.data.repository.TransactionRepository;
import com.jalch.bank.domain.converter.todto.TransferRequestToTransactionDTOConverter;
import com.jalch.bank.domain.converter.tomodel.TransferInfoToTransferConverter;
import com.jalch.bank.domain.model.Transfer;
import com.jalch.bank.domain.model.TransferInfo;
import com.jalch.bank.domain.model.Transfers;
import com.jalch.bank.domain.validator.ValidationResult;
import com.jalch.bank.rest.request.TransferRequest;
import com.jalch.bank.domain.validator.CappedAmountTransferRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

@Service
public class PersonalAccountTransfersService implements TransferService<TransferRequest>, TransfersService {

    private final CappedAmountTransferRequestValidator validator;
    private final TransferRequestToTransactionDTOConverter converter;
    private final TransferInfoToTransferConverter toTransferConverter;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public PersonalAccountTransfersService(CappedAmountTransferRequestValidator validator,
                                           TransferRequestToTransactionDTOConverter toDTOConverter,
                                           TransferInfoToTransferConverter toTransferConverter,
                                           AccountRepository accountRepository,
                                           TransactionRepository transactionRepository) {
        this.validator = validator;
        this.converter = toDTOConverter;
        this.toTransferConverter = toTransferConverter;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public ResponseEntity make(TransferRequest transferRequest) {
        ValidationResult validation = this.validator.validate(transferRequest);
        if (validation.getResult() == ValidationResult.Result.INVALID)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(validation.getValidationMessages());

        Optional<TransactionDTO> transactionDTO = this.converter.convert(transferRequest);
        if (!transactionDTO.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(singletonMap("message", "Error: source or destination account do not exist."));

        transactionRepository.save(transactionDTO.get());

        AccountDTO sourceAccount = accountRepository.findById(transferRequest.getSourceAccountId()).get();
        AccountDTO destinationAccount = accountRepository.findById(transferRequest.getDestinationAccountId()).get();
        BigDecimal sourceBalance = sourceAccount.getBalance();
        BigDecimal destinationBalance = destinationAccount.getBalance();
        sourceAccount.setBalance(sourceBalance.subtract(transferRequest.getAmount()));
        destinationAccount.setBalance(destinationBalance.add(transferRequest.getAmount()));
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(singletonMap("message", "Transfer completed."));
    }

    @Override
    public Transfers getAll(long accountId) {
        Optional<AccountDTO> accountDTO = accountRepository.findById(accountId);
        if (!accountDTO.isPresent()) return Transfers.builder()
                .transfers(Optional.empty()).build();

        List<TransactionDTO> transactions = transactionRepository.findBySourceAccountOrDestinationAccountOrderByIdAsc(
                accountDTO.get(), accountDTO.get());
        List<Transfer> transfers = transactions.stream()
                .map(transaction -> this.toTransferConverter.convert(TransferInfo.builder()
                        .queriedAccountId(accountId)
                        .transactionDTO(transaction).build()))
                .filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());

        return Transfers.builder().transfers(Optional.of(transfers)).build();
    }

}
