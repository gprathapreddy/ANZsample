package com.ing.tech.EasyBank.service;

import com.ing.tech.EasyBank.dto.*;
import com.ing.tech.EasyBank.entity.Account;
import com.ing.tech.EasyBank.entity.Transaction;
import com.ing.tech.EasyBank.entity.User;
import com.ing.tech.EasyBank.enums.TransactionTypeEnum;
import com.ing.tech.EasyBank.exception.AccountNotFound;
import com.ing.tech.EasyBank.exception.UnauthorizedException;
import com.ing.tech.EasyBank.exception.UserAlreadyExists;
import com.ing.tech.EasyBank.exception.UserNotFound;
import com.ing.tech.EasyBank.repository.AccountRepository;
import com.ing.tech.EasyBank.repository.UserRepository;
import com.ing.tech.EasyBank.security.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.ing.tech.EasyBank.security.AuthenticityRoles.ROLE_USER;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Slf4j

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AccountRepository accountRepository;

    private static final int TOKEN_TTL = 60 * 3;

    public CreateNewUserDtoOutput createNewUser(CreateNewUserDto createNewUserDto) {
        Optional<User> newUser = userRepository.findByUsername(createNewUserDto.getUsername());
        String hashedPassword = passwordEncoder.encode(createNewUserDto.getPassword());
        if (newUser.isPresent())
            throw new UserAlreadyExists();

        User user = new User(
                createNewUserDto.getFirstName(),
                createNewUserDto.getLastName(),
                createNewUserDto.getUsername(),
                hashedPassword,
                ROLE_USER
        );

        User savedUser = userRepository.save(user);

        return new CreateNewUserDtoOutput(
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getUsername()
        );
    }




    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException();
        }

        Set<String> authorities = Arrays.stream(user.getAuthority().split(",")).collect(Collectors.toSet());

        return jwtProvider.generateToken(username, authorities, TOKEN_TTL);
    }

    public boolean userHasAccount(String accountNumber, String username){
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        Account account = accountRepository.getAccountByAccountNumber(accountNumber).orElseThrow(AccountNotFound::new);

        return user.getAccounts().contains(account);
    }

}































