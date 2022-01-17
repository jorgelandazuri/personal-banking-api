package com.jalch.bank.domain.service.data;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.AccountHolderDTO;
import com.jalch.bank.data.repository.AccountHolderRepository;
import com.jalch.bank.data.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TestDataInitializer {

    @Value("${bank.treasury.account.id}")
    private String bankTreasuryAccountId;

    private AccountHolderRepository accountHolderRepository;
    private AccountRepository accountRepository;

    @Autowired
    public TestDataInitializer(AccountHolderRepository accountHolderRepository, AccountRepository accountRepository) {
        this.accountHolderRepository = accountHolderRepository;
        this.accountRepository = accountRepository;
    }

    public void init(){
        AccountHolderDTO bankTreasury = new AccountHolderDTO();
        bankTreasury.setName("Bank Treasury");
        bankTreasury.setDocumentId("§00_1£");
        bankTreasury.setId(Long.parseLong(this.bankTreasuryAccountId));
        accountHolderRepository.save(bankTreasury);
        AccountHolderDTO arisha = new AccountHolderDTO();
        arisha.setName("Arisha Barron");
        arisha.setDocumentId("12-3");
        accountHolderRepository.save(arisha);
        AccountHolderDTO branden = new AccountHolderDTO();
        branden.setName("Branden Gibson");
        branden.setDocumentId("45-6");
        accountHolderRepository.save(branden);
        AccountHolderDTO rhonda = new AccountHolderDTO();
        rhonda.setName("Rhonda Church");
        rhonda.setDocumentId("78-9");
        accountHolderRepository.save(rhonda);
        AccountHolderDTO georgina = new AccountHolderDTO();
        georgina.setName("Georgina Hazel");
        georgina.setDocumentId("10-AB");
        accountHolderRepository.save(georgina);

        AccountDTO bankTreasuryAccount = new AccountDTO();
        BigDecimal bankTreasuryBalance = new BigDecimal(1000000000.647835).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        bankTreasuryAccount.setBalance(bankTreasuryBalance);
        bankTreasuryAccount.setAccountHolderDTO(bankTreasury);
        accountRepository.save(bankTreasuryAccount);

    }
}
