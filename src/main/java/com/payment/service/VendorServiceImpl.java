package com.payment.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.dto.AddressCreateRequest;
import com.payment.dto.RequestResp;
import com.payment.dto.VendorPaymentRequest;
import com.payment.dto.VendorPaymentResponse;
import com.payment.dto.VendorPaymentUpdate;
import com.payment.dto.VendorRequest;
import com.payment.dto.VendorResponse;
import com.payment.dto.VendorUpdateRequest;
import com.payment.entities.Account;
import com.payment.entities.Organization;
import com.payment.entities.Request;
import com.payment.entities.Vendor;
import com.payment.entities.VendorPayment;
import com.payment.exception.ResourceNotFoundException;
import com.payment.repo.AccountRepo;
import com.payment.repo.OrganizationRepo;
import com.payment.repo.RequestRepo;
import com.payment.repo.VendorPaymentRepo;
import com.payment.repo.VendorRepo;

@Service
@Transactional
public class VendorServiceImpl implements VendorService {

	@Autowired ModelMapper modelMapper;
	
    @Autowired
    VendorRepo vendorRepo;
    @Autowired
    VendorPaymentRepo vendorPaymentRepo;
    @Autowired
    OrganizationRepo organizationRepo;
    @Autowired
    RequestRepo requestRepo;
    @Autowired AccountRepo accountRepo;

    @Override
    public VendorResponse createVendor(VendorRequest dto, Long orgId) {

        // if (vendorRepo.existsByVendorName(dto.getVendorName())) {
        //     throw new IllegalArgumentException("Name already exists");
        // }
        // if (vendorRepo.existsByEmail(dto.getEmail())) {
        //     throw new IllegalArgumentException("Email already exists");
        // }
        // if (vendorRepo.existsByPhoneNumber(dto.getPhoneNumber())) {
        //     throw new IllegalArgumentException("Phone number already exists");
        // }
        
        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + orgId));

        if (!org.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        if (vendorRepo.existsByVendorNameAndOrganizations_OrganizationId(dto.getVendorName().trim(), orgId)) {
            throw new IllegalStateException("Vendor with name '" + dto.getVendorName() + "' already exists in this organization");
        }
        
        if (vendorRepo.existsByEmailAndOrganizations_OrganizationId(dto.getEmail(), orgId)) {
            throw new IllegalStateException("Email '" + dto.getEmail() + "' is already used in this organization");
        }

        // ✅ Check phone number uniqueness per organization
        if (vendorRepo.existsByPhoneNumberAndOrganizations_OrganizationId(dto.getPhoneNumber(), orgId)) {
            throw new IllegalStateException("Phone number '" + dto.getPhoneNumber() + "' is already used in this organization");
        }
        
        Vendor vendor = new Vendor();
        vendor.setVendorName(dto.getVendorName().trim());
        vendor.setEmail(dto.getEmail());
        vendor.setPhoneNumber(dto.getPhoneNumber());
        vendor.setActive(true);
        vendor.setOrganizations(org);

        Account newAccount;
        if (vendorRepo.existsByAccount_AccountNumber(dto.getAccountNumber())) {
            // Fetch the existing account
            newAccount = accountRepo.findByAccountNumber(dto.getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

            // Get all vendors linked to that account
            List<Vendor> vendors = vendorRepo.findByAccount(newAccount);

            // Check if any vendor linked to this account has a *different* name
            boolean usedByDifferentVendor = vendors.stream()
                    .anyMatch(v -> !v.getVendorName().equalsIgnoreCase(dto.getVendorName()));

            if (usedByDifferentVendor) {
                throw new IllegalStateException("This Account is already being used by another vendor");
            }

            // Otherwise, it's safe to reuse the same account
            vendor.setAccount(newAccount);
        } else {
            // Create a new account if it doesn't exist
            Account account = new Account();
            account.setAccountNumber(dto.getAccountNumber());
            account.setIfsc(dto.getIfsc());
            account.setAccountType("SAVINGS");
            account.setBalance(BigDecimal.ZERO);
            newAccount = accountRepo.save(account);
            vendor.setAccount(newAccount);
        }
        
        AddressCreateRequest adr = dto.getAddress();
        com.payment.entities.Address address = new com.payment.entities.Address();
        address.setCity(adr.getCity());
        address.setState(adr.getState());
        address.setPinCode(adr.getPinCode());
        vendor.setAddress(address);

        
        // vendor.getOrganizations().add(org);
        org.getVendors().add(vendor);

        Vendor saved = vendorRepo.save(vendor);

        return mapToResponse(saved);
    }

    @Override
    public VendorResponse getVendorById(Long id, Long orgId) {
    	
    	Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }
    	
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));

        boolean belongsToOrg = vendor.getOrganizations().getOrganizationId() == orgId;

        if (!belongsToOrg) {
            throw new IllegalStateException("Vendor does not belong to this organization");
        }

        return mapToResponse(vendor);
    }

    @Override
    public Page<VendorResponse> getAllVendors(Pageable pageable, Long orgId) {
        // 1. Validate organization
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));
        
        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        // 2. Fetch paginated vendors for that org
        Page<Vendor> vendors = vendorRepo.findByOrganizations_OrganizationId(orgId, pageable);

        // 3. Map to response DTO
        return vendors.map(this::mapToResponse);
    }

    @Override
    public VendorResponse updateVendor(Long id, VendorUpdateRequest dto, Long orgId) {
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));

        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + orgId));

        if (!org.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        // ✅ safer organization ownership check
        if (!vendor.getOrganizations().getOrganizationId().equals(orgId)) {
            throw new IllegalStateException("Vendor does not belong to this organization");
        }

        // ✅ Vendor name update check (unique per organization)
        if (dto.getVendorName() != null &&
            !dto.getVendorName().trim().equalsIgnoreCase(vendor.getVendorName())) {

            boolean nameExists = vendorRepo.existsByVendorNameAndOrganizations_OrganizationId(
                    dto.getVendorName().trim(), orgId);

            if (nameExists) {
                throw new IllegalStateException(
                    "Vendor name '" + dto.getVendorName() + "' already exists in this organization");
            }

            vendor.setVendorName(dto.getVendorName().trim());
        }

        // ✅ Email
        if (dto.getEmail() != null &&
                !dto.getEmail().equalsIgnoreCase(vendor.getEmail())) {

                boolean emailExists = vendorRepo.existsByEmailAndOrganizations_OrganizationId(
                        dto.getEmail(), orgId);
                if (emailExists) {
                    throw new IllegalStateException(
                        "Email '" + dto.getEmail() + "' is already used in this organization");
                }
                vendor.setEmail(dto.getEmail());
            }

        // ✅ Phone number
        if (dto.getPhoneNumber() != null &&
                !dto.getPhoneNumber().equals(vendor.getPhoneNumber())) {

                boolean phoneExists = vendorRepo.existsByPhoneNumberAndOrganizations_OrganizationId(
                        dto.getPhoneNumber(), orgId);
                if (phoneExists) {
                    throw new IllegalStateException(
                        "Phone number '" + dto.getPhoneNumber() + "' is already used in this organization");
                }
                vendor.setPhoneNumber(dto.getPhoneNumber());
            }

        // ✅ Account update
        if (dto.getAccountNumber() != null &&
        	    !dto.getAccountNumber().equals(vendor.getAccount().getAccountNumber())) {

        	    if (vendorRepo.existsByAccount_AccountNumber(dto.getAccountNumber())) {

        	        Account existingAccount = accountRepo.findByAccountNumber(dto.getAccountNumber())
        	                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        	        List<Vendor> vendors = vendorRepo.findByAccount(existingAccount);

        	        boolean usedByDifferentVendor = vendors.stream()
        	                .anyMatch(v -> !v.getVendorName().equalsIgnoreCase(dto.getVendorName()));

        	        if (usedByDifferentVendor) {
        	            throw new IllegalStateException("This Account is already being used by another vendor");
        	        }

        	        // Safe to reuse same account (same vendor name)
        	        vendor.setAccount(existingAccount);

        	    } else {
        	        // New account — create and assign
        	        Account newAccount = new Account();
        	        newAccount.setAccountNumber(dto.getAccountNumber());
        	        newAccount.setIfsc(dto.getIfsc());
        	        newAccount.setAccountType("SAVINGS");
        	        newAccount.setBalance(BigDecimal.ZERO);
        	        newAccount = accountRepo.save(newAccount);
        	        vendor.setAccount(newAccount);
        	    }
        	}

        if (dto.getIfsc() != null) {
            vendor.getAccount().setIfsc(dto.getIfsc());
        }

        // ✅ Address update
        if (dto.getAddress() != null) {
            AddressCreateRequest adr = dto.getAddress();
            vendor.getAddress().setCity(adr.getCity());
            vendor.getAddress().setState(adr.getState());
            vendor.getAddress().setPinCode(adr.getPinCode());
        }

        Vendor updated = vendorRepo.save(vendor);
        return mapToResponse(updated);
    }

    @Override
    public void deleteVendor(Long vendorId, Long orgId) {

        // 1. Fetch vendor and organization
        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + orgId));

        // 2. Validate organization state
        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        // 3. Ensure vendor belongs to this organization
        if (!vendor.getOrganizations().getOrganizationId().equals(orgId)) {
            throw new IllegalStateException("Vendor does not belong to this organization");
        }

        // 4. Prevent deletion if vendor has any payment records
        boolean hasPayments = vendorPaymentRepo.existsByVendor(vendor);
        if (hasPayments) {
            throw new IllegalStateException("Vendor has existing transactions; cannot delete");
        }

        // 5. Handle account reference cleanup
        Account account = vendor.getAccount();
        List<Vendor> vendorsUsingSameAccount = vendorRepo.findByAccount(account);

        // Remove vendor from organization’s vendor list
        organization.getVendors().remove(vendor);

        // Delete the vendor
        vendorRepo.delete(vendor);

        // Delete account only if this vendor was the only one using it
        if (vendorsUsingSameAccount.size() == 1) {
            accountRepo.delete(account);
        }
    }

    @Override
    public VendorPaymentResponse initiatePayment(VendorPaymentRequest request, Long orgId) {
        Vendor vendor = vendorRepo.findById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        boolean isRegistered = vendor.getOrganizations().getOrganizationId() == orgId;

        if (!isRegistered) {
            throw new IllegalStateException("Organization not registered for this vendor");
        }

        VendorPayment vp = new VendorPayment();
        vp.setVendor(vendor);
        vp.setAmount(request.getAmount());
        vp.setRequest(null); // nullable
        vp.setStatus("NOT_PAID");

        VendorPayment saved = vendorPaymentRepo.save(vp);

        // Map to DTO
        VendorPaymentResponse response = new VendorPaymentResponse();
        response.setVpId(saved.getVpId());
        response.setAmount(saved.getAmount());
        response.setVendorId(vendor.getVendorId());
        response.setVendorName(vendor.getVendorName());
        response.setStatus(saved.getStatus());
        return response;
    }

    private VendorResponse mapToResponse(Vendor vendor) {
        VendorResponse resp = new VendorResponse();
        resp.setVendorId(vendor.getVendorId());
        resp.setVendorName(vendor.getVendorName());
        resp.setEmail(vendor.getEmail());
        resp.setPhoneNumber(vendor.getPhoneNumber());
        resp.setAccountNumber(vendor.getAccount().getAccountNumber());
        resp.setIfsc(vendor.getAccount().getIfsc());
        resp.setActive(vendor.isActive());

        AddressCreateRequest adr = new AddressCreateRequest();
        adr.setCity(vendor.getAddress().getCity());
        adr.setState(vendor.getAddress().getState());
        adr.setPinCode(vendor.getAddress().getPinCode());
        resp.setAddress(adr);

        resp.setOrganizationId(vendor.getOrganizations().getOrganizationId());

        return resp;
    }

    @Override
    public Page<VendorPaymentResponse> getPaymentStatus(Long orgId, String status, int page, int size) {
        
    	Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }
    	
    	PageRequest pageable = PageRequest.of(page, size);
        Page<VendorPayment> payments = vendorPaymentRepo
                .findByVendor_Organizations_OrganizationIdAndStatus(orgId, status, pageable);

        return payments.map(payment -> {
            VendorPaymentResponse resp = new VendorPaymentResponse();
            resp.setVpId(payment.getVpId());
            resp.setAmount(payment.getAmount());
            resp.setVendorId(payment.getVendor().getVendorId());
            resp.setVendorName(payment.getVendor().getVendorName());
            resp.setStatus(payment.getStatus());
            if (payment.getRequest() != null) {
                resp.setRequestId(payment.getRequest().getRequestId());
            }
            return resp;
        });
    }
    
    @Override
    public Page<VendorPaymentResponse> getAllVendorPayments(Long orgId, int page, int size) {
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<VendorPayment> payments = vendorPaymentRepo
                .findByVendor_Organizations_OrganizationId(orgId, pageable);

        return payments.map(payment -> {
            VendorPaymentResponse resp = new VendorPaymentResponse();
            resp.setVpId(payment.getVpId());
            resp.setAmount(payment.getAmount());
            resp.setVendorId(payment.getVendor().getVendorId());
            resp.setVendorName(payment.getVendor().getVendorName());
            resp.setStatus(payment.getStatus());
            if (payment.getRequest() != null) {
                resp.setRequestId(payment.getRequest().getRequestId());
            }
            return resp;
        });
    }

    @Override
    public Page<VendorPaymentResponse> getOrgPaymentStatus(String status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<VendorPayment> payments = vendorPaymentRepo.findByStatus(status, pageable);

        return payments.map(payment -> {
            VendorPaymentResponse resp = new VendorPaymentResponse();
            resp.setVpId(payment.getVpId());
            resp.setAmount(payment.getAmount());
            resp.setVendorId(payment.getVendor().getVendorId());
            resp.setVendorName(payment.getVendor().getVendorName());
            resp.setStatus(payment.getStatus());
            return resp;
        });
    }

    @Override
    public VendorPaymentResponse sentRequestToAdmin(Long vendorPaymentId, Long orgId) {
        // 1. Check if organization exists
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }
        
        // 2. Fetch the payment
        VendorPayment vp = vendorPaymentRepo.findById(vendorPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Request not found"));

        // 3. Get the vendor who raised this payment
        Vendor vendor = vp.getVendor();

        // 4. Check whether this vendor belongs to this organization
        boolean isAssociated = vendor.getOrganizations().getOrganizationId() == orgId;

        if (!isAssociated) {
            throw new IllegalStateException("Payment does not belong to this organization");
        }

        if (vp.getRequest() != null) {
            throw new IllegalStateException("A request for this vendor payment has already been sent to admin");
        }

        // 5. Create the request
        Request request = new Request();
        request.setRequestType("VendorPayment");
        request.setRequestStatus("PENDING");
        request.setTotalAmount(vp.getAmount());
        request.setCreatedBy(organization.getOrganizationName()); // or org admin's name
        request.setOrganization(organization);
        request.setRequestDate(LocalDate.now());

        // 6. Save the request
        Request savedRequest = requestRepo.save(request);

        // 7. Associate request with the payment
        vp.setRequest(savedRequest);
        vp.setStatus("PENDING"); // optional: update payment status
        vendorPaymentRepo.save(vp);

        // 8. Map to response
        VendorPaymentResponse response = new VendorPaymentResponse();
        response.setVpId(vp.getVpId());
        response.setAmount(vp.getAmount());
        response.setVendorId(vendor.getVendorId());
        response.setVendorName(vendor.getVendorName());
        response.setStatus(vp.getStatus());

        return response;
    }

    @Override
    public Page<RequestResp> getAllVendorPaymentByStatus(Long orgId, String status, Pageable pageable) {
        
    	Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }
    	
    	Page<Request> requests = requestRepo.findByOrganization_OrganizationIdAndRequestStatus(
                orgId, status, pageable);

        return requests.map(req -> {
            RequestResp resp = new RequestResp();
            resp.setRequestId(req.getRequestId());
            resp.setRequestType(req.getRequestType());
            resp.setRequestStatus(req.getRequestStatus());
            resp.setRequestDate(req.getRequestDate());
            resp.setTotalAmount(req.getTotalAmount());
            resp.setCreatedBy(req.getCreatedBy());
            resp.setRejectReason(req.getRejectReason());

            if (req.getOrganization() != null && req.getOrganization().getAccount() != null) {
                resp.setBalance(req.getOrganization().getAccount().getBalance());
            }

            return resp;
        });
    }
    
    @Override
    public Page<RequestResp> getAllVendorPayments(Long orgId, Pageable pageable) {

        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        // Fetch all requests for the organization (without filtering by status)
        Page<Request> requests = requestRepo.findByOrganization_OrganizationId(orgId, pageable);

        return requests.map(req -> {
            RequestResp resp = new RequestResp();
            resp.setRequestId(req.getRequestId());
            resp.setRequestType(req.getRequestType());
            resp.setRequestStatus(req.getRequestStatus());
            resp.setRequestDate(req.getRequestDate());
            resp.setTotalAmount(req.getTotalAmount());
            resp.setCreatedBy(req.getCreatedBy());
            resp.setRejectReason(req.getRejectReason());

            if (req.getOrganization() != null && req.getOrganization().getAccount() != null) {
                resp.setBalance(req.getOrganization().getAccount().getBalance());
            }

            return resp;
        });
    }


    @Override
    @Transactional
    public VendorPaymentResponse updatePaymentRequest(Long orgId, VendorPaymentUpdate dto) {
        // 1. Fetch VendorPayment
        VendorPayment vendorPayment = vendorPaymentRepo.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor payment not found"));
        
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        // 2. Ensure that the vendor payment belongs to the organization
        Vendor vendor = vendorPayment.getVendor();
        boolean isAssociated = vendor.getOrganizations().getOrganizationId() == orgId;

        if (!isAssociated) {
            throw new IllegalStateException("This payment does not belong to the logged-in organization");
        }

        // 3. Ensure the payment is currently rejected before allowing update
        if (!"REJECTED".equalsIgnoreCase(vendorPayment.getStatus())) {
            throw new IllegalStateException("Only rejected payments can be updated");
        }

        // 4. Update the amount
        vendorPayment.setAmount(dto.getAmount());

        // 5. Optionally, reset status to DRAFT or NEW (until resubmitted)
        vendorPayment.setStatus("UPDATED");

        // 6. Save changes
        VendorPayment updated = vendorPaymentRepo.save(vendorPayment);

        Request req = updated.getRequest();
        req.setRequestStatus("PENDING");
        req.setActionDate(null);
        req.setTotalAmount(updated.getAmount());
        requestRepo.save(req);

        // 7. Map to response
        VendorPaymentResponse response = new VendorPaymentResponse();
        response.setVpId(updated.getVpId());
        response.setAmount(updated.getAmount());
        response.setStatus(updated.getStatus());
        response.setVendorId(updated.getVendor().getVendorId());
        response.setVendorName(updated.getVendor().getVendorName());

        return response;
    }
    
    @Override
    public Page<VendorResponse> getVendorByName(String vendorName, Pageable pageable, Long orgId) {

        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        Page<Vendor> vendors = vendorRepo
                .findByVendorNameContainingIgnoreCaseAndOrganizations_OrganizationId(vendorName, orgId, pageable);

        return vendors.map(vendor -> {
            VendorResponse response = modelMapper.map(vendor, VendorResponse.class);
            if (vendor.getAccount() != null) {
                response.setAccountNumber(vendor.getAccount().getAccountNumber());
                response.setIfsc(vendor.getAccount().getIfsc());
            }
            if (vendor.getOrganizations() != null) {
                response.setOrganizationId(vendor.getOrganizations().getOrganizationId());
            }
            return response;
        });
    }

}
