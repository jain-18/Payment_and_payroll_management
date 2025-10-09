package com.payment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.payment.dto.VendorPaymentRequest;
import com.payment.dto.VendorPaymentResponse;
import com.payment.dto.VendorRequest;
import com.payment.dto.VendorResponse;
import com.payment.dto.VendorUpdateRequest;
import com.payment.service.VendorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vendors")
@Validated
public class VendorController {

	@Autowired VendorService vendorService;

    @PostMapping
    public ResponseEntity<VendorResponse> createVendor(@Valid @RequestBody VendorRequest dto) {
        return new ResponseEntity<>(vendorService.createVendor(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorResponse> getVendor(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    @GetMapping
    public ResponseEntity<List<VendorResponse>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendorResponse> updateVendor(@PathVariable Long id,
    		@Valid @RequestBody VendorUpdateRequest dto) {
        return ResponseEntity.ok(vendorService.updateVendor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.noContent().build();
    }

    // Initiate vendor payment
    @PostMapping("/payments")
    public ResponseEntity<VendorPaymentResponse> initiatePayment(@Valid @RequestBody VendorPaymentRequest dto) {
        return new ResponseEntity<>(vendorService.initiatePayment(dto.getVendorId(), dto.getAmount()), HttpStatus.CREATED);
    }
}
