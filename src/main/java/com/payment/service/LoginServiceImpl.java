package com.payment.service;

import java.math.BigDecimal;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	private RegistrationRequest pendingRegistration;
	private String genratedOtp;

	private byte[] pancardBytes;
	private byte[] cancelledChequeBytes;
	private byte[] companyRegistrationCertificateBytes;

	@Override
	public void generateOTPandSendMail(RegistrationRequest request) {
		// TODO Auto-generated method stub
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

		}

		genratedOtp = String.valueOf(new Random().nextInt(999999));

		/*
		 * code of mail
		 * 
		 * 
		 */

		System.out.println("otp is :- " + genratedOtp);

	}

	@Override
	public boolean verifyingOTP(String otp) {
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
			user.setRole(role);
			userRepo.save(user);

			pendingRegistration = null;
			pancardBytes = null;
			cancelledChequeBytes = null;
			companyRegistrationCertificateBytes = null;

			RegistrationResponse response = modelMapper.map(pendingRegistration, RegistrationResponse.class);
			response.setOrganizationId(organization.getOrganizationId());
			return response;

		} catch (Exception e) {
			// throw new RuntimeException("upload failed");
			System.out.println("upload fail");
		}
		return null;
	}

}
