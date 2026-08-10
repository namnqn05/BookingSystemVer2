package com.example.booking_system.repository;

import com.example.booking_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
