package com.ing.tech.EasyBank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.tech.EasyBank.dto.ExchangeDtoInput;
import com.ing.tech.EasyBank.dto.ExchangeDtoOutput;
import com.ing.tech.EasyBank.entity.Account;
import com.ing.tech.EasyBank.entity.Exchange;
import com.ing.tech.EasyBank.entity.User;
import com.ing.tech.EasyBank.exception.*;
import com.ing.tech.EasyBank.repository.AccountRepository;
import com.ing.tech.EasyBank.repository.ExchangeRepository;
import com.ing.tech.EasyBank.repository.TransactionRepository;
import com.ing.tech.EasyBank.repository.UserRepository;
import com.ing.tech.EasyBank.service.helpers.DateFormatter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class ExchangeService {
    private final AccountRepository accountRepository;
    private final ExchangeRepository exchangeRepository;


    @Autowired
    private RestTemplate restTemplate;


    public JsonNode connectToExchangeApi(String baseCurrency, String finalCurrency, Double amount){

        String url = "https://api.exchangerate.host/convert";
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("from", "{fromCurrency}")
                .queryParam("to", "{toCurrency}")
                .queryParam("amount", "{amount}")
                .build()
                .toUriString();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fromCurrency", baseCurrency);
        parameters.put("toCurrency", finalCurrency);
        parameters.put("amount", amount.toString());
        ResponseEntity<String> response = restTemplate.getForEntity(
                urlTemplate,
                String.class,
                parameters
        );


        JsonNode parent;
        try {
            parent = new ObjectMapper().readTree(response.getBody());
            boolean success = parent.get("success").asBoolean();
            log.info(String.valueOf(success));
            if (!success){
                throw new InvalidCurrencyExchange();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new InvalidCurrencyExchange();
        }
        return parent;
    }

    public Double getCurrencyExchangeRate(JsonNode parent){
        return parent.get("info").get("rate").asDouble();
    }

    public Double getCurrencyExchangeResult(JsonNode parent){
        return parent.get("result").asDouble();
    }


    @Transactional
    public ExchangeDtoOutput exchange(ExchangeDtoInput exchangeDtoInput) {
        double amount = exchangeDtoInput.getAmount();
        Account fromAccount = accountRepository
                .getAccountByAccountNumber(exchangeDtoInput.getFromAccountNumber())
                .orElseThrow(AccountNotFound::new);
        Account toAccount = accountRepository
                .getAccountByAccountNumber(exchangeDtoInput.getToAccountNumber())
                .orElseThrow(AccountNotFound::new);

        if( !fromAccount.getUser().getUsername().equals(toAccount.getUser().getUsername()) )
            throw new ExchangeBetweenDifferentUsers("Cannot use exchange functionality between different users !");

        String fromCurrency = fromAccount.getCurrency();
        String toCurrency = toAccount.getCurrency();
        if(fromCurrency.equals(toCurrency)){
            throw new IdenticalCurrencies();
        }

        // check balance - amount
        if (fromAccount.getBalance() < amount){
            throw new InsufficientFunds();
        }
        // call method to retrieve result for these currencies (by getting result, else get rate)
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        log.info("INITIAL AMOUNT: " + amount);

        JsonNode parent = connectToExchangeApi(fromCurrency, toCurrency, amount);
        double exchangeRate = getCurrencyExchangeRate(parent);
        double convertedAmount = getCurrencyExchangeResult(parent);

        log.info("CONVERTED AMOUNT: " + convertedAmount);
        toAccount.setBalance(toAccount.getBalance() + convertedAmount);

        // save to database
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);


        //String finalDate = DateFormatter.getRomanianDateFormatString();

        Exchange exchange = new Exchange(fromAccount, toAccount, amount, convertedAmount,
                exchangeRate, LocalDateTime.now());
        exchangeRepository.save(exchange);
        return new ExchangeDtoOutput(fromCurrency, toCurrency, amount, convertedAmount, exchangeRate,
                LocalDateTime.now().toString());
    }
}

















