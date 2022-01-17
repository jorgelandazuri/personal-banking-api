package com.jalch.bank.domain.service.transfer;

import com.jalch.bank.domain.model.Transfers;

interface TransfersService {
    Transfers getAll(long accountId);
}
