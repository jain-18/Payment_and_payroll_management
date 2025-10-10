package com.payment.service;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.payment.dto.OrgInfoResponse;
import com.payment.dto.OrganizationResponse;
import com.payment.dto.OrganizationUpdateRequest;
import com.payment.entities.Account;
import com.payment.entities.Address;
import com.payment.entities.Organization;
import com.payment.repo.OrganizationRepo;


@Service
public class OrganizationServiceImpl implements OrganizationService {

    private OrganizationRepo organizationRepo;
    private ModelMapper modelMapper;
    private CloudinaryService cloudinaryService;

    public OrganizationServiceImpl(OrganizationRepo organizationRepo, ModelMapper modelMapper,CloudinaryService cloudinaryService) {
        this.organizationRepo = organizationRepo;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
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
        if (status == true) {
            /*
             * code for email to organization
             */
        }
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

    @Override
    public OrgInfoResponse updateOrganization(OrganizationUpdateRequest request, Long id) {

        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No organization found with id " + id));

        if (request.getOrganizationName() != null && !request.getOrganizationName().isBlank()) {
            organization.setOrganizationName(request.getOrganizationName());
        }

        if (request.getOrganizationEmail() != null && !request.getOrganizationEmail().isBlank()) {
            organization.setOrganizationEmail(request.getOrganizationEmail());
        }

        if (request.getAddress() != null) {
            if (organization.getAddress() == null) {
                organization.setAddress(new Address());
            }
            Address addr = organization.getAddress();

            if (request.getAddress().getCity() != null) {
                addr.setCity(request.getAddress().getCity());
            }
            if (request.getAddress().getState() != null) {
                addr.setState(request.getAddress().getState());
            }
            if (request.getAddress().getPinCode() != null) {
                addr.setPinCode(request.getAddress().getPinCode());
            }
        }
        if (request.getAccount() != null) {
            if (organization.getAccount() == null) {
                organization.setAccount(new Account());
            }
            Account acc = organization.getAccount();

            if (request.getAccount().getAccountNumber() != null) {
                acc.setAccountNumber(request.getAccount().getAccountNumber());
            }
            if (request.getAccount().getIfsc() != null) {
                acc.setIfsc(request.getAccount().getIfsc());
            }
        }
        if(request.getPancard()!=null && !request.getPancard().isEmpty()){
            try {
                organization.getDocument().setPanUrl(cloudinaryService.uploadFile(request.getPancard().getBytes(), request.getPancard().getOriginalFilename()));
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        if(request.getCancelledCheque()!=null && !request.getCancelledCheque().isEmpty()){
            try {
                organization.getDocument().setCancelledCheque(cloudinaryService.uploadFile(request.getCancelledCheque().getBytes(), request.getCancelledCheque().getOriginalFilename()));
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        if(request.getCompanyRegistrationCertificate()!=null && !request.getCompanyRegistrationCertificate().isEmpty()){
            try {
                organization.getDocument().setCompanyRegistrationCertificate(cloudinaryService.uploadFile(request.getCompanyRegistrationCertificate().getBytes(), request.getCompanyRegistrationCertificate().getOriginalFilename()));
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        organization.setActive(false);
        organization = organizationRepo.save(organization);
        return modelMapper.map(organization, OrgInfoResponse.class);
    }

    @Override
    public Page<OrganizationResponse> getOrganizationByStatus(Pageable pageable,boolean status) {
       Page<Organization> organizations = organizationRepo.findByIsActive(status,pageable);
        // Convert Page<Organization> → Page<OrganizationResponse>
        Page<OrganizationResponse> response = organizations
                .map(org -> modelMapper.map(org, OrganizationResponse.class));
        return response;
    }

}
