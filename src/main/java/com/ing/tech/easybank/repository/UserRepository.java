package com.ing.tech.EasyBank.repository;

import com.ing.tech.EasyBank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUserByFirstName(String firstName);

    Optional<User> findByUsername(String username);


}
