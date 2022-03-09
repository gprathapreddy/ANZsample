package com.ing.tech.EasyBank.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    @Min(0)
    private Double balance;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Transaction> outcomeTransactions;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Transaction> incomeTransactions;

    @OneToMany(mappedBy = "senderAcc", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Exchange> outcomeExchangeTransactions;

    @OneToMany(mappedBy = "receiverAcc", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Exchange> incomeExchangeTransactions;


    public Account(User user, String currency, Double balance ) {
        this.currency = currency;
        this.balance = balance;
        this.user = user;
    }
}































