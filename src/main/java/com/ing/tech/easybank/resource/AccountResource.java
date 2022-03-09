package com.ing.tech.EasyBank.resource;

import com.ing.tech.EasyBank.dto.*;
import com.ing.tech.EasyBank.entity.User;
import com.ing.tech.EasyBank.exception.AccountNotFound;
import com.ing.tech.EasyBank.exception.UnauthorizedException;
import com.ing.tech.EasyBank.exception.UnequalRequestAndTokenUsername;
import com.ing.tech.EasyBank.repository.UserRepository;
import com.ing.tech.EasyBank.service.AccountService;
import com.ing.tech.EasyBank.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountResource {
    private final AccountService accountService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<CreateNewAccountDtoOutput> createNewAccount(@Valid @RequestBody CreateNewAccountDtoInput createNewAccountDto
            , Principal principal){
        return ResponseEntity.ok(accountService.createAccount(createNewAccountDto, principal.getName()));
    }

    @GetMapping()
    public ResponseEntity<Set<AccountDto>> getAccountsByUsername(Principal principal) {
        return ResponseEntity.ok(accountService.getUserAccountsByUsername(principal.getName()));
    }


    @GetMapping("/incomeTransactionHistory")
    public ResponseEntity<List<TransactionDto>> getIncomeTransactionHistory(
            @PathParam("accountNumber") String accountNumber,
            Principal principal
    ){
        if(!userService.userHasAccount(accountNumber,principal.getName()))
            throw new AccountNotFound();
        return ResponseEntity.ok(accountService.getIncomeHistory(accountNumber));
    }

    @GetMapping("/outcomeTransactionHistory")
    public ResponseEntity<List<TransactionDto>> getOutcomeTransactionHistory(
            @PathParam("accountNumber") String accountNumber,
            Principal principal
    ){
        if(!userService.userHasAccount(accountNumber,principal.getName()))
            throw new AccountNotFound();
        return ResponseEntity.ok(accountService.getOutcomeHistory(accountNumber));
    }


}
