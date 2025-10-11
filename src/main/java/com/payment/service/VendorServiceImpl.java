package com.payment.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.controller.VendorPaymentUpdate;
import com.payment.dto.AddressCreateRequest;
import com.payment.dto.RequestResp;
import com.payment.dto.VendorPaymentRequest;
import com.payment.dto.VendorPaymentResponse;
import com.payment.dto.VendorRequest;
import com.payment.dto.VendorResponse;
import com.payment.dto.VendorUpdateRequest;
import com.payment.entities.Account;
import com.payment.entities.Organization;
import com.payment.entities.Request;
import com.payment.entities.Vendor;
import com.payment.entities.VendorPayment;
import com.payment.exception.ResourceNotFoundException;
import com.payment.repo.OrganizationRepo;
import com.payment.repo.RequestRepo;
import com.payment.repo.VendorPaymentRepo;
import com.payment.repo.VendorRepo;

@Service
@Transactional
public class VendorServiceImpl implements VendorService {

    @Autowired
    VendorRepo vendorRepo;
    @Autowired
    VendorPaymentRepo vendorPaymentRepo;
    @Autowired
    OrganizationRepo organizationRepo;
    @Autowired
    RequestRepo requestRepo;

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
        if (vendorRepo.existsByAccount_AccountNumber(dto.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists");
        }
        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + orgId));


        Vendor vendor = new Vendor();
        vendor.setVendorName(dto.getVendorName());
        vendor.setEmail(dto.getEmail());
        vendor.setPhoneNumber(dto.getPhoneNumber());
        vendor.setActive(true);
        vendor.setOrganizations(org);

        Account account = new Account();
        account.setAccountNumber(dto.getAccountNumber());
        account.setIfsc(dto.getIfsc());
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);

        vendor.setAccount(account);

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
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));

        boolean belongsToOrg = vendor.getOrganizations().getOrganizationId() == orgId;

        if (!belongsToOrg) {
            throw new IllegalStateException("Vendor does not belong to this organization");
        }

        return mapToResponse(vendor);
    }

    @Override
    public List<VendorResponse> getAllVendors(Long orgId) {
        return vendorRepo.findAll().stream()
                .filter(vendor -> vendor.getOrganizations().getOrganizationId()==orgId)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VendorResponse updateVendor(Long id, VendorUpdateRequest dto, Long orgId) {
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));

        // Check if this vendor belongs to the given organization
        Organization currentOrg = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + orgId));

        if (!vendor.getOrganizations().equals(currentOrg)) {
            throw new IllegalStateException("Vendor does not belong to this organization");
        }

        // Vendor name
        if (dto.getVendorName() != null && !dto.getVendorName().equals(vendor.getVendorName())) {
            // if (vendorRepo.existsByVendorName(dto.getVendorName())) {
            //     throw new IllegalArgumentException("Vendor name already exists");
            // }
            vendor.setVendorName(dto.getVendorName());
        }

        // Email
        if (dto.getEmail() != null && !dto.getEmail().equals(vendor.getEmail())) {
            // if (vendorRepo.existsByEmail(dto.getEmail())) {
            //     throw new IllegalArgumentException("Email already exists");
            // }
            vendor.setEmail(dto.getEmail());
        }

        // Phone
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(vendor.getPhoneNumber())) {
            // if (vendorRepo.existsByPhoneNumber(dto.getPhoneNumber())) {
            //     throw new IllegalArgumentException("Phone number already exists");
            // }
            vendor.setPhoneNumber(dto.getPhoneNumber());
        }

        // Account number & IFSC
        if (dto.getAccountNumber() != null &&
                !dto.getAccountNumber().equals(vendor.getAccount().getAccountNumber())) {
            if (vendorRepo.existsByAccount_AccountNumber(dto.getAccountNumber())) {
                throw new IllegalArgumentException("Account number already exists");
            }
            vendor.getAccount().setAccountNumber(dto.getAccountNumber());
        }

        if (dto.getIfsc() != null) {
            vendor.getAccount().setIfsc(dto.getIfsc());
        }

        // Address
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
        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + orgId));

        // Check if the vendor is associated with this organization
        if (!vendor.getOrganizations().equals(organization)) {
            throw new IllegalStateException("Vendor does not belong to this organization");
        }

        // Remove the association between vendor and this specific organization
        // vendor.getOrganizations().remove(organization);
        organization.getVendors().remove(vendor);

        // If vendor is no longer associated with any organization, you can decide to
        // delete it
        VendorPayment vp = vendorPaymentRepo.findByVendor(vendor).orElse(null);
        if(vp != null){
            throw new IllegalStateException("Vendor has existing transactions; cannot delete");
        }
        vendorRepo.delete(vendor);
    }

    @Override
    public VendorPaymentResponse initiatePayment(VendorPaymentRequest request, Long orgId) {
        Vendor vendor = vendorRepo.findById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

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
    @Transactional
    public VendorPaymentResponse updatePaymentRequest(Long orgId, VendorPaymentUpdate dto) {
        // 1. Fetch VendorPayment
        VendorPayment vendorPayment = vendorPaymentRepo.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor payment not found"));

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

}
