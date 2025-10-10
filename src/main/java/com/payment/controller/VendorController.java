package com.payment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.RequestResp;
import com.payment.dto.VendorPaymentRequest;
import com.payment.dto.VendorPaymentResponse;
import com.payment.dto.VendorRequest;
import com.payment.dto.VendorResponse;
import com.payment.dto.VendorUpdateRequest;
import com.payment.service.VendorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/vendors")
@Validated
public class VendorController {

    @Autowired
    VendorService vendorService;

    @PostMapping
    public ResponseEntity<VendorResponse> createVendor(@Valid @RequestBody VendorRequest dto,
            HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        return new ResponseEntity<>(vendorService.createVendor(dto, orgId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorResponse> getVendor(@PathVariable Long id, HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        return ResponseEntity.ok(vendorService.getVendorById(id, orgId));
    }

    @GetMapping
    public ResponseEntity<List<VendorResponse>> getAllVendors(HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        return ResponseEntity.ok(vendorService.getAllVendors(orgId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendorResponse> updateVendor(@PathVariable Long id,
            @Valid @RequestBody VendorUpdateRequest dto, HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        return ResponseEntity.ok(vendorService.updateVendor(id, dto, orgId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id, HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        vendorService.deleteVendor(id, orgId);
        return ResponseEntity.noContent().build();
    }

    // Initiate vendor payment
    @PostMapping("/payments")
    public ResponseEntity<VendorPaymentResponse> initiatePayment(@Valid @RequestBody VendorPaymentRequest dto,
            HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        return new ResponseEntity<>(vendorService.initiatePayment(dto, orgId), HttpStatus.CREATED);
    }

    @GetMapping("/payments/paid")
    public ResponseEntity<Page<VendorPaymentResponse>> getPaymentStatusPaid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long orgId = 1L; // TODO: replace with logged-in orgId
        Page<VendorPaymentResponse> payments = vendorService.getPaymentStatus(orgId, "PAID", page, size);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/payments/notPaid")
    public ResponseEntity<Page<VendorPaymentResponse>> getPaymentStatusNotPaid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long orgId = 1L; // TODO: replace with logged-in orgId
        Page<VendorPaymentResponse> payments = vendorService.getPaymentStatus(orgId, "NOT_PAID", page, size);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/allOrgPayments/paid")
    public ResponseEntity<Page<VendorPaymentResponse>> getOrgPaymentStatusPaid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<VendorPaymentResponse> payments = vendorService.getOrgPaymentStatus("PAID", page, size);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/allOrgPayments/notPaid")
    public ResponseEntity<Page<VendorPaymentResponse>> getOrgPaymentStatusNotPaid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<VendorPaymentResponse> payments = vendorService.getOrgPaymentStatus("NOT_PAID", page, size);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/request")
    public ResponseEntity<VendorPaymentResponse> paymentRequestToAdmin(@RequestParam Long vendorId) {
        // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        VendorPaymentResponse response = vendorService.sentRequestToAdmin(vendorId,orgId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/vendorPaymentRejected")
    public ResponseEntity<Page<RequestResp>> getRejectTedVendorPaymentForOrg(HttpServletRequest request,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "actionDate") String sortBy
    ) {
        // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(vendorService.getAllVendorPaymentByStatus(orgId,"REJECTED",pageable));
    }

    @GetMapping("/vendorPaymentApproved")
    public ResponseEntity<Page<RequestResp>> getApprovedVendorPaymentForOrg(HttpServletRequest request,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "actionDate") String sortBy
    ) {
        // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(vendorService.getAllVendorPaymentByStatus(orgId,"APPROVED",pageable));
    }

    @PutMapping("/editRejectedVendorPayment")
    public ResponseEntity<VendorPaymentResponse> getUpdatedResponse(@RequestBody VendorPaymentUpdate dto) {
         // Long orgId = Will you method to get id of logged in organization
        Long orgId = 1L;
        return ResponseEntity.ok(vendorService.updatePaymentRequest(orgId,dto));
    }
    
    

    

}
