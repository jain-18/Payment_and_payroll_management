package com.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.payment.dto.OrgInfoResponse;
import com.payment.dto.OrganizationResponse;
import com.payment.dto.OrganizationUpdateRequest;
import com.payment.dto.RaiseConcernedResp;

public interface OrganizationService {

    Page<OrganizationResponse> getAllOrganization(Pageable pageable);

    OrganizationResponse changeStatus(Long id, boolean status);

    OrgInfoResponse getOrganization(Long id);

    OrgInfoResponse updateOrganization(OrganizationUpdateRequest request,Long id);

    Page<OrganizationResponse> getOrganizationByStatus(Pageable pageable,boolean status);

//    Page<RaiseConcernedResp> getAllRaisedConcernsOfOrg(PageRequest pageable, Long orgId);
    Page<RaiseConcernedResp> getAllRaisedConcernsOfOrg(Pageable pageable, Long orgId, Boolean solved);

    RaiseConcernedResp solvedRaiseConcern(Long concernId, Long orgId);

    void deleteConcern(Long concernId, Long orgId);

    Page<OrganizationResponse> getOrganizationByName(String orgainzationName, PageRequest pageable);
}
