package com.jalch.bank.data.repository;

import com.jalch.bank.data.dto.AccountDTO;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<AccountDTO, Long> {
}
