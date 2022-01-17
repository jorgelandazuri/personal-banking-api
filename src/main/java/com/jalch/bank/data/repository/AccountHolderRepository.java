package com.jalch.bank.data.repository;

import com.jalch.bank.data.dto.AccountHolderDTO;
import org.springframework.data.repository.CrudRepository;

public interface AccountHolderRepository extends CrudRepository<AccountHolderDTO, String> {
}
