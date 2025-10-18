package com.payment.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.entities.Request;
import com.payment.entities.Vendor;
import com.payment.entities.VendorPayment;

@Repository
public interface VendorPaymentRepo extends JpaRepository<VendorPayment, Long> {
    Page<VendorPayment> findByVendor_Organizations_OrganizationIdAndStatus(
        Long orgId, 
        String status, 
        Pageable pageable
    );

    Page<VendorPayment> findByStatus(String status, Pageable pageable);
    
    VendorPayment findByRequest(Request req);

    Optional<VendorPayment> findByVendor(Vendor vendor);
    
    boolean existsByVendor(Vendor vendor);
}