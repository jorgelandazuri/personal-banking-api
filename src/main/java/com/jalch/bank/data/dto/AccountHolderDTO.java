package com.jalch.bank.data.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ACCOUNT_HOLDERS", schema = "bank")
@Getter
@Setter
public class AccountHolderDTO {
    @Column(name = "ID",insertable = false, updatable = false, columnDefinition="serial")
    private Long id;

    @Id
    @Column(name = "DOCUMENT_ID", unique = true, length = 30)
    private String documentId;

    @Column(name = "NAME")
    private String name;
}
