package com.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.payment.dto.EmployeeDetail;
import com.payment.dto.EmployeeRequest;
import com.payment.dto.EmployeeResponse;
import com.payment.dto.EmployeeUpdateRequest;
import com.payment.dto.RaiseConcernedResp;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest dto,Long orgId);

    EmployeeResponse getEmployeeById(Long id,Long orgId);

    Page<EmployeeResponse> getAllEmployees(Pageable pageable,Long orgId);

    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest dto,Long orgId);

    void deleteEmployee(Long id,Long orgId);

    void raiseConcerns(Long slipId, Long empId, Long orgId);

    Page<RaiseConcernedResp> getAllRaisedConcerns(PageRequest pageable, Long orgId, Long empId);

    EmployeeDetail getEmployeeDetail(Long empId);

    Page<EmployeeResponse> searchEmployees(String keyword, PageRequest pageable, Long orgId);
}