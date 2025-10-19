package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.SalaryRequestOfMonth;
import com.payment.dto.SalarySlip;
import com.payment.dto.SalaryStructureRequest;
import com.payment.dto.SalaryStructureResponse;
import com.payment.security.JwtTokenProvider;
import com.payment.service.SalaryStructureService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/api/salary-structures")
@Validated
public class SalaryStructureController {

    @Autowired
    private SalaryStructureService salaryStructureService;
    
    @Autowired private JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping
    public ResponseEntity<SalaryStructureResponse> create(@RequestBody SalaryStructureRequest request,
            HttpServletRequest httpServletRequest) {
        // code for getting orgId from httpservletReq
    	String token = jwtTokenProvider.getTokenFromRequest(httpServletRequest);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        SalaryStructureResponse response = salaryStructureService.createSalaryStructure(request, orgId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{slipId}")
    public ResponseEntity<SalaryStructureResponse> update(@PathVariable Long slipId,
            HttpServletRequest httpServletRequest) {
        // code for getting orgId from httpservletReq
    	String token = jwtTokenProvider.getTokenFromRequest(httpServletRequest);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        SalaryStructureResponse response = salaryStructureService.updateSalaryStructure(slipId, orgId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/sendRequest")
    public ResponseEntity<Void> salaryRequest(HttpServletRequest request) {
        // get organization id from jwt
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        salaryStructureService.sendRequestToAdmin(orgId);
        return null;
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/sendSalaryUpdatedRequest")
    public ResponseEntity<Void> salaryUpdateRequest(HttpServletRequest request) {
        // get organization id from jwt
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        salaryStructureService.sendRequestUpdateToAdmin(orgId);
        return null;
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/allsalarySlip")
    public ResponseEntity<Page<SalaryRequestOfMonth>> getAllSalarySlip(HttpServletRequest request,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        // get organization id from jwt
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<SalaryRequestOfMonth> resp=  salaryStructureService.getAllSalarySlip(orgId,status,pageable);
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/salary-slip-of-emp")
    public ResponseEntity<Page<SalarySlip>> getSalarySlipOfEmployee(HttpServletRequest httpServletRequest,
        @RequestParam(required = false) String month,
        @RequestParam(required = false) String year,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDir){
    	String token = jwtTokenProvider.getTokenFromRequest(httpServletRequest);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
    	Long empId = jwtTokenProvider.extractEmployeeId(token);
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        
        Page<SalarySlip> resp = salaryStructureService.getSalarySlipWithPagination(orgId, empId, month, year, pageable);
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/employee-salary-slips")
    public ResponseEntity<Page<SalarySlip>> getSalarySlipsOfEmployee(
            HttpServletRequest request,
            @RequestParam Long empId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        String token = jwtTokenProvider.getTokenFromRequest(request);
        Long orgId = jwtTokenProvider.extractOrganizationId(token);

        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<SalarySlip> resp = salaryStructureService.getSalarySlipWithPagination(orgId, empId, month, year, pageable);
        return ResponseEntity.ok(resp);
    }
    
    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/createAllSalaryStructure")
    public ResponseEntity<String> sendAllRequest(HttpServletRequest httpServletRequest) {
        String token = jwtTokenProvider.getTokenFromRequest(httpServletRequest);
        Long orgId = jwtTokenProvider.extractOrganizationId(token);
        salaryStructureService.createAllSalaryStructure(orgId);
        return ResponseEntity.ok("Salary structures created successfully for all employees.");
    }
    


}
