package com.example.booking_system.repository;

import com.example.booking_system.model.Role;
import com.example.booking_system.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    @Query("select p.code from RolePermission rp join rp.permission p where rp.role = :role")
    List<String> findPermissionCodesByRole(@Param("role") Role role);
}
