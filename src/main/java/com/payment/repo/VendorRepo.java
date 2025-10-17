package com.payment.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.entities.Vendor;

@Repository
public interface VendorRepo extends JpaRepository<Vendor, Long> {
	boolean existsByVendorName(String vendorName);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByAccount_AccountNumber(String accountNumber);
    Optional<Vendor> findById(Long id);
    Page<Vendor> findByOrganizations_OrganizationId(Long organizationId, Pageable pageable);
}
