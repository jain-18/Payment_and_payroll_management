package com.payment.service;

import com.payment.dto.RegistrationRequest;
import com.payment.dto.RegistrationResponse;

public interface LoginService {
	
	void generateOTPandSendMail(RegistrationRequest request);
	boolean verifyingOTP(String otp);
	RegistrationResponse registerUser();

}
