package com.ing.tech.EasyBank.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User sender;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User receiver;

    @Column(name="account_number", nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    @Min(0)
    private double amount;

    @Column(nullable = false)   // TODO validate currency
    private String currency;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column
    private String message;

    public Request(User sender, User receiver, String accountNumber, double amount,
                   String currency, LocalDateTime date, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.message = message;
    }
}













