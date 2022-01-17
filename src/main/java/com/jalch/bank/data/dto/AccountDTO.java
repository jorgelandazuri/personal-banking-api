package com.jalch.bank.data.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ACCOUNTS", schema = "bank")
@Getter
@Setter
public class AccountDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_HOLDER_ID", referencedColumnName = "DOCUMENT_ID")
    private AccountHolderDTO accountHolderDTO;

    @Column(name = "BALANCE")
    private BigDecimal balance;

}
