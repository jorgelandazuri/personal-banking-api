package com.jalch.bank.domain.converter.todto;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.rest.request.AccountCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class
AccountCreationRequestToAccountDTOConverter implements Converter<AccountCreationRequest, AccountDTO>{

    private AccountCreationRequestToAccountHolderDTOConverter converterHelper;

    @Autowired
    public AccountCreationRequestToAccountDTOConverter(AccountCreationRequestToAccountHolderDTOConverter converterHelper) {
        this.converterHelper = converterHelper;
    }

    @Override
    public AccountDTO convert(AccountCreationRequest accountCreationRequest) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountHolderDTO(converterHelper.convert(accountCreationRequest));
        accountDTO.setBalance(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        return accountDTO;
    }
}
