package com.payment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.entities.VendorPayment;

@Repository
public interface VendorPaymentRepo extends JpaRepository<VendorPayment, Long> {

}