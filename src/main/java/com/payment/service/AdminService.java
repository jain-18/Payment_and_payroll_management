package com.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.payment.dto.RequestReasonDto;
import com.payment.dto.RequestResp;
import com.payment.dto.SalaryRequestRes;
import com.payment.dto.VendorRequestRes;

public interface AdminService {


    RequestResp getSingleRequest(Long requestId);

    RequestResp vendorRequestApproved(Long requestId);

    RequestResp vendorRequestReject(RequestReasonDto dto);

    RequestResp approveSalaryRequest(Long requestId);

    RequestResp rejectSalaryRequest(RequestReasonDto dto);

    public Page<VendorRequestRes> getALLVendorRequestByStatus(PageRequest pageable, String status, String requestType);

    Page<SalaryRequestRes> getALLSalaryRequestByStatus(PageRequest pageable, String status, String requestType);
}
