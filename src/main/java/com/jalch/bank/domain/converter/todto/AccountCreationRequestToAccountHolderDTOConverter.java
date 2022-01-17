package com.jalch.bank.domain.converter.todto;

import com.jalch.bank.data.dto.AccountHolderDTO;
import com.jalch.bank.rest.request.AccountCreationRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountCreationRequestToAccountHolderDTOConverter
        implements Converter<AccountCreationRequest, AccountHolderDTO>{

    @Override
    public AccountHolderDTO convert(AccountCreationRequest accountCreationRequest) throws IllegalArgumentException {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        accountHolderDTO.setDocumentId(accountCreationRequest.getDocumentId());
        accountHolderDTO.setName(accountCreationRequest.getNameAndSurname());
        return accountHolderDTO;
    }
}
