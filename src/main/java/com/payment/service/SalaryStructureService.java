package com.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.payment.dto.SalaryRequestOfMonth;
import com.payment.dto.SalaryStructureRequest;
import com.payment.dto.SalaryStructureResponse;

public interface SalaryStructureService {

    SalaryStructureResponse createSalaryStructure(SalaryStructureRequest request,Long orgId);

    SalaryStructureResponse updateSalaryStructure(Long slipId,Long orgId);

    void sendRequestToAdmin(Long orgId);

    void sendRequestUpdateToAdmin(Long orgId);

    Page<SalaryRequestOfMonth> getAllSalarySlip(Long orgId,String status, PageRequest pageable);
}
