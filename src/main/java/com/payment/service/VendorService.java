package com.payment.service;

import java.math.BigDecimal;
import java.util.List;

import com.payment.dto.VendorPaymentResponse;
import com.payment.dto.VendorRequest;
import com.payment.dto.VendorResponse;
import com.payment.dto.VendorUpdateRequest;

public interface VendorService {

    VendorResponse createVendor(VendorRequest dto);

    VendorResponse getVendorById(Long id);

    List<VendorResponse> getAllVendors();

    VendorResponse updateVendor(Long id, VendorUpdateRequest dto);

    void deleteVendor(Long id);

    VendorPaymentResponse initiatePayment(Long vendorId, BigDecimal amount);
}
