package com.jalch.bank.domain.service.account;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.AccountHolderDTO;
import com.jalch.bank.data.repository.AccountHolderRepository;
import com.jalch.bank.data.repository.AccountRepository;
import com.jalch.bank.domain.converter.todto.AccountCreationRequestToAccountDTOConverter;
import com.jalch.bank.domain.converter.todto.AccountCreationRequestToAccountHolderDTOConverter;
import com.jalch.bank.domain.converter.tomodel.AccountDTOToBalanceConverter;
import com.jalch.bank.domain.model.AccountAndBalance;
import com.jalch.bank.domain.model.TopUp;
import com.jalch.bank.domain.service.transfer.TopUpTransferService;
import com.jalch.bank.rest.request.AccountCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.util.Collections.singletonMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Service
public class PersonalAccountService implements AccountService<AccountCreationRequest, Optional<AccountAndBalance>> {

    private final AccountHolderRepository accountHolderRepository;
    private final AccountRepository accountRepository;
    private final AccountCreationRequestToAccountHolderDTOConverter accountHolderConverter;
    private final AccountCreationRequestToAccountDTOConverter accountConverter;
    private final AccountDTOToBalanceConverter accountDTOToBalanceConverter;
    private final TopUpTransferService topUpTransferService;

    @Autowired
    public PersonalAccountService(AccountHolderRepository accountHolderRepository,
                                  AccountRepository accountRepository,
                                  AccountCreationRequestToAccountHolderDTOConverter accountHolderConverter,
                                  AccountCreationRequestToAccountDTOConverter accountConverter,
                                  AccountDTOToBalanceConverter accountDTOToBalanceConverter,
                                  TopUpTransferService topUpTransferService) {

        this.accountHolderRepository = accountHolderRepository;
        this.accountRepository = accountRepository;
        this.accountHolderConverter = accountHolderConverter;
        this.accountConverter = accountConverter;
        this.accountDTOToBalanceConverter = accountDTOToBalanceConverter;
        this.topUpTransferService = topUpTransferService;
    }

    @Override
    @Transactional
    public ResponseEntity create(AccountCreationRequest accountCreationRequest) {
        Optional<AccountHolderDTO> existingAccountHolder = accountHolderRepository.findById(accountCreationRequest.getDocumentId());
        if (!existingAccountHolder.isPresent())
            accountHolderRepository.save(accountHolderConverter.convert(accountCreationRequest));

        AccountDTO savedAccount = accountRepository.save(accountConverter.convert(accountCreationRequest));
        TopUp topUp = TopUp.builder()
                .amount(accountCreationRequest.getInitialDeposit())
                .accountId(savedAccount.getId())
                .build();

        ResponseEntity topUpResponse = topUpTransferService.make(topUp);
        return topUpResponse.getStatusCode() == OK
                ? ResponseEntity.status(CREATED).body(singletonMap("message", "Account created!"))
                : topUpResponse;
    }

    @Override
    public Optional<AccountAndBalance> getBalance(long accountId) {
        Optional<AccountDTO> account = accountRepository.findById(accountId);
        return account.map(accountDTOToBalanceConverter::convert);
    }

}
