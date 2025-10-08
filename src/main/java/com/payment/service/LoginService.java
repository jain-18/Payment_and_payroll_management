package com.payment.service;

import java.io.IOException;

import com.payment.dto.RegistrationRequest;
import com.payment.dto.RegistrationResponse;

public interface LoginService {
	
	void generateOTPandSendMail(RegistrationRequest request) throws IOException;
	boolean verifyingOTP(String otp);
	RegistrationResponse registerUser();

}
