package com.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.dto.EmployeeRequest;
import com.payment.dto.EmployeeResponse;
import com.payment.dto.EmployeeUpdateRequest;
import com.payment.entities.Account;
import com.payment.entities.Employee;
import com.payment.repo.AccountRepo;
import com.payment.repo.EmployeeRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepository;
    
    @Autowired
    private AccountRepo accountRepository;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Employee employee = mapToEntity(dto);
        employee.setActive(true);
        Employee saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));
        return mapToResponse(employee);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));

        if (dto.getEmployeeName() != null) employee.setEmployeeName(dto.getEmployeeName());
        if (dto.getEmployeeRole() != null) employee.setEmployeeRole(dto.getEmployeeRole());
        if (dto.getDepartment() != null) employee.setDepartment(dto.getDepartment());
        if (dto.getSalary() != null) employee.setSalary(dto.getSalary());
        if (dto.getJoinedDate() != null) employee.setJoinedDate(dto.getJoinedDate());
        if (dto.getEmail() != null && !dto.getEmail().equals(employee.getEmail())) {
            if (employeeRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            employee.setEmail(dto.getEmail());
        }

        if (dto.getIfsc() != null || dto.getAccountNumber() != null) {
        if (employee.getAccount() == null) {
        	if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
                throw new IllegalArgumentException("Account number already exists");
            }
            Account acc = new Account();
            acc.setAccountNumber(dto.getAccountNumber());
            acc.setIfsc(dto.getIfsc());
            acc.setAccountType("SAVINGS");
            acc.setBalance(employee.getAccount() != null ? employee.getAccount().getBalance() : BigDecimal.ZERO);
            employee.setAccount(acc);
        } else {
            if (dto.getAccountNumber() != null && !dto.getAccountNumber().equals(employee.getAccount().getAccountNumber())) {
                if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
                    throw new IllegalArgumentException("Account number already exists");
                }
                employee.getAccount().setAccountNumber(dto.getAccountNumber());
            }
            if (dto.getIfsc() != null) {
                employee.getAccount().setIfsc(dto.getIfsc());
            }
        }
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));
        employeeRepository.delete(emp);
    }


    private Employee mapToEntity(EmployeeRequest dto) {
        Employee employee = new Employee();
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setEmployeeRole(dto.getEmployeeRole());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());
        employee.setSalary(dto.getSalary());
        employee.setJoinedDate(dto.getJoinedDate());
        employee.setActive(true);

        Account account = new Account();
        account.setAccountNumber(dto.getAccountNumber());
        account.setIfsc(dto.getIfsc());
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);

        employee.setAccount(account);
        return employee;
    }

    private EmployeeResponse mapToResponse(Employee e) {
        EmployeeResponse dto = new EmployeeResponse();
        dto.setEmployeeId(e.getEmployeeId());
        dto.setEmployeeName(e.getEmployeeName());
        dto.setEmployeeRole(e.getEmployeeRole());
        dto.setEmail(e.getEmail());
        dto.setDepartment(e.getDepartment());
        dto.setSalary(e.getSalary());
        dto.setJoinedDate(e.getJoinedDate());
        dto.setActive(e.isActive());

        if (e.getAccount() != null) {
            dto.setAccountNumber(e.getAccount().getAccountNumber());
            dto.setIfsc(e.getAccount().getIfsc());
        }
        return dto;
    }
}
