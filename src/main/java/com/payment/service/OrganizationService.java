package com.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.payment.dto.OrgInfoResponse;
import com.payment.dto.OrganizationResponse;
import com.payment.dto.OrganizationUpdateRequest;

import jakarta.validation.Valid;

public interface OrganizationService {

    Page<OrganizationResponse> getAllOrganization(Pageable pageable);

    OrganizationResponse changeStatus(Long id, boolean status);

    OrgInfoResponse getOrganization(Long id);

    OrgInfoResponse updateOrganization(OrganizationUpdateRequest request,Long id);

    Page<OrganizationResponse> getOrganizationByStatus(Pageable pageable,boolean status);
}
