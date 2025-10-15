package com.payment.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.Organization;


public interface OrganizationRepo extends JpaRepository<Organization, Long>{
    Page<Organization> findByIsActive(boolean isActive, Pageable pageable);

    Optional<Organization> findByUsers_UserName(String userName);
    Optional<Organization> findByOrganizationName(String organizationName);
    Optional<Organization> findByOrganizationEmail(String organizationEmail);
    Optional<Organization> findByAccount_AccountNumber(String accountNumber);

    boolean existsByUsers_UserName(String userName);
    boolean existsByOrganizationName(String organizationName);
    boolean existsByOrganizationEmail(String organizationEmail);
    boolean existsByAccount_AccountNumber(String accountNumber);

    long count();

    long countByIsActiveTrue();

    long countByIsActiveFalse();
    
//    Optional<Organization> findByUsername(String username);
}
