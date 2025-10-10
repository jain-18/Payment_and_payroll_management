package com.payment.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.Request;

public interface RequestRepo extends JpaRepository<Request, Long> {
    Page<Request> findByRequestStatus(String requestStatus, Pageable pageable);
}
