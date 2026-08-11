package com.example.booking_system.repository;

import com.example.booking_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(com.example.booking_system.model.Role role);

    @Query("SELECT u FROM User u WHERE " +
           "(CAST(:q AS string) IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%')) OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))) " +
           "AND (CAST(:activated AS boolean) IS NULL OR u.activated = :activated)")
    Page<User> findAllUsersWithPaginationAndSearch(@Param("q") String q, @Param("activated") Boolean activated, Pageable pageable);
}
