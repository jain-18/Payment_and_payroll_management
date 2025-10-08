package com.payment.service;

import java.util.List;

import com.payment.dto.EmployeeRequest;
import com.payment.dto.EmployeeResponse;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest dto);

    EmployeeResponse getEmployeeById(Long id);

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse updateEmployee(Long id, EmployeeRequest dto);

    void deleteEmployee(Long id);
}