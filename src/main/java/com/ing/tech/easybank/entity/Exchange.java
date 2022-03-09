package com.ing.tech.EasyBank.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "exchange")
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Account senderAcc;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Account receiverAcc;

    @Column(name = "sent_amount", nullable = false)
    @Min(0)
    private double sentAmount;

    @Column(name = "received_amount", nullable = false)
    @Min(0)
    private double receivedAmount;

    @Column(name = "exchange_rate", nullable = false)
    @Min(0)
    private double exchangeRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date", nullable = false)
    private LocalDateTime date;


    public Exchange(Account senderAcc, Account receiverAcc, double sentAmount, double receivedAmount, double exchangeRate, LocalDateTime date) {
        this.senderAcc = senderAcc;
        this.receiverAcc = receiverAcc;
        this.sentAmount = sentAmount;
        this.receivedAmount = receivedAmount;
        this.exchangeRate = exchangeRate;
        this.date = date;
    }
}

