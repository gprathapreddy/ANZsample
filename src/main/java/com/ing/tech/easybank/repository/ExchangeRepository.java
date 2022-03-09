package com.ing.tech.EasyBank.repository;

import com.ing.tech.EasyBank.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
}
