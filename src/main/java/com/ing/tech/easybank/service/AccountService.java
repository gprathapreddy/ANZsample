package com.ing.tech.EasyBank.service;

import com.ing.tech.EasyBank.dto.AccountDto;
import com.ing.tech.EasyBank.dto.CreateNewAccountDtoInput;
import com.ing.tech.EasyBank.dto.CreateNewAccountDtoOutput;
import com.ing.tech.EasyBank.dto.TransactionDto;
import com.ing.tech.EasyBank.entity.Account;
import com.ing.tech.EasyBank.entity.Transaction;
import com.ing.tech.EasyBank.entity.User;
import com.ing.tech.EasyBank.exception.*;
import com.ing.tech.EasyBank.repository.AccountRepository;
import com.ing.tech.EasyBank.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    private final Map<String, String> mapCurrencies;


    public Set<AccountDto> getUserAccountsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        return user.getAccounts().stream()
                .map(account -> new AccountDto(
                        account.getAccountNumber(),
                        account.getCurrency(),
                        account.getBalance(),
                        user.getUsername()))
                .collect(Collectors.toSet());
    }

    public CreateNewAccountDtoOutput createAccount(CreateNewAccountDtoInput createNewAccountDto, String username) {
        String currency = createNewAccountDto.getCurrency();
        double balance  = createNewAccountDto.getBalance();

        if(balance < 0)
            throw new BalanceCannotBeNegative();

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);

        if (!mapCurrencies.containsKey(currency))
            throw new CurrencyNotFound();

        Account newAccount = new Account( user, currency, balance);
        newAccount.setAccountNumber("0");   //dummy account number to be able to save newAccount into DB; it is changed after saving to a valid account number
        accountRepository.save(newAccount);

        String accountNumber = createIban(currency, newAccount.getId());
        newAccount.setAccountNumber(accountNumber);
        accountRepository.save(newAccount);

        return new CreateNewAccountDtoOutput( username, accountNumber, currency, balance );

    }

    // if anything wrong with transaction history change them back to Set
    public List<TransactionDto> getIncomeHistory(String accountNumber) {
        Account account = accountRepository.getAccountByAccountNumber(accountNumber).orElseThrow(AccountNotFound::new);

         List<TransactionDto> transactionHistory = account.getIncomeTransactions()
                .stream()
                .sorted(Comparator
                        .comparing
                                (Transaction::getDate)
                        .reversed())
                .map(transactionService::transactionToTransactionDto)

                .collect(Collectors.toList());

        return transactionHistory;
    }

    public List<TransactionDto> getOutcomeHistory(String accountNumber) {
        Account account = accountRepository.getAccountByAccountNumber(accountNumber).orElseThrow(AccountNotFound::new);

        List<TransactionDto> transactionHistory = account.getOutcomeTransactions()
                .stream()
                .sorted(Comparator
                        .comparing
                                (Transaction::getDate)
                        .reversed())
                .map(transactionService::transactionToTransactionDto)
                .collect(Collectors.toList());

        return transactionHistory;
    }

    public Optional<Account> getAccountInCurrencyFromUsername(String username, String currency) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);

        return user.getAccounts()
                .stream()
                .filter(account -> account.getCurrency().equals(currency))
                .collect(Collectors.toList())
                .stream()
                .findFirst();
    }

    public String createIban(String currency, Long id) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        String country = "RO";
        builder.append(country);
        /*for (int i = 0; i < 2; i++) {
            builder.append(random.nextInt(10));
        }*/
        builder.append("00");
        String bankCode = "EASY";
        builder.append(bankCode);
        builder.append(currency);

        // ROxx EASY xxx1 0000 0000 0001
        long bankAccount = 1000000000000L + id;
        builder.append(bankAccount);
        return builder.toString();
    }


}
