package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.OrgInfoResponse;
import com.payment.dto.OrganizationResponse;
import com.payment.dto.OrganizationUpdateRequest;
import com.payment.dto.RaiseConcernedResp;
import com.payment.security.JwtTokenProvider;
import com.payment.service.OrganizationService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/portal/organizations")
@CrossOrigin(origins = "http://localhost:4200/")
public class OrganizationController {

    private OrganizationService organizationService;
    
    @Autowired private JwtTokenProvider jwtTokenProvider;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/status")
    public ResponseEntity<OrganizationResponse> changeStatus(@RequestParam Long id, @RequestParam boolean status) {
        OrganizationResponse response = organizationService.changeStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orgInfo")
    public ResponseEntity<OrgInfoResponse> getOrganizationInfo(@RequestParam Long id) {
        OrgInfoResponse org = organizationService.getOrganization(id);
        return ResponseEntity.ok(org);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    @PatchMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrgInfoResponse> updateOrganization(@Valid @ModelAttribute OrganizationUpdateRequest request,
            @RequestParam Long id) {
        OrgInfoResponse org = organizationService.updateOrganization(request, id);
        return ResponseEntity.ok(org);
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    //the process is after getting list of concerns, there will bve 3 options delete, solved and edit
    // After clicking the edit the it can get stored in local storage, the data of concern
    // after editEmployee -> saveEmployee -> updateSalaryStructure -> Send request to admin-> organization have to mark maually to solved
    
    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/org-raised-concerns")
    public ResponseEntity<Page<RaiseConcernedResp>> getAllConcernsOfOrgagaization(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "raiseAt") String sortBy, HttpServletRequest request
    ){
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<RaiseConcernedResp> response = organizationService.getAllRaisedConcernsOfOrg(pageable, orgId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/solvedRaiseConcern")
    public ResponseEntity<RaiseConcernedResp> solvedRaised(@RequestParam Long concernId,HttpServlet httpServlet, HttpServletRequest request) {
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        RaiseConcernedResp resp = organizationService.solvedRaiseConcern(concernId,orgId);
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/concerns")
    public ResponseEntity<Void> deleteConcern(@RequestParam Long concernId, HttpServletRequest request){
    	String token = jwtTokenProvider.getTokenFromRequest(request);
    	Long orgId = jwtTokenProvider.extractOrganizationId(token);
        organizationService.deleteConcern(concernId,orgId);
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-name")
    public ResponseEntity<Page<OrganizationResponse>> getOrganizationByname(@RequestParam String organizationName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "organizationName") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<OrganizationResponse> response = organizationService.getOrganizationByName(organizationName, pageable);
        return ResponseEntity.ok(response);
    }
    

}
