package com.payment.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.payment.dto.PendingVendorRes;
import com.payment.dto.RequestResp;
import com.payment.entities.Account;
import com.payment.entities.Organization;
import com.payment.entities.Request;
import com.payment.entities.VendorPayment;
import com.payment.repo.AccountRepo;
import com.payment.repo.RequestRepo;
import com.payment.repo.VendorPaymentRepo;

import jakarta.transaction.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private RequestRepo requestRepo;

    @Autowired
    private VendorPaymentRepo vendorPaymentRepo;

    @Autowired
    private AccountRepo accountRepo;


    @Override
    public Page<PendingVendorRes> getAllPendingVendorRequest(Pageable pageable) {
        Page<Request> pendingRequests = requestRepo.findByRequestStatus("PENDING", pageable);

        return pendingRequests.map(req -> {
            PendingVendorRes res = new PendingVendorRes();
            res.setRequestId(req.getRequestId());
            res.setRequestStatus(req.getRequestStatus());
            res.setRequestDate(req.getRequestDate());
            res.setCreatedBy(req.getCreatedBy());

            // Get vendor name from VendorPayment
            VendorPayment vp = vendorPaymentRepo.findByRequest(req);
            if (vp != null && vp.getVendor() != null) {
                res.setTo(vp.getVendor().getVendorName());
            }

            res.setTotalAmount(req.getTotalAmount());
            return res;
        });
    }

    @Override
    public RequestResp getSingleRequest(Long requestId) {
        // 1. Fetch the request from DB
        Request request = requestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));

        // 2. Get the organization
        Organization org = request.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Request is not associated with any organization");
        }

        // 3. Get the organization's account to fetch balance
        Account account = org.getAccount(); // Assuming Organization has a OneToOne with Account
        BigDecimal balance = account != null ? account.getBalance() : BigDecimal.ZERO;

        // 4. Map to Response DTO
        RequestResp resp = new RequestResp();
        resp.setRequestId(request.getRequestId());
        resp.setRequestType(request.getRequestType());
        resp.setRequestStatus(request.getRequestStatus());
        resp.setRequestDate(request.getRequestDate());
        resp.setTotalAmount(request.getTotalAmount());
        resp.setBalance(balance);
        resp.setCreatedBy(request.getCreatedBy());
        resp.setRejectReason(request.getRejectReason());

        return resp;
    }

    @Override
    @Transactional
    public RequestResp vendorRequestApproved(Long requestId) {
        // 1. Fetch the Request
        Request request = requestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));

        // 2. Update Request fields
        request.setRequestStatus("APPROVED");
        request.setActionDate(LocalDate.now());
        Request updatedRequest = requestRepo.save(request);

        // 3. Fetch the VendorPayment linked with this Request
        VendorPayment vendorPayment = vendorPaymentRepo.findByRequest(request);

        if (vendorPayment == null) {
            throw new IllegalArgumentException("No VendorPayment found for this Request");
        }
        // 4. Update VendorPayment status to PAID
        vendorPayment.setStatus("PAID");
        vendorPaymentRepo.save(vendorPayment);

        // 5. Deduct amount from organization's account balance
        Organization org = request.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Request is not linked to any organization");
        }

        Account account = org.getAccount();
        if (account == null) {
            throw new IllegalStateException("Organization does not have an account");
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getTotalAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient organization balance to approve this request");
        }
        account.setBalance(newBalance);
        accountRepo.save(account);

        // 6. Prepare and return response
        RequestResp resp = new RequestResp();
        resp.setRequestId(updatedRequest.getRequestId());
        resp.setRequestType(updatedRequest.getRequestType());
        resp.setRequestStatus(updatedRequest.getRequestStatus());
        resp.setRequestDate(updatedRequest.getRequestDate());
        resp.setTotalAmount(updatedRequest.getTotalAmount());
        resp.setBalance(account.getBalance());
        resp.setCreatedBy(updatedRequest.getCreatedBy());
        resp.setRejectReason(updatedRequest.getRejectReason());

        return resp;
    }

}
