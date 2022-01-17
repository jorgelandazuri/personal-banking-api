package com.jalch.bank.domain.converter.tomodel;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.domain.model.AccountAndBalance;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountDTOToBalanceConverter implements Converter<AccountDTO, AccountAndBalance>{

    @Override
    public AccountAndBalance convert(AccountDTO accountDTO) {
        return AccountAndBalance.builder()
                .accountId(accountDTO.getId())
                .balance(accountDTO.getBalance()).build();
    }
}
