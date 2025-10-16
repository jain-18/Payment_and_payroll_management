package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.EmployeeRequest;
import com.payment.dto.EmployeeResponse;
import com.payment.dto.EmployeeUpdateRequest;
import com.payment.dto.RaiseConcernedResp;
import com.payment.security.JwtTokenProvider;
import com.payment.service.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest dto, HttpServletRequest request) {
        // code for fetching orgId from jwt
        String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
    	System.out.println(orgId);
    	System.out.println(dto.getEmployeeName());
    	System.out.println(dto.getEmail());
        return new ResponseEntity<>(employeeService.createEmployee(dto, orgId), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        // code for fetching orgId from jwt
    	String token = jwtTokenProvider.getTokenFromRequest(httpServletRequest);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        return ResponseEntity.ok(employeeService.getEmployeeById(id, orgId));
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "employeeName") String sortBy,
            HttpServletRequest httpServletRequest) {

        // code for fetching orgId from jwt
    	String token = jwtTokenProvider.getTokenFromRequest(httpServletRequest);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<EmployeeResponse> response = employeeService.getAllEmployees(pageable, orgId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateRequest dto, HttpServletRequest httpServletRequest) {
        // code for fetching orgId from jwt
    	String token = jwtTokenProvider.getTokenFromRequest(httpServletRequest);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto, orgId));
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        // code for fetching orgId from jwt
    	String token = jwtTokenProvider.getTokenFromRequest(httpServletRequest);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        employeeService.deleteEmployee(id, orgId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/raise-concerns")
    public ResponseEntity<Void> raiseConcerns(@RequestParam Long slipId, HttpServletRequest request){
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
    	Long empId = jwtTokenProvider.extractEmployeeId(token);
        employeeService.raiseConcerns(slipId,empId,orgId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/raised-concerns")
    public ResponseEntity<Page<RaiseConcernedResp>> getAllConcerns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "raiseAt") String sortBy, HttpServletRequest request
    ){
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
    	Long empId = jwtTokenProvider.extractEmployeeId(token);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<RaiseConcernedResp> response = employeeService.getAllRaisedConcerns(pageable, orgId,empId);
        return ResponseEntity.ok(response);
    }

}
