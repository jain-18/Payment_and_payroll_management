package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.SalaryRequestOfMonth;
import com.payment.dto.SalaryStructureRequest;
import com.payment.dto.SalaryStructureResponse;
import com.payment.service.SalaryStructureService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/salary-structures")
@Validated
public class SalaryStructureController {

    @Autowired
    private SalaryStructureService salaryStructureService;

    @PostMapping
    public ResponseEntity<SalaryStructureResponse> create(@RequestBody SalaryStructureRequest request,
            HttpServletRequest httpServletRequest) {
        // code for getting orgId from httpservletReq
        Long orgId = 1L;
        SalaryStructureResponse response = salaryStructureService.createSalaryStructure(request, orgId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{slipId}")
    public ResponseEntity<SalaryStructureResponse> update(@PathVariable Long slipId,
            HttpServletRequest httpServletRequest) {
        // code for getting orgId from httpservletReq
        Long orgId = 1L;
        SalaryStructureResponse response = salaryStructureService.updateSalaryStructure(slipId, orgId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendRequest")
    public ResponseEntity<Void> salaryRequest(HttpServletRequest request) {
        // get organization id from jwt
        Long orgId = 1L;
        salaryStructureService.sendRequestToAdmin(orgId);
        return null;
    }

    @PostMapping("/sendSalaryUpdatedRequest")
    public ResponseEntity<Void> salaryUpdateRequest(HttpServletRequest request) {
        // get organization id from jwt
        Long orgId = 1L;
        salaryStructureService.sendRequestUpdateToAdmin(orgId);
        return null;
    }

    @GetMapping("/allsalarySlip")
    public ResponseEntity<Page<SalaryRequestOfMonth>> getAllSalarySlip(HttpServletRequest request,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        // get organization id from jwt
        Long orgId = 1L;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<SalaryRequestOfMonth> resp=  salaryStructureService.getAllSalarySlip(orgId,status,pageable);
        return ResponseEntity.ok(resp);
    }

}
