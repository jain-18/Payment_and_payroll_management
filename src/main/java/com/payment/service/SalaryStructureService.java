package com.payment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.payment.dto.SalaryRequestOfMonth;
import com.payment.dto.SalarySlip;
import com.payment.dto.SalaryStructureRequest;
import com.payment.dto.SalaryStructureResponse;

public interface SalaryStructureService {

    SalaryStructureResponse createSalaryStructure(SalaryStructureRequest request,Long orgId);

    SalaryStructureResponse updateSalaryStructure(Long slipId,Long orgId);

    void sendRequestToAdmin(Long orgId);

    void sendRequestUpdateToAdmin(Long orgId);

    Page<SalaryRequestOfMonth> getAllSalarySlip(Long orgId,String status, PageRequest pageable);

    List<SalarySlip> getSalarySlip(Long orgId, Long empId, String month, String year);

    Page<SalarySlip> getSalarySlipWithPagination(Long orgId, Long empId, String month, String year, PageRequest pageable);
}
