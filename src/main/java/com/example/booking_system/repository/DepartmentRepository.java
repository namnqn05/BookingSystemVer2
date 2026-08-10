package com.example.booking_system.repository;

import com.example.booking_system.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT u.department FROM User u WHERE u.id = :userId")
    Optional<Department> findDepartmentByUserId(@Param("userId") Long userId);
}
