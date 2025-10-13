package com.payment.service;

import com.payment.dto.LoginDto;

public interface AuthService {

	String employeeLogin(LoginDto loginDto);
	String organizationLogin(LoginDto loginDto);
	String adminLogin(LoginDto loginDto);
}
