package com.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.dto.AddressCreateRequest;
import com.payment.dto.VendorPaymentResponse;
import com.payment.dto.VendorRequest;
import com.payment.dto.VendorResponse;
import com.payment.dto.VendorUpdateRequest;
import com.payment.entities.Account;
import com.payment.entities.Organization;
import com.payment.entities.Vendor;
import com.payment.entities.VendorPayment;
import com.payment.repo.OrganizationRepo;
import com.payment.repo.VendorPaymentRepo;
import com.payment.repo.VendorRepo;

@Service
@Transactional
public class VendorServiceImpl implements VendorService {

	@Autowired VendorRepo vendorRepo;
    @Autowired VendorPaymentRepo vendorPaymentRepo;
    @Autowired OrganizationRepo organizationRepo;

    @Override
    public VendorResponse createVendor(VendorRequest dto) {

    	if (vendorRepo.existsByVendorName(dto.getVendorName())) {
            throw new IllegalArgumentException("Name already exists");
        }
        if (vendorRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (vendorRepo.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        if (vendorRepo.existsByAccount_AccountNumber(dto.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists");
        }

        Vendor vendor = new Vendor();
        vendor.setVendorName(dto.getVendorName());
        vendor.setEmail(dto.getEmail());
        vendor.setPhoneNumber(dto.getPhoneNumber());
        vendor.setActive(true);

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

        Organization org = organizationRepo.findById(dto.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        vendor.getOrganizations().add(org);
        org.getVendors().add(vendor);

        Vendor saved = vendorRepo.save(vendor);

        return mapToResponse(saved);
    }

    @Override
    public VendorResponse getVendorById(Long id) {
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
        return mapToResponse(vendor);
    }

    @Override
    public List<VendorResponse> getAllVendors() {
        return vendorRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VendorResponse updateVendor(Long id, VendorUpdateRequest dto) {
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        if (dto.getVendorName() != null && !dto.getVendorName().equals(vendor.getVendorName())) {
        	if (vendorRepo.existsByVendorName(dto.getVendorName())) {
                throw new IllegalArgumentException("Email already exists");
            }
        	vendor.setVendorName(dto.getVendorName());
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(vendor.getEmail())) {
            if (vendorRepo.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            vendor.setEmail(dto.getEmail());
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(vendor.getPhoneNumber())) {
            if (vendorRepo.existsByPhoneNumber(dto.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number already exists");
            }
            vendor.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getAccountNumber() != null && !dto.getAccountNumber().equals(vendor.getAccount().getAccountNumber())) {
            if (vendorRepo.existsByAccount_AccountNumber(dto.getAccountNumber())) {
                throw new IllegalArgumentException("Account number already exists");
            }
            vendor.getAccount().setAccountNumber(dto.getAccountNumber());
        }

        if (dto.getIfsc() != null) vendor.getAccount().setIfsc(dto.getIfsc());

        if (dto.getAddress() != null) {
            AddressCreateRequest adr = dto.getAddress();
            vendor.getAddress().setCity(adr.getCity());
            vendor.getAddress().setState(adr.getState());
            vendor.getAddress().setPinCode(adr.getPinCode());
        }

        if (dto.getOrganizationId() != null) {
            Organization org = organizationRepo.findById(dto.getOrganizationId())
                    .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
            vendor.getOrganizations().clear();
            vendor.getOrganizations().add(org);
            org.getVendors().add(vendor);
        }

        Vendor updated = vendorRepo.save(vendor);
        return mapToResponse(updated);
    }

    @Override
    public void deleteVendor(Long id) {
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
        
        for (Organization org : vendor.getOrganizations()) {
            org.getVendors().remove(vendor);
        }
        vendor.getOrganizations().clear();
        
        vendorRepo.delete(vendor);
    }

    @Override
    public VendorPaymentResponse initiatePayment(Long vendorId, BigDecimal amount) {
        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        VendorPayment vp = new VendorPayment();
        vp.setVendor(vendor);
        vp.setAmount(amount);
        vp.setRequest(null); // nullable

        VendorPayment saved = vendorPaymentRepo.save(vp);

        // Map to DTO
        VendorPaymentResponse response = new VendorPaymentResponse();
        response.setVpId(saved.getVpId());
        response.setAmount(saved.getAmount());
        response.setVendorId(vendor.getVendorId());
        response.setVendorName(vendor.getVendorName());
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

        if (!vendor.getOrganizations().isEmpty()) {
            resp.setOrganizationId(vendor.getOrganizations().get(0).getOrganizationId());
        }

        return resp;
    }
}

