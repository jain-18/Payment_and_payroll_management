package com.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.payment.dto.EmployeeRequest;
import com.payment.dto.EmployeeResponse;
import com.payment.dto.EmployeeUpdateRequest;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest dto,Long orgId);

    EmployeeResponse getEmployeeById(Long id,Long orgId);

    Page<EmployeeResponse> getAllEmployees(Pageable pageable,Long orgId);

    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest dto,Long orgId);

    void deleteEmployee(Long id,Long orgId);
}