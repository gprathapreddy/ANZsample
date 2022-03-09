package com.ing.tech.EasyBank.resource;

import com.ing.tech.EasyBank.dto.MonthlySummaryDto;
import com.ing.tech.EasyBank.dto.TransactionDto;
import com.ing.tech.EasyBank.dto.TransactionTypeDto;
import com.ing.tech.EasyBank.exception.AccountNotFound;
import com.ing.tech.EasyBank.repository.AccountRepository;
import com.ing.tech.EasyBank.service.TransactionService;
import com.ing.tech.EasyBank.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.time.Month;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionResource {

    private final TransactionService transactionService;
    private final UserService userService;
    private final AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<TransactionDto> transfer(
            @Valid @RequestBody TransactionDto transactionDto,
            Principal principal
    ){
        if(!userService.userHasAccount(transactionDto.getFromAccountNumber(), principal.getName()))
            throw new AccountNotFound();

        return ResponseEntity.ok(transactionService.transfer(transactionDto));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionTypeDto>> getAllTransactions(Principal principal){
        return ResponseEntity.ok(transactionService.getAllTransactions(principal.getName()));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<MonthlySummaryDto>> getMonthlySummary(
            Principal principal,
            @PathParam("month") int month){
        return ResponseEntity.ok(transactionService.getMonthlySummary(principal.getName(), month));
    }

}
