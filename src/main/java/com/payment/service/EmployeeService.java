package com.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.payment.dto.EmployeeRequest;
import com.payment.dto.EmployeeResponse;
import com.payment.dto.EmployeeUpdateRequest;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest dto);

    EmployeeResponse getEmployeeById(Long id);

    Page<EmployeeResponse> getAllEmployees(Pageable pageable);

    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest dto);

    void deleteEmployee(Long id);
}