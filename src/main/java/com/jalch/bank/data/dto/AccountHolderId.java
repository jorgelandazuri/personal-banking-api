package com.jalch.bank.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class AccountHolderId implements Serializable {
    private Long id;
    private String documentId;
}
