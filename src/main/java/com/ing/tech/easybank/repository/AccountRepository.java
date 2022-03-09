package com.ing.tech.EasyBank.repository;

import com.ing.tech.EasyBank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    public Optional<Account> getAccountByAccountNumber(String accountNumber);
}
