package com.payment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.payment.dto.OrganizationResponse;
import com.payment.dto.RequestResp;
import com.payment.dto.VendorPaymentRequest;
import com.payment.dto.VendorPaymentResponse;
import com.payment.dto.VendorPaymentUpdate;
import com.payment.dto.VendorRequest;
import com.payment.dto.VendorResponse;
import com.payment.dto.VendorUpdateRequest;
import com.payment.security.JwtTokenProvider;
import com.payment.service.VendorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/api/vendors")
@Validated
public class VendorController {

    @Autowired
    VendorService vendorService;
    
    @Autowired JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping
    public ResponseEntity<VendorResponse> createVendor(@Valid @RequestBody VendorRequest dto,
            HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        return new ResponseEntity<>(vendorService.createVendor(dto, orgId), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/{id}")
    public ResponseEntity<VendorResponse> getVendor(@PathVariable Long id, HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        return ResponseEntity.ok(vendorService.getVendorById(id, orgId));
    }
    
    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/by-name")
    public ResponseEntity<Page<VendorResponse>> getVendorByname(@RequestParam String vendorName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "vendorName") String sortBy, HttpServletRequest request) {
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<VendorResponse> response = vendorService.getVendorByName(vendorName, pageable, orgId);
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping
    public ResponseEntity<Page<VendorResponse>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "vendorId") String sortBy, HttpServletRequest request) {

    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<VendorResponse> vendors = vendorService.getAllVendors(pageable, orgId);
        return ResponseEntity.ok(vendors);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{id}")
    public ResponseEntity<VendorResponse> updateVendor(@PathVariable Long id,
            @Valid @RequestBody VendorUpdateRequest dto, HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        return ResponseEntity.ok(vendorService.updateVendor(id, dto, orgId));
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id, HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        vendorService.deleteVendor(id, orgId);
        return ResponseEntity.noContent().build();
    }

    // Initiate vendor payment
    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/payments")
    public ResponseEntity<VendorPaymentResponse> initiatePayment(@Valid @RequestBody VendorPaymentRequest dto,
            HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        return new ResponseEntity<>(vendorService.initiatePayment(dto, orgId), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/payments/paid")
    public ResponseEntity<Page<VendorPaymentResponse>> getPaymentStatusPaid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {

    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token); // TODO: replace with logged-in orgId
        Page<VendorPaymentResponse> payments = vendorService.getPaymentStatus(orgId, "PAID", page, size);
        return ResponseEntity.ok(payments);
    }
    
    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/payments/all")
    public ResponseEntity<Page<VendorPaymentResponse>> getAllVendorPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        String token = jwtTokenProvider.getTokenFromRequest(request);
        Long orgId = jwtTokenProvider.extractOrganizationId(token);

        Page<VendorPaymentResponse> payments = vendorService.getAllVendorPayments(orgId, page, size);
        return ResponseEntity.ok(payments);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/payments/notPaid")
    public ResponseEntity<Page<VendorPaymentResponse>> getPaymentStatusNotPaid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {

    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token); // TODO: replace with logged-in orgId
        Page<VendorPaymentResponse> payments = vendorService.getPaymentStatus(orgId, "NOT_PAID", page, size);
        return ResponseEntity.ok(payments);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/payments")
    public ResponseEntity<Page<VendorPaymentResponse>> getPaymentsByStatus(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        String token = jwtTokenProvider.getTokenFromRequest(request);
        Long orgId = jwtTokenProvider.extractOrganizationId(token);

        Page<VendorPaymentResponse> payments;

        // If no status provided, return all payments
        if (status == null || status.isBlank()) {
            payments = vendorService.getAllVendorPayments(orgId, page, size);
        } else {
            payments = vendorService.getPaymentStatus(orgId, status.toUpperCase(), page, size);
        }

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

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/request")
    public ResponseEntity<VendorPaymentResponse> paymentRequestToAdmin(@RequestParam Long vendorId, HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        VendorPaymentResponse response = vendorService.sentRequestToAdmin(vendorId, orgId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/vendorPaymentRejected")
    public ResponseEntity<Page<RequestResp>> getRejectTedVendorPaymentForOrg(HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "actionDate") String sortBy) {
        // Long orgId = Will you method to get id of logged in organization
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(vendorService.getAllVendorPaymentByStatus(orgId, "REJECTED", pageable));
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/vendorPaymentApproved")
    public ResponseEntity<Page<RequestResp>> getApprovedVendorPaymentForOrg(HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "actionDate") String sortBy) {
        // Long orgId = Will you method to get id of logged in organization
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(vendorService.getAllVendorPaymentByStatus(orgId, "APPROVED", pageable));
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/vendor-payments")
    public ResponseEntity<Page<RequestResp>> getVendorPaymentsByStatus(
            HttpServletRequest request,
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "actionDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        // 🔐 Replace with actual logged-in org ID extraction
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);

        Sort sort = sortDir.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<RequestResp> response = vendorService.getAllVendorPaymentByStatus(orgId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/editRejectedVendorPayment")
    public ResponseEntity<VendorPaymentResponse> getUpdatedResponse(@RequestBody VendorPaymentUpdate dto, HttpServletRequest request) {
        // Long orgId = Will you method to get id of logged in organization
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        return ResponseEntity.ok(vendorService.updatePaymentRequest(orgId, dto));
    }

}
