package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.PendingVendorRes;
import com.payment.dto.RequestResp;
import com.payment.service.AdminService;
import com.payment.service.VendorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    VendorService vendorService;

    @Autowired
    AdminService adminService;

    @GetMapping("/pendingVendor")
    public ResponseEntity<Page<PendingVendorRes>> getAllPendingVendor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<PendingVendorRes> response = adminService.getAllPendingVendorRequest(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/singleVendorRequest")
    public ResponseEntity<RequestResp> getAllPendingVendor(@RequestParam Long requestId){
        RequestResp requestResp = adminService.getSingleRequest(requestId);
        return ResponseEntity.ok(requestResp);
    }
    @PostMapping("/vendorRequestApproved")
    public ResponseEntity<RequestResp> requestApproved(@RequestParam Long requestId){
        RequestResp requestResp = adminService.vendorRequestApproved(requestId);
        return ResponseEntity.ok(requestResp);
    }

}
