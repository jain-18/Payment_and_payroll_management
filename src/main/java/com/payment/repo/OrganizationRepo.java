package com.payment.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.Organization;


public interface OrganizationRepo extends JpaRepository<Organization, Long>{
    Page<Organization> findByIsActive(boolean isActive, Pageable pageable);

//    Optional<Organization> findByUsername(String username);
}
