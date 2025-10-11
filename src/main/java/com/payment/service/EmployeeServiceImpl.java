package com.payment.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.dto.EmployeeRequest;
import com.payment.dto.EmployeeResponse;
import com.payment.dto.EmployeeUpdateRequest;
import com.payment.entities.Account;
import com.payment.entities.Employee;
import com.payment.entities.Organization;
import com.payment.entities.Role;
import com.payment.entities.User;
import com.payment.exception.ResourceNotFoundException;
import com.payment.repo.AccountRepo;
import com.payment.repo.EmployeeRepo;
import com.payment.repo.OrganizationRepo;
import com.payment.repo.RoleRepo;
import com.payment.repo.UserRepo;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepository;

    @Autowired
    private AccountRepo accountRepository;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest dto, Long orgId) {
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("No orgaization with id" + orgId));

        if (!organization.isActive()) {
            throw new ResourceNotFoundException("Organization is not active for this operation");
        }
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists");
        }

        Employee employee = mapToEntity(dto);
        employee.setActive(true);
        employee.setOrganization(organization);
        Employee saved = employeeRepository.save(employee);
        User user = new User();
        user.setUserName(employee.getEmployeeName()+employee.getEmployeeId());
        user.setOrganization(organization);
        user.setEmployee(employee);
        user.setActive(true);
        user.setPassword(employee.getEmployeeName()+employee.getEmployeeId());
        Role role = roleRepo.findByRoleName("ROLE_EMPLOYEE");
        user.setRole(role);
        userRepo.save(user);

        return mapToResponse(saved);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id, Long orgId) {
        // 1. Check if organization exists and is active
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new RuntimeException("Organization is not active for this operation");
        }

        // 2. Fetch employee
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        // 3. Check if the employee belongs to this organization
        if (!employee.getOrganization().getOrganizationId().equals(orgId)) {
            throw new RuntimeException("This employee does not belong to the given organization");
        }

        // 4. Map to response
        return mapToResponse(employee);
    }

    @Override
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable, Long orgId) {
        // 1. Check if organization exists and is active
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new RuntimeException("Organization is not active for this operation");
        }

        // 2. Fetch employees for this specific organization
        Page<Employee> employees = employeeRepository.findByOrganization_OrganizationId(orgId, pageable);

        // 3. Map entities to response DTOs
        return employees.map(this::mapToResponse);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest dto, Long orgId) {
        // 1. Validate organization
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new RuntimeException("Organization is not active for this operation");
        }

        // 2. Fetch employee
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        // 3. Check org ownership
        if (!employee.getOrganization().getOrganizationId().equals(orgId)) {
            throw new RuntimeException("Employee does not belong to this organization");
        }

        // 4. Update fields if provided
        if (dto.getEmployeeName() != null)
            employee.setEmployeeName(dto.getEmployeeName());
        if (dto.getEmployeeRole() != null)
            employee.setEmployeeRole(dto.getEmployeeRole());
        if (dto.getDepartment() != null)
            employee.setDepartment(dto.getDepartment());
        if (dto.getSalary() != null)
            employee.setSalary(dto.getSalary());
        if (dto.getJoinedDate() != null)
            employee.setJoinedDate(dto.getJoinedDate());

        if (dto.getEmail() != null && !dto.getEmail().equals(employee.getEmail())) {
            if (employeeRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            employee.setEmail(dto.getEmail());
        }

        // 5. Handle account details
        if (dto.getAccountNumber() != null || dto.getIfsc() != null) {
            if (employee.getAccount() == null) {
                if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
                    throw new IllegalArgumentException("Account number already exists");
                }
                Account acc = new Account();
                acc.setAccountNumber(dto.getAccountNumber());
                acc.setIfsc(dto.getIfsc());
                acc.setAccountType("SAVINGS");
                acc.setBalance(BigDecimal.ZERO);
                employee.setAccount(acc);
            } else {
                if (dto.getAccountNumber() != null
                        && !dto.getAccountNumber().equals(employee.getAccount().getAccountNumber())) {
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

        // 6. Save and map
        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    public void deleteEmployee(Long id, Long orgId) {
        // 1. Validate organization
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new RuntimeException("Organization is not active for this operation");
        }

        // 2. Fetch employee
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        // 3. Check ownership
        if (!emp.getOrganization().getOrganizationId().equals(orgId)) {
            throw new RuntimeException("This employee does not belong to the given organization");
        }

        // 4. Delete employee
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
