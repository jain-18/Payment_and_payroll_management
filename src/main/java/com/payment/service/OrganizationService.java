package com.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.payment.dto.OrgInfoResponse;
import com.payment.dto.OrganizationResponse;

public interface OrganizationService {

    Page<OrganizationResponse> getAllOrganization(Pageable pageable);

    OrganizationResponse changeStatus(Long id, boolean status);

    OrgInfoResponse getOrganization(Long id);
}
