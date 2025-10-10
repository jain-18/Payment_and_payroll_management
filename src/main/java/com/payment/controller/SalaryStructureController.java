package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.SalaryStructureRequest;
import com.payment.dto.SalaryStructureResponse;
import com.payment.service.SalaryStructureService;

@RestController
@RequestMapping("/api/salary-structures")
@Validated
public class SalaryStructureController {

	@Autowired private SalaryStructureService salaryStructureService;

    @PostMapping
    public ResponseEntity<SalaryStructureResponse> create(@RequestBody SalaryStructureRequest request) {
        SalaryStructureResponse response = salaryStructureService.createSalaryStructure(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{slipId}")
    public ResponseEntity<SalaryStructureResponse> update(@PathVariable Long slipId) {
        SalaryStructureResponse response = salaryStructureService.updateSalaryStructure(slipId);
        return ResponseEntity.ok(response);
    }
}
