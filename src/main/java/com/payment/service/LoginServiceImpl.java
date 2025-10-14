package com.payment.service;

import java.math.BigDecimal;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.dto.RegistrationRequest;
import com.payment.dto.RegistrationResponse;
import com.payment.entities.Account;
import com.payment.entities.Address;
import com.payment.entities.Document;
import com.payment.entities.Organization;
import com.payment.entities.Role;
import com.payment.entities.User;
import com.payment.repo.OrganizationRepo;
import com.payment.repo.RoleRepo;
import com.payment.repo.UserRepo;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

	@Autowired
	private OrganizationRepo organizationRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private EmailService emailService;
	
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public LoginServiceImpl(PasswordEncoder passwordEncoder) {
	    this.passwordEncoder = passwordEncoder;
	}

	private RegistrationRequest pendingRegistration;
	private String genratedOtp;

	private byte[] pancardBytes;
	private byte[] cancelledChequeBytes;
	private byte[] companyRegistrationCertificateBytes;

	@Override
	public void generateOTPandSendMail(RegistrationRequest request) {
		// TODO Auto-generated method stub
		
		if (organizationRepo.existsByUsers_UserName(request.getUserName())) {
	        throw new IllegalArgumentException("Username is already taken. Please choose another one.");
	    }
	    if (organizationRepo.existsByOrganizationName(request.getOrganizationName())) {
	        throw new IllegalArgumentException("Organization name already exists.");
	    }
	    if (organizationRepo.existsByOrganizationEmail(request.getOrganizationEmail())) {
	        throw new IllegalArgumentException("Email is already registered.");
	    }
	    if (organizationRepo.existsByAccount_AccountNumber(request.getAccountNo())) {
	        throw new IllegalArgumentException("Account number is already linked to another organization.");
	    }
		
		pendingRegistration = request;

		// Store files as byte[]
		try {
			if (!request.getPancard().isEmpty()) {
				pancardBytes = request.getPancard().getBytes();
			}
			if (!request.getCancelledCheque().isEmpty()) {
				cancelledChequeBytes = request.getCancelledCheque().getBytes();
			}
			if (!request.getCompanyRegistrationCertificate().isEmpty()) {
				companyRegistrationCertificateBytes = request.getCompanyRegistrationCertificate().getBytes();
			}
		} catch (Exception e) {
			throw new IllegalStateException("Failed to process uploaded files: " + e.getMessage(), e);
		}

		genratedOtp = String.valueOf(new Random().nextInt(999999));

		emailService.sendOtpEmail(request.getOrganizationEmail(), genratedOtp);

		System.out.println("otp is :- " + genratedOtp);

	}

	@Override
	public boolean verifyingOTP(String otp) {
		if (genratedOtp == null) {
	        throw new IllegalStateException("OTP not generated. Please initiate registration first.");
	    }
		return otp.equalsIgnoreCase(genratedOtp);
	}

	@Override
	public RegistrationResponse registerUser() {
			
		Organization organization = modelMapper.map(pendingRegistration, Organization.class);
		organization.setActive(false);

		Account account = new Account();
		account.setAccountNumber(pendingRegistration.getAccountNo());
		account.setAccountType("BUSSINESS_ACCOUNT");
		account.setIfsc(pendingRegistration.getIfsc());
		account.setBalance(new BigDecimal(1000000));
		organization.setAccount(account);

		Address address = modelMapper.map(pendingRegistration.getAddress(), Address.class);
		organization.setAddress(address);

		try {
			Document document = new Document();
			document.setPanUrl(
					cloudinaryService.uploadFile(pancardBytes, pendingRegistration.getPancard().getOriginalFilename()));
			document.setCancelledCheque(cloudinaryService.uploadFile(cancelledChequeBytes,
					pendingRegistration.getCancelledCheque().getOriginalFilename()));
			document.setCompanyRegistrationCertificate(
					cloudinaryService.uploadFile(companyRegistrationCertificateBytes,
							pendingRegistration.getCompanyRegistrationCertificate().getOriginalFilename()));

			organization.setDocument(document);
			System.out.println(organization.getDocument().getPanUrl());
			System.out.println(organization.getDocument().getCancelledCheque());
			System.out.println(organization.getDocument().getCompanyRegistrationCertificate());

			organization = organizationRepo.save(organization);

			User user = modelMapper.map(pendingRegistration, User.class);
			user.setActive(false);
			user.setEmployee(null);
			user.setOrganization(organization);
			
			Role role = roleRepo.findByRoleName("ROLE_ORGANIZATION");
			if (role == null) {
                throw new IllegalStateException("Role 'ROLE_ORGANIZATION' not found in database.");
            }
			user.setRole(role);
			user.setPassword(passwordEncoder.encode(pendingRegistration.getPassword()));
			userRepo.save(user);
			
			RegistrationResponse response = modelMapper.map(pendingRegistration, RegistrationResponse.class);
			response.setOrganizationId(organization.getOrganizationId());

			pendingRegistration = null;
			pancardBytes = null;
			cancelledChequeBytes = null;
			companyRegistrationCertificateBytes = null;

			
			return response;

		} catch (Exception e) {
			throw new IllegalStateException("Registration failed during document upload: " + e.getMessage());
			// System.out.println("upload fail");
		}
		// return null;
	}

}
