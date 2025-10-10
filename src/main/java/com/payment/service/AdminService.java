package com.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.payment.dto.PendingVendorRes;
import com.payment.dto.RequestResp;

public interface AdminService {

    Page<PendingVendorRes> getAllPendingVendorRequest(Pageable pageable);

    RequestResp getSingleRequest(Long requestId);

    RequestResp vendorRequestApproved(Long requestId);

}
