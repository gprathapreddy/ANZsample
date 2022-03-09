package com.ing.tech.EasyBank.service;

import com.ing.tech.EasyBank.dto.*;
import com.ing.tech.EasyBank.entity.Account;
import com.ing.tech.EasyBank.entity.Request;
import com.ing.tech.EasyBank.entity.Transaction;
import com.ing.tech.EasyBank.entity.User;
import com.ing.tech.EasyBank.exception.*;
import com.ing.tech.EasyBank.repository.AccountRepository;
import com.ing.tech.EasyBank.repository.RequestRepository;
import com.ing.tech.EasyBank.repository.TransactionRepository;
import com.ing.tech.EasyBank.repository.UserRepository;
import com.ing.tech.EasyBank.service.helpers.DateFormatter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class RequestService {
    AccountRepository accountRepository;
    RequestRepository requestRepository;
    UserService userService;
    UserRepository userRepository;
    TransactionRepository transactionRepository;
    TransactionService transactionService;

    // create request
    public RequestSentDtoInput createRequest(RequestSentDtoInput requestSentDto, String username){

        User sender = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        User receiver = userRepository.findByUsername(requestSentDto.getReceiver()).orElseThrow(UserNotFound::new);

        if(sender.getUsername().equals(receiver.getUsername()))
            throw new RequestCannotBeToSelf("Request cannot be to self!");

        String accountNumber = requestSentDto.getTransferAccount();
        if (!checkAccountNumber(sender.getAccounts(), accountNumber)){
            throw new AccountNotFound();
        }
        double amount = requestSentDto.getAmount();
        String currency = requestSentDto.getCurrency();
        if (!checkCurrency(sender.getAccounts(), currency)){
            throw new CurrencyNotFound();
        }
        String message = requestSentDto.getMessage();

        //String finalDate = DateFormatter.getRomanianDateFormatString();

        Request request = new Request(
                sender, receiver, accountNumber, amount, currency, LocalDateTime.now(), message
        );
        requestRepository.save(request);
        return requestSentDto; // should I return void or anything else?

    }

    public TransactionDto acceptRequest(AcceptRequestDto acceptRequestDto, String username){

        Long id = acceptRequestDto.getId();
        Request request = requestRepository.findById(id).orElseThrow(RequestNotFound::new);

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);

        //request receiver must be the same with the one who tries to pay (accept the request)
        if(!user.getUsername().equals(request.getReceiver().getUsername()))
            throw new InvalidUsername("Only the request receiver can accept a request!");

        // this will get the first account which has the specified currency and enough money (can be improved)
        if (!checkCurrency(user.getAccounts(), request.getCurrency())){
            throw new CurrencyNotFound();
        }
        Account compatibleAccount = user.getAccounts().stream()
                .filter(x -> x.getCurrency().equals(request.getCurrency()) && x.getBalance() >= request.getAmount())
                .findFirst().orElseThrow(InsufficientFunds::new);
        Account accountToSendTo = accountRepository
                .getAccountByAccountNumber(request.getAccountNumber()).orElseThrow(AccountNotFound::new);
        double amount = request.getAmount();

        //String finalDate = DateFormatter.getRomanianDateFormatString();

        Transaction transaction = new Transaction(
                compatibleAccount,
                accountToSendTo,
                amount,
                LocalDateTime.now()
                );
        transactionRepository.save(transaction);
        requestRepository.delete(request);

        compatibleAccount.setBalance(compatibleAccount.getBalance() - amount);
        accountToSendTo.setBalance(accountToSendTo.getBalance() + amount);

        accountRepository.save(compatibleAccount);
        accountRepository.save(accountToSendTo);

        return transactionService.transactionToTransactionDto(transaction);
    }


    // decline request (delete)
    public void declineRequest(DeclineRequestDto declineRequestDto, String username){
        Long requestId = declineRequestDto.getId();

        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFound::new);

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);

        //request receiver must be the same with the one who tries to decline (accept the request)
        if(!user.getUsername().equals(request.getReceiver().getUsername()))
            throw new InvalidUsername("Only the request receiver can decline a request!");

        requestRepository.delete(request);
    }


    public Set<RequestReceivedDtoOutput> getReceivedRequests(String receiverUsername) {
        User receiver = userRepository.findByUsername(receiverUsername).orElseThrow(UserNotFound::new);
        return receiver.getReceivedRequests().stream()
                .map(x -> new RequestReceivedDtoOutput(
                        x.getId(),
                        receiverUsername,
                        x.getSender().getUsername(),
                        x.getAccountNumber(),
                        x.getAmount(),
                        x.getCurrency(),
                        x.getMessage()
                )).collect(Collectors.toSet());
    }

    public Set<RequestSentDtoOutput> getSentRequests(String senderUsername) {
        User sender = userRepository.findByUsername(senderUsername).orElseThrow(UserNotFound::new);
        return sender.getSentRequests().stream()
                .map(x -> new RequestSentDtoOutput(
                        x.getId(),
                        senderUsername,
                        x.getReceiver().getUsername(),
                        x.getAccountNumber(),
                        x.getAmount(),
                        x.getCurrency(),
                        x.getMessage()
                )).collect(Collectors.toSet());
    }


    // TODO this should probably be moved from here
    public boolean checkCurrency(Set<Account> accounts, String currency){
        return accounts.stream().anyMatch(x -> x.getCurrency().equals(currency));
    }

    public boolean checkAccountNumber(Set<Account> accounts, String accountNumber){
        return accounts.stream().anyMatch(x -> x.getAccountNumber().equals(accountNumber));
    }
}





















