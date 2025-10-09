package com.payment.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.payment.dto.OrgInfoResponse;
import com.payment.dto.OrganizationResponse;
import com.payment.entities.Account;
import com.payment.entities.Address;
import com.payment.entities.Document;
import com.payment.entities.Organization;
import com.payment.repo.OrganizationRepo;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private OrganizationRepo organizationRepo;
    private ModelMapper modelMapper;

    public OrganizationServiceImpl(OrganizationRepo organizationRepo, ModelMapper modelMapper) {
        this.organizationRepo = organizationRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<OrganizationResponse> getAllOrganization(Pageable pageable) {
        Page<Organization> organizations = organizationRepo.findAll(pageable);

        // Convert Page<Organization> → Page<OrganizationResponse>
        Page<OrganizationResponse> response = organizations
                .map(org -> modelMapper.map(org, OrganizationResponse.class));
        return response;

    }

    @Override
    public OrganizationResponse changeStatus(Long id, boolean status) {
        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No orgaization with id" + id));
        organization.setActive(status);
        organization = organizationRepo.save(organization);
        return modelMapper.map(organization, OrganizationResponse.class);
    }

    @Override
    public OrgInfoResponse getOrganization(Long id) {
        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No orgaization with id" + id));
        OrgInfoResponse orgInfoResponse = modelMapper.map(organization, OrgInfoResponse.class);
        return orgInfoResponse;
    }

}
