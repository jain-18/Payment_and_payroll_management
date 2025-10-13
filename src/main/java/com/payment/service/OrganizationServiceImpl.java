package com.payment.service;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.payment.dto.OrgInfoResponse;
import com.payment.dto.OrganizationResponse;
import com.payment.dto.OrganizationUpdateRequest;
import com.payment.dto.RaiseConcernedResp;
import com.payment.entities.Account;
import com.payment.entities.Address;
import com.payment.entities.Employee;
import com.payment.entities.Organization;
import com.payment.entities.RaiseConcerns;
import com.payment.exception.ResourceNotFoundException;
import com.payment.repo.OrganizationRepo;
import com.payment.repo.RaiseConcernsRepo;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private OrganizationRepo organizationRepo;
    private ModelMapper modelMapper;
    private CloudinaryService cloudinaryService;

    @Autowired
    private RaiseConcernsRepo raiseConcernsRepo;
    
    @Autowired
    private EmailService emailService;

    public OrganizationServiceImpl(OrganizationRepo organizationRepo, ModelMapper modelMapper,
            CloudinaryService cloudinaryService) {
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
                .orElseThrow(() -> new ResourceNotFoundException("No organization found with id: " + id));
        organization.setActive(status);
        if (status == true) {
        	if (organization.getOrganizationEmail() != null && !organization.getOrganizationEmail().isBlank()) {
                String subject = "Organization Account Activated";
                String message = "Dear " + organization.getOrganizationName() + ",\n\n"
                        + "Your organization account has been activated successfully.\n"
                        + "You can now log in and start using our services.\n\n"
                        + "Regards,\nPaymentApp Team";

                emailService.sendCustomEmail(organization.getOrganizationEmail(), subject, message);
        }
        }
        organization = organizationRepo.save(organization);
        return modelMapper.map(organization, OrganizationResponse.class);
    }

    @Override
    public OrgInfoResponse getOrganization(Long id) {
        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No organization found with id: " + id));
        OrgInfoResponse orgInfoResponse = modelMapper.map(organization, OrgInfoResponse.class);
        return orgInfoResponse;
    }

    @Override
    public OrgInfoResponse updateOrganization(OrganizationUpdateRequest request, Long id) {

        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No organization found with id: " + id));

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
        if (request.getPancard() != null && !request.getPancard().isEmpty()) {
            try {
                organization.getDocument().setPanUrl(cloudinaryService.uploadFile(request.getPancard().getBytes(),
                        request.getPancard().getOriginalFilename()));
            } catch (IOException e) {
                throw new IllegalStateException("File upload failed: " + e.getMessage());
            }
        }
        if (request.getCancelledCheque() != null && !request.getCancelledCheque().isEmpty()) {
            try {
                organization.getDocument().setCancelledCheque(cloudinaryService.uploadFile(
                        request.getCancelledCheque().getBytes(), request.getCancelledCheque().getOriginalFilename()));
            } catch (IOException e) {
                throw new IllegalStateException("File upload failed: " + e.getMessage());
            }
        }
        if (request.getCompanyRegistrationCertificate() != null
                && !request.getCompanyRegistrationCertificate().isEmpty()) {
            try {
                organization.getDocument().setCompanyRegistrationCertificate(
                        cloudinaryService.uploadFile(request.getCompanyRegistrationCertificate().getBytes(),
                                request.getCompanyRegistrationCertificate().getOriginalFilename()));
            } catch (IOException e) {
                throw new IllegalStateException("File upload failed: " + e.getMessage());
            }
        }
        organization.setActive(false);
        organization = organizationRepo.save(organization);
        return modelMapper.map(organization, OrgInfoResponse.class);
    }

    @Override
    public Page<OrganizationResponse> getOrganizationByStatus(Pageable pageable, boolean status) {
        Page<Organization> organizations = organizationRepo.findByIsActive(status, pageable);
        // Convert Page<Organization> → Page<OrganizationResponse>
        Page<OrganizationResponse> response = organizations
                .map(org -> modelMapper.map(org, OrganizationResponse.class));
        return response;
    }

    @Override
    public Page<RaiseConcernedResp> getAllRaisedConcernsOfOrg(PageRequest pageable, Long orgId) {
        Page<RaiseConcerns> concernsPage = raiseConcernsRepo.findByOrganizationOrganizationId(orgId, pageable);

        return concernsPage.map(concern -> {
            RaiseConcernedResp resp = new RaiseConcernedResp();
            resp.setConcernId(concern.getConcernId());
            resp.setOrganizationName(concern.getOrganization().getOrganizationName());
            resp.setRaiseAt(concern.getRaiseAt().toString()); // you can format if needed
            resp.setSolved(concern.isSolved());
            
            if (concern.isSolved()) {
                Employee emp = concern.getEmployee();
                if (emp != null && emp.getEmail() != null && !emp.getEmail().isBlank()) {
                    String subject = "Your Raised Concern Has Been Resolved";
                    String message = "Dear " + emp.getEmployeeName() + ",\n\n"
                            + "We’re pleased to inform you that your concern raised on "
                            + concern.getRaiseAt() + " has been successfully resolved.\n\n"
                            + "If you have any further questions, feel free to reach out.\n\n"
                            + "Regards,\nPaymentApp Support Team";
                    emailService.sendCustomEmail(emp.getEmail(), subject, message);
                }
            }
            
            return resp;
        });
    }

}
