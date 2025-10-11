package com.payment.repo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.Organization;
import com.payment.entities.Request;

public interface RequestRepo extends JpaRepository<Request, Long> {
    Page<Request> findByRequestStatus(String requestStatus, Pageable pageable);

    Page<Request> findByOrganization_OrganizationIdAndRequestStatus(Long orgId, String status, Pageable pageable);

    boolean existsByOrganizationAndRequestStatusInAndRequestDateBetweenAndTotalAmount(
            Organization organization,
            List<String> statuses,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal totalAmount);
}
