package com.jalch.bank.data.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTIONS", schema = "bank")
@Getter
@Setter
public class TransactionDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name= "SOURCE", referencedColumnName = "ID")
    private AccountDTO sourceAccount;

    @ManyToOne
    @JoinColumn(name= "DESTINATION", referencedColumnName = "ID")
    private AccountDTO destinationAccount;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "DATE_TIME")
    private LocalDateTime dateTime;

}
