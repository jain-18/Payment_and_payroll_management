package com.payment.controller;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.OtpRequest;
import com.payment.dto.RegistrationRequest;
import com.payment.dto.RegistrationResponse;
import com.payment.service.LoginService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/portal")
public class LoginController {

	private LoginService loginService;

	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> registration(@Valid @ModelAttribute RegistrationRequest request) throws IOException {
		System.out.println("PAN File: " + request.getPancard().getOriginalFilename() + ", size: " + request.getPancard().getSize());
		loginService.generateOTPandSendMail(request);
		return ResponseEntity.ok("sent email to entered email Id");
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<RegistrationResponse> verifyOtp(@RequestBody OtpRequest otpRequest) {
		if (loginService.verifyingOTP(otpRequest.getOtp())) {
			RegistrationResponse response = loginService.registerUser();
			return ResponseEntity.created(URI.create("/portal/register")).body(response);
		} else {
			throw new RuntimeException("Invalid Otp");
		}
	}

}
