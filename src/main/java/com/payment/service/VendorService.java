package com.payment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.payment.dto.OrganizationResponse;
import com.payment.dto.RequestResp;
import com.payment.dto.VendorPaymentRequest;
import com.payment.dto.VendorPaymentResponse;
import com.payment.dto.VendorPaymentUpdate;
import com.payment.dto.VendorRequest;
import com.payment.dto.VendorResponse;
import com.payment.dto.VendorUpdateRequest;

public interface VendorService {

    VendorResponse createVendor(VendorRequest dto,Long orgId);

    VendorResponse getVendorById(Long id,Long Id);

    Page<VendorResponse> getAllVendors(Pageable pageable, Long orgId);

    VendorResponse updateVendor(Long id, VendorUpdateRequest dto,Long orgId);

    void deleteVendor(Long id,Long orgId);

    VendorPaymentResponse initiatePayment(VendorPaymentRequest paymentRequest,Long orgId);

    Page<VendorPaymentResponse> getPaymentStatus(Long orgId, String status, int page, int size);

    Page<VendorPaymentResponse> getOrgPaymentStatus(String string, int page, int size);

    VendorPaymentResponse sentRequestToAdmin(Long vendorId, Long orgId);

    Page<RequestResp> getAllVendorPaymentByStatus(Long orgId, String status, Pageable pageable);

    VendorPaymentResponse updatePaymentRequest(Long orgId, VendorPaymentUpdate dto);
    
    Page<VendorResponse> getVendorByName(String vendorName, Pageable pageable, Long orgId);
}
