package com.payment.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.payment.dto.VendorRequestRes;
import com.payment.dto.RequestReasonDto;
import com.payment.dto.RequestResp;
import com.payment.dto.SalaryRequestRes;
import com.payment.entities.Account;
import com.payment.entities.Employee;
import com.payment.entities.Organization;
import com.payment.entities.Request;
import com.payment.entities.SalaryStructure;
import com.payment.entities.VendorPayment;
import com.payment.exception.ResourceNotFoundException;
import com.payment.repo.AccountRepo;
import com.payment.repo.RequestRepo;
import com.payment.repo.SalaryStructureRepo;
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

    @Autowired
    private SalaryStructureRepo salaryStructureRepo;
    
    @Autowired
    private EmailService emailService;

    @Override
    public Page<VendorRequestRes> getALLVendorRequestByStatus(PageRequest pageable, String status, String requestType) {
        Page<Request> request = requestRepo.findByRequestStatusAndRequestTypeIgnoreCase(status,
                requestType, pageable);
        return request.map(req -> {
            VendorRequestRes res = new VendorRequestRes();
            res.setRequestId(req.getRequestId());
            res.setRequestStatus(req.getRequestStatus());
            res.setRequestDate(req.getRequestDate());
            res.setCreatedBy(req.getCreatedBy());
            res.setTotalAmount(req.getTotalAmount());
            res.setRequestType(req.getRequestType());

            VendorPayment vp = vendorPaymentRepo.findByRequest(req);
            if (vp != null && vp.getVendor() != null) {
                res.setTo(vp.getVendor().getVendorName());
            }
            return res;
        });
    }

    @Override
    public Page<SalaryRequestRes> getALLSalaryRequestByStatus(PageRequest pageable, String status, String requestType) {
        Page<Request> request = requestRepo.findByRequestStatusAndRequestTypeIgnoreCase(status,
                requestType, pageable);

        return request.map(req -> {
            SalaryRequestRes res = new SalaryRequestRes();
            res.setRequestId(req.getRequestId());
            res.setRequestStatus(req.getRequestStatus());
            res.setRequestDate(req.getRequestDate());
            res.setCreatedBy(req.getCreatedBy());
            res.setTotalAmount(req.getTotalAmount());
            res.setRequestType(req.getRequestType());
            return res;
        });
    }

    @Override
    public RequestResp getSingleRequest(Long requestId) {
        // 1. Fetch the request from DB
        Request request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));

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
        resp.setActionDate(request.getActionDate());
        resp.setRejectReason(request.getRejectReason());

        return resp;
    }

    @Override
    @Transactional
    public RequestResp vendorRequestApproved(Long requestId) {
        // 1. Fetch the Request
        Request request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));

        if (request.getActionDate() != null) {
            throw new IllegalStateException("Request already responded");
        }

        // 2. Update Request fields
        request.setRequestStatus("APPROVED");
        request.setActionDate(LocalDate.now());
        request.setRejectReason(null);
        Request updatedRequest = requestRepo.save(request);

        // 3. Fetch the VendorPayment linked with this Request
        VendorPayment vendorPayment = vendorPaymentRepo.findByRequest(request);

        if (vendorPayment == null) {
            throw new ResourceNotFoundException("No VendorPayment found for this Request");
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
        
     // Send email to Organization
        String orgEmail = org.getOrganizationEmail();
        if (orgEmail != null && !orgEmail.isBlank()) {
            String subject = "Vendor Payment Approved";
            String body = "Dear " + org.getOrganizationName() + ",\n\n"
                    + "The vendor payment request (ID: " + request.getRequestId() + ") has been approved.\n"
                    + "Amount: " + request.getTotalAmount() + "\n\nRegards,\nPaymentApp Team";
            emailService.sendCustomEmail(orgEmail, subject, body);
        }

        // Send email to Vendor
        if (vendorPayment.getVendor() != null && vendorPayment.getVendor().getEmail() != null) {
            String vendorEmail = vendorPayment.getVendor().getEmail();
            String subject = "Payment Received";
            String body = "Dear " + vendorPayment.getVendor().getVendorName() + ",\n\n"
                    + "The payment request (ID: " + request.getRequestId() + ") by " + org.getOrganizationName() + "has been approved and paid.\n"
                    + "Amount: " + request.getTotalAmount() + "\n\nRegards,\nPaymentApp Team";
            emailService.sendCustomEmail(vendorEmail, subject, body);
        }

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
        resp.setActionDate(updatedRequest.getActionDate());

        return resp;
    }

    @Override
    @Transactional
    public RequestResp vendorRequestReject(RequestReasonDto dto) {
        // 1. Fetch the Request
        Request request = requestRepo.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + dto.getId()));

        if (request.getActionDate() != null) {
            throw new IllegalStateException("Request already responded");
        }

        // 2. Update Request fields
        request.setRequestStatus("REJECTED");
        request.setRejectReason(dto.getRejectReason());
        request.setActionDate(LocalDate.now());
        Request updatedRequest = requestRepo.save(request);

        // 3. Fetch the VendorPayment linked with this Request
        VendorPayment vendorPayment = vendorPaymentRepo.findByRequest(request);
        if (vendorPayment == null) {
            throw new ResourceNotFoundException("No VendorPayment found for this Request");
        }

        // 4. Update VendorPayment status to REJECTED
        vendorPayment.setStatus("REJECTED");
        vendorPaymentRepo.save(vendorPayment);

        Organization org = request.getOrganization();
        if (org != null && org.getOrganizationEmail() != null) {
            String subject = "Vendor Payment Rejected";
            String body = "Dear " + org.getOrganizationName() + ",\n\n"
                    + "The vendor payment request (ID: " + request.getRequestId() + ") has been rejected.\n"
                    + "Reason: " + request.getRejectReason() + "\n\nRegards,\nPaymentApp Team";
            emailService.sendCustomEmail(org.getOrganizationEmail(), subject, body);
        }
        
        // 5. Prepare and return response
        RequestResp resp = new RequestResp();
        resp.setRequestId(updatedRequest.getRequestId());
        resp.setRequestType(updatedRequest.getRequestType());
        resp.setRequestStatus(updatedRequest.getRequestStatus());
        resp.setRequestDate(updatedRequest.getRequestDate());
        resp.setTotalAmount(updatedRequest.getTotalAmount());
        resp.setCreatedBy(updatedRequest.getCreatedBy());
        resp.setRejectReason(updatedRequest.getRejectReason());
        resp.setActionDate(updatedRequest.getActionDate());

        // Include balance if needed
        if (updatedRequest.getOrganization() != null && updatedRequest.getOrganization().getAccount() != null) {
            resp.setBalance(updatedRequest.getOrganization().getAccount().getBalance());
        }

        return resp;
    }

    @Override
    @Transactional
    public RequestResp approveSalaryRequest(Long requestId) {
        Request request = requestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));

        if ("APPROVED".equalsIgnoreCase(request.getRequestStatus()) ||
                "REJECTED".equalsIgnoreCase(request.getRequestStatus())) {
            throw new RuntimeException("Request already responded");
        }

        Organization org = request.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Request is not linked to any organization");
        }

        Account account = org.getAccount();
        System.out.println(account.getBalance());
        System.out.println(request.getTotalAmount());
        if (account.getBalance().compareTo(request.getTotalAmount()) < 0) {
            throw new IllegalStateException("Insufficient organization balance");
        }

        // Deduct amount
        account.setBalance(account.getBalance().subtract(request.getTotalAmount()));
        accountRepo.save(account);

        // Approve request
        request.setRequestStatus("APPROVED");
        request.setActionDate(LocalDate.now());
        requestRepo.save(request);

        // Update all linked SalaryStructures
        List<SalaryStructure> structures = salaryStructureRepo.findByRequest(request);
        for (SalaryStructure s : structures) {
            s.setStatus("PAID");
        }
        salaryStructureRepo.saveAll(structures);

        String orgEmail = org.getOrganizationEmail();
        if (orgEmail != null && !orgEmail.isBlank()) {
            String subject = "Salary Request Approved";
            String body = "Dear " + org.getOrganizationName() + ",\n\n"
                    + "The salary request (ID: " + request.getRequestId() + ") has been approved.\n"
                    + "Total Amount: " + request.getTotalAmount() + "\n\nRegards,\nPaymentApp Team";
            emailService.sendCustomEmail(orgEmail, subject, body);
        }
        
        // for emailing employees
        for (SalaryStructure s : structures) {
            Employee emp = s.getEmployee();
            if (emp != null && emp.getEmail() != null) {
                String subject = "Salary Credited";
                String body = "Dear " + emp.getEmployeeName() + ",\n\n"
                        + "Your salary for " + s.getPeriodMonth() + "/" + s.getPeriodYear() 
                        + " has been credited.\n"
                        + "Amount: " + s.getSalaryComponent().getNetSalary() + "\n\nRegards,\nPaymentApp Team";
                emailService.sendCustomEmail(emp.getEmail(), subject, body);
            }
        }

        // Prepare response
        RequestResp resp = new RequestResp();
        resp.setRequestId(request.getRequestId());
        resp.setRequestType(request.getRequestType());
        resp.setRequestStatus(request.getRequestStatus());
        resp.setRequestDate(request.getRequestDate());
        resp.setTotalAmount(request.getTotalAmount());
        resp.setBalance(account.getBalance());
        resp.setCreatedBy(request.getCreatedBy());
        resp.setRejectReason(request.getRejectReason());
        return resp;
    }

    @Override
    @Transactional
    public RequestResp rejectSalaryRequest(RequestReasonDto dto) {
        Request request = requestRepo.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + dto.getId()));

        if ("APPROVED".equalsIgnoreCase(request.getRequestStatus()) ||
                "REJECTED".equalsIgnoreCase(request.getRequestStatus())) {
            throw new RuntimeException("Request already responded");
        }

        request.setRequestStatus("REJECTED");
        request.setRejectReason(dto.getRejectReason());
        request.setActionDate(LocalDate.now());
        requestRepo.save(request);

        // Reset salary structures
        List<SalaryStructure> structures = salaryStructureRepo.findByRequest(request);
        for (SalaryStructure s : structures) {
            s.setStatus("REJECTED");
            s.setRequest(request);
        }
        salaryStructureRepo.saveAll(structures);

        Organization org = request.getOrganization();
        if (org != null && org.getOrganizationEmail() != null) {
            String subject = "Salary Request Rejected";
            String body = "Dear " + org.getOrganizationName() + ",\n\n"
                    + "The salary request (ID: " + request.getRequestId() + ") has been rejected.\n"
                    + "Reason: " + request.getRejectReason() + "\n\nRegards,\nPaymentApp Team";
            emailService.sendCustomEmail(org.getOrganizationEmail(), subject, body);
        }
        
        RequestResp resp = new RequestResp();
        resp.setRequestId(request.getRequestId());
        resp.setRequestType(request.getRequestType());
        resp.setRequestStatus(request.getRequestStatus());
        resp.setRequestDate(request.getRequestDate());
        resp.setTotalAmount(request.getTotalAmount());
        resp.setCreatedBy(request.getCreatedBy());
        resp.setRejectReason(request.getRejectReason());
        return resp;
    }

}
