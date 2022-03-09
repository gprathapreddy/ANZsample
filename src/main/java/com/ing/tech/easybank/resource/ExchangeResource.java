package com.ing.tech.EasyBank.resource;

import com.ing.tech.EasyBank.dto.ExchangeDtoInput;
import com.ing.tech.EasyBank.dto.ExchangeDtoOutput;
import com.ing.tech.EasyBank.exception.AccountNotFound;
import com.ing.tech.EasyBank.repository.AccountRepository;
import com.ing.tech.EasyBank.service.ExchangeService;
import com.ing.tech.EasyBank.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/exchange")
@AllArgsConstructor
public class ExchangeResource {
    private final ExchangeService exchangeService;
    private final UserService userService;
    private final AccountRepository accountRepository;

    @PostMapping()
    public ResponseEntity<ExchangeDtoOutput> exchange(
            @Valid @RequestBody ExchangeDtoInput exchangeDtoInput,
            Principal principal
    ){
        if(!userService.userHasAccount(exchangeDtoInput.getFromAccountNumber(), principal.getName()))
            throw new AccountNotFound();

        return ResponseEntity.ok(exchangeService.exchange(exchangeDtoInput));
    }
}
