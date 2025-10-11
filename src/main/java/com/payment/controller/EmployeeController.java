package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.payment.dto.EmployeeRequest;
import com.payment.dto.EmployeeResponse;
import com.payment.dto.EmployeeUpdateRequest;
import com.payment.service.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest dto) {
        //code for fetching orgId from jwt
        Long orgId = 1L;
        return new ResponseEntity<>(employeeService.createEmployee(dto,orgId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id,HttpServletRequest httpServletRequest) {
        //code for fetching orgId from jwt
        Long orgId = 1L;
        return ResponseEntity.ok(employeeService.getEmployeeById(id,orgId));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "employeeName") String sortBy,
            HttpServletRequest httpServletRequest) {
        
        //code for fetching orgId from jwt
        Long orgId = 1L;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<EmployeeResponse> response = employeeService.getAllEmployees(pageable,orgId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id,
                                                              @Valid @RequestBody EmployeeUpdateRequest dto,HttpServletRequest httpServletRequest) {
        //code for fetching orgId from jwt
        Long orgId = 1L;
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto,orgId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id,HttpServletRequest httpServletRequest) {
        //code for fetching orgId from jwt
        Long orgId = 1L;
        employeeService.deleteEmployee(id,orgId);
        return ResponseEntity.noContent().build();
    }
}
