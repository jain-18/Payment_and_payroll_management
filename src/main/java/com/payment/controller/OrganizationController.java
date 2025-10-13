package com.payment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.OrgInfoResponse;
import com.payment.dto.OrganizationResponse;
import com.payment.dto.OrganizationUpdateRequest;
import com.payment.dto.RaiseConcernedResp;
import com.payment.service.OrganizationService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/portal/organizations")
public class OrganizationController {

    private OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping("/status")
    public ResponseEntity<OrganizationResponse> changeStatus(@RequestParam Long id, @RequestParam boolean status) {
        OrganizationResponse response = organizationService.changeStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orgInfo")
    public ResponseEntity<OrgInfoResponse> getOrganizationInfo(@RequestParam Long id) {
        OrgInfoResponse org = organizationService.getOrganization(id);
        return ResponseEntity.ok(org);
    }

    @PatchMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrgInfoResponse> updateOrganization(@Valid @ModelAttribute OrganizationUpdateRequest request,
            @RequestParam Long id) {
        OrgInfoResponse org = organizationService.updateOrganization(request, id);
        return ResponseEntity.ok(org);
    }

    @GetMapping()
    public ResponseEntity<Page<OrganizationResponse>> getOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "organizationName") String sortBy,
            @RequestParam(required = false) Boolean active // optional filter
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<OrganizationResponse> response;

        if (active == null) {
            response = organizationService.getAllOrganization(pageable);
        } else {
            response = organizationService.getOrganizationByStatus(pageable, active);
        }

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/org-raised-concerns")
    public ResponseEntity<Page<RaiseConcernedResp>> getAllConcernsOfOrgagaization(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "raiseAt") String sortBy
    ){
        Long orgId = 1L;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<RaiseConcernedResp> response = organizationService.getAllRaisedConcernsOfOrg(pageable, orgId);
        return ResponseEntity.ok(response);
    }

}
