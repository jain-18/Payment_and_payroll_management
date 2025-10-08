package com.payment.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.payment.dto.RegistrationRequest;
import com.payment.dto.RegistrationResponse;

@Service
public class LoginServiceImpl implements LoginService{
	
	//private OrganizationRepo organizationRepo;
//	private UserRepo userRepo;
	
	private RegistrationRequest pendingRegistration;
	private String genratedOtp;

	@Override
	public void generateOTPandSendMail(RegistrationRequest request) {
		// TODO Auto-generated method stub
		pendingRegistration = request;
		genratedOtp = String.valueOf(new Random().nextInt(999999));
		
		/*
		 * code of mail
		 * 
		 * 
		 */
		
		System.out.println(genratedOtp);
		
	}

	@Override
	public boolean verifyingOTP(String otp) {
		return otp.equalsIgnoreCase(genratedOtp);
	}

	@Override
	public RegistrationResponse registerUser() {
		/*
		 * Code for registering user
		 * code for registering organization
		 */
		return null;
	}
	
	

}
