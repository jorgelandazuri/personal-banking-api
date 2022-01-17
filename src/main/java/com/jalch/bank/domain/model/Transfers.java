package com.jalch.bank.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Builder
@Getter
public class Transfers {
    private final Optional<List<Transfer>> transfers;
}
