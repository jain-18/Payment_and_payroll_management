package com.payment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.OrgInfoResponse;
import com.payment.dto.OrganizationResponse;
import com.payment.service.OrganizationService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/potal/orgainzations")
public class OrganizationController {

    private OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping()
    public ResponseEntity<Page<OrganizationResponse>> getAllOrganization(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "organizationName") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<OrganizationResponse> response = organizationService.getAllOrganization(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/status")
    public ResponseEntity<OrganizationResponse> changeStatus(@RequestParam Long id, @RequestParam boolean status) {
        OrganizationResponse response = organizationService.changeStatus(id,status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orgInfo")
    public ResponseEntity<OrgInfoResponse> getOrganizationInfo(@RequestParam Long id) {
        OrgInfoResponse org = organizationService.getOrganization(id);
        return ResponseEntity.ok(org);
    }
    
    

}
