package com.ing.tech.EasyBank.repository;

import com.ing.tech.EasyBank.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
}
