package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.AdminData;
import com.payment.dto.RequestReasonDto;
import com.payment.dto.RequestResp;
import com.payment.dto.SalaryRequestRes;
import com.payment.dto.VendorRequestRes;
import com.payment.service.AdminService;
import com.payment.service.VendorService;

@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200/")
public class AdminController {
    @Autowired
    VendorService vendorService;

    @Autowired
    AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/vendor-requests")
    public ResponseEntity<Page<VendorRequestRes>> getAllVendorRequestsByStatus(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<VendorRequestRes> response = adminService.getALLVendorRequestByStatus(
                pageable,
                status,
                "VENDORPAYMENT");

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/salary-requests")
    public ResponseEntity<Page<SalaryRequestRes>> getAllSalaryRequestsByStatus(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<SalaryRequestRes> response = adminService.getALLSalaryRequestByStatus(pageable, status, "SALARYPAYMENT");

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/singleRequest")
    public ResponseEntity<RequestResp> getAllPendingVendor(@RequestParam Long requestId) {
        RequestResp requestResp = adminService.getSingleRequest(requestId);
        return ResponseEntity.ok(requestResp);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/vendorRequestApproved")
    public ResponseEntity<RequestResp> requestApproved(@RequestParam Long requestId) {
        RequestResp requestResp = adminService.vendorRequestApproved(requestId);
        return ResponseEntity.ok(requestResp);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/vendorRequestRejected")
    public ResponseEntity<RequestResp> requestApproved(@RequestBody RequestReasonDto dto) {
        RequestResp requestResp = adminService.vendorRequestReject(dto);
        return ResponseEntity.ok(requestResp);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salaryRequestApproved")
    public ResponseEntity<RequestResp> salaryRequestApproved(@RequestParam Long requestId) {
        RequestResp requestResp = adminService.approveSalaryRequest(requestId);
        return ResponseEntity.ok(requestResp);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salaryRequestReject")
    public ResponseEntity<RequestResp> salaryRequestReject(@RequestBody RequestReasonDto dto) {
        RequestResp requestResp = adminService.rejectSalaryRequest(dto);
        return ResponseEntity.ok(requestResp);
    }

    @GetMapping("/dashboard-data")
    public ResponseEntity<AdminData> getDashboardData() {
        AdminData adminData = adminService.getDashboardData();
        return ResponseEntity.ok(adminData);
    }
    

}
