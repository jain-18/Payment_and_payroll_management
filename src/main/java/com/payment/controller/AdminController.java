package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.VendorRequestRes;
import com.payment.dto.RequestReasonDto;
import com.payment.dto.RequestResp;
import com.payment.dto.SalaryRequestRes;
import com.payment.service.AdminService;
import com.payment.service.VendorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    VendorService vendorService;

    @Autowired
    AdminService adminService;

    @GetMapping("/pendingVendor")
    public ResponseEntity<Page<VendorRequestRes>> getAllPendingVendor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<VendorRequestRes> response = adminService.getALLVendorRequestByStatus(pageable, "PENDING",
                "VENDORPAYMENT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approvedVendor")
    public ResponseEntity<Page<VendorRequestRes>> getAllApprovedVendor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<VendorRequestRes> response = adminService.getALLVendorRequestByStatus(pageable, "APPROVED",
                "VENDORPAYMENT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rejectedVendor")
    public ResponseEntity<Page<VendorRequestRes>> getAllRejectedVendor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<VendorRequestRes> response = adminService.getALLVendorRequestByStatus(pageable, "REJECTED",
                "VENDORPAYMENT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendingSalary")
    public ResponseEntity<Page<SalaryRequestRes>> getAllPendindSalary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<SalaryRequestRes> response = adminService.getALLSalaryRequestByStatus(pageable, "PENDING",
                "SALARYPAYMENT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approvedSalary")
    public ResponseEntity<Page<SalaryRequestRes>> getAllApprovedSalary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<SalaryRequestRes> response = adminService.getALLSalaryRequestByStatus(pageable, "APPROVED",
                "SALARYPAYMENT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rejectedSalary")
    public ResponseEntity<Page<SalaryRequestRes>> getAllRejectedSalary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<SalaryRequestRes> response = adminService.getALLSalaryRequestByStatus(pageable, "REJECTED",
                "SALARYPAYMENT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/singleRequest")
    public ResponseEntity<RequestResp> getAllPendingVendor(@RequestParam Long requestId) {
        RequestResp requestResp = adminService.getSingleRequest(requestId);
        return ResponseEntity.ok(requestResp);
    }

    @PostMapping("/vendorRequestApproved")
    public ResponseEntity<RequestResp> requestApproved(@RequestParam Long requestId) {
        RequestResp requestResp = adminService.vendorRequestApproved(requestId);
        return ResponseEntity.ok(requestResp);
    }

    @PostMapping("/vendorRequestRejected")
    public ResponseEntity<RequestResp> requestApproved(@RequestBody RequestReasonDto dto) {
        RequestResp requestResp = adminService.vendorRequestReject(dto);
        return ResponseEntity.ok(requestResp);
    }

    @PostMapping("/salaryRequestApproved")
    public ResponseEntity<RequestResp> salaryRequestApproved(@RequestParam Long requestId) {
        RequestResp requestResp = adminService.approveSalaryRequest(requestId);
        return ResponseEntity.ok(requestResp);
    }

    @PostMapping("/salaryRequestReject")
    public ResponseEntity<RequestResp> salaryRequestReject(@RequestBody RequestReasonDto dto) {
        RequestResp requestResp = adminService.rejectSalaryRequest(dto);
        return ResponseEntity.ok(requestResp);
    }

}
