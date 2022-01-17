package com.jalch.bank.data.repository;

import com.jalch.bank.data.dto.AccountDTO;
import com.jalch.bank.data.dto.TransactionDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends CrudRepository<TransactionDTO, Long> {

    List<TransactionDTO> findBySourceAccountOrDestinationAccountOrderByIdAsc(@Param("sourceAccount") AccountDTO sourceAccount,
                                                                             @Param("destinationAccount") AccountDTO destinationAccount);
}
