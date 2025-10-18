package com.payment.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.entities.Vendor;
import java.util.List;
import com.payment.entities.Account;
import com.payment.entities.Organization;


@Repository
public interface VendorRepo extends JpaRepository<Vendor, Long> {
	boolean existsByVendorName(String vendorName);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByAccount_AccountNumber(String accountNumber);
    Optional<Vendor> findById(Long id);
    Page<Vendor> findByOrganizations_OrganizationId(Long organizationId, Pageable pageable);
    List<Vendor> findByAccount(Account account);
    
    boolean existsByVendorNameAndOrganizations_OrganizationId(String vendorName, Long organizationId);
    boolean existsByEmailAndOrganizations_OrganizationId(String email, Long orgId);
    boolean existsByPhoneNumberAndOrganizations_OrganizationId(String phoneNumber, Long orgId);
    
    List<Vendor> findByEmail(String email);
    List<Vendor> findByPhoneNumber(String phoneNumber);
    
    Page<Vendor> findByVendorNameContainingIgnoreCase(String vendorName, PageRequest pageable);
}
