package com.ing.tech.EasyBank.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Account sender;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Account receiver;

    @Column(nullable = false)
    @Min(0)
    private double amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    public Transaction(Account sender, Account receiver, double amount, LocalDateTime date) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.date = date;
    }
}
