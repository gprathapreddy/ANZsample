package com.ing.tech.EasyBank.service;

import com.ing.tech.EasyBank.dto.MonthlySummaryDto;
import com.ing.tech.EasyBank.dto.TransactionDto;
import com.ing.tech.EasyBank.dto.TransactionTypeDto;
import com.ing.tech.EasyBank.entity.Account;
import com.ing.tech.EasyBank.entity.Transaction;
import com.ing.tech.EasyBank.entity.User;
import com.ing.tech.EasyBank.enums.TransactionTypeEnum;
import com.ing.tech.EasyBank.exception.*;
import com.ing.tech.EasyBank.repository.AccountRepository;
import com.ing.tech.EasyBank.repository.TransactionRepository;
import com.ing.tech.EasyBank.repository.UserRepository;
import com.ing.tech.EasyBank.service.helpers.DateFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;


@AllArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionDto transfer(TransactionDto transactionDto) {

        String senderAccountNumber = transactionDto.getFromAccountNumber();
        String receiverAccountNumber = transactionDto.getToAccountNumber();

        Account sender = accountRepository.getAccountByAccountNumber(senderAccountNumber).orElseThrow(AccountNotFound::new);
        Account receiver = accountRepository.getAccountByAccountNumber(receiverAccountNumber).orElseThrow(AccountNotFound::new);
        Double amount = transactionDto.getAmount();

        if (!sender.getCurrency().equals(receiver.getCurrency()))
            throw new AccountsHaveDifferentCurrencies();

        if (sender.getBalance() < amount)
            throw new InsufficientFunds();

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        // TODO - move this, checkCurrency and checkAccountNumber to another class
        // date time formatter - change pattern - Either like this or with JsonFormat ?!
        //String finalDate = DateFormatter.getRomanianDateFormatString();

        Transaction transaction = new Transaction(sender, receiver, amount, LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionToTransactionDto(savedTransaction);

    }

    public List<TransactionTypeDto> getAllTransactions(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        List<TransactionTypeDto> allTransactionTypeDto = new ArrayList<>();

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        user.getAccounts().stream()
                .map(Account::getIncomeTransactions)
                .forEach(x -> x.stream()
                        // this next filter will filter the transactions between accounts with the same user
                        .filter(t -> !t.getSender().getUser().getUsername().equals(t.getReceiver().getUser().getUsername()))
                        .forEach(trans -> allTransactionTypeDto.add(new TransactionTypeDto(
                                trans.getDate(),
                                trans.getId(),
                                trans.getSender().getUser().getFirstName() + " " + trans.getSender().getUser().getLastName(),
                                //trans.getReceiver().getUser().getUsername(),
                                trans.getAmount(),
                                trans.getReceiver().getCurrency(),
                                TransactionTypeEnum.INCOME
                        ))));
        user.getAccounts().stream()
                .map(Account::getOutcomeTransactions)
                .forEach(x -> x.stream()
                        // this next filter will filter the transactions between accounts with the same user
                        .filter(t -> !t.getSender().getUser().getUsername().equals(t.getReceiver().getUser().getUsername()))
                        .forEach(trans -> allTransactionTypeDto.add(new TransactionTypeDto(
                                trans.getDate(),
                                trans.getId(),
//                                trans.getSender().getUser().getUsername(),
                                trans.getReceiver().getUser().getFirstName() + " " + trans.getReceiver().getUser().getLastName(),
                                trans.getAmount(),
                                trans.getSender().getCurrency(),
                                TransactionTypeEnum.EXPENSE
                        ))));

        List<TransactionTypeDto> unique = allTransactionTypeDto.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(TransactionTypeDto::getDate)
                                .thenComparing(TransactionTypeDto::getTransactionId))),
                        ArrayList::new));

        return unique.stream()
                .sorted(Comparator
                        .comparing
                                (TransactionTypeDto::getDate)
                        .reversed()).collect(Collectors.toList());
    }

    public TransactionDto transactionToTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getSender().getAccountNumber(),
                transaction.getReceiver().getAccountNumber(),
                transaction.getAmount(),
                transaction.getDate().toString()
        );
    }

    public List<MonthlySummaryDto> getMonthlySummary(String username, int monthNumber) {
        // no point in doing validations for month number (this can be done in front end - not empty, only digits)
        if (monthNumber < 1 || monthNumber > 12){
            throw new InvalidMonth("Invalid month");
        }
        Month month = Month.of(monthNumber);
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        List<Transaction> transactions = new ArrayList<>();
        user.getAccounts().stream()
                .map(Account::getIncomeTransactions)
                .forEach(x -> x.stream().forEach(transactions::add));
        user.getAccounts().stream()
                .map(Account::getOutcomeTransactions)
                .forEach(x -> x.stream().forEach(transactions::add));
        List<Transaction> unique = transactions.stream()
                .filter(x -> !x.getSender().getUser().getUsername().equals(x.getReceiver().getUser().getUsername()))
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(Transaction::getDate)
                                .thenComparing(Transaction::getId))),
                        ArrayList::new));

        Set<String> userCurrencies = user.getAccounts().stream()
                .map(x -> x.getCurrency()).collect(Collectors.toSet());
        List<MonthlySummaryDto> summaries = new ArrayList<>();
        for (String currency : userCurrencies) {
            double expenses = unique.stream()
                    .filter(t -> t.getSender().getCurrency().equals(currency))
                    .filter(t -> t.getDate().getMonth() == month)
                    .filter(x -> x.getSender().getUser().getUsername().equals(username))
                    .mapToDouble(Transaction::getAmount).sum();
            double income = unique.stream()
                    .filter(t -> t.getReceiver().getCurrency().equals(currency))
                    .filter(t -> t.getDate().getMonth() == month)
                    .filter(x -> x.getReceiver().getUser().getUsername().equals(username))
                    .mapToDouble(Transaction::getAmount).sum();

            summaries.add(new MonthlySummaryDto(month.toString(), currency, income, expenses));
        }

        return summaries;
    }


}
