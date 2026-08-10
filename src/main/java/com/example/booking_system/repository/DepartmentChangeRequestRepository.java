package com.example.booking_system.repository;

import com.example.booking_system.model.DepartmentChangeRequest;
import com.example.booking_system.model.DepartmentChangeRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentChangeRequestRepository extends JpaRepository<DepartmentChangeRequest, Long> {

    @Query("SELECT r FROM DepartmentChangeRequest r WHERE (:status IS NULL OR r.status = :status)")
    Page<DepartmentChangeRequest> findAllByStatusWithPagination(@Param("status") DepartmentChangeRequestStatus status, Pageable pageable);

    List<DepartmentChangeRequest> findByUserIdAndStatus(Long userId, DepartmentChangeRequestStatus status);
}
