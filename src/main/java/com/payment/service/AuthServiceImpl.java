package com.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.payment.dto.LoginDto;
import com.payment.exception.UserApiException;
import com.payment.security.JwtTokenProvider;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Override
	public String employeeLogin(LoginDto loginDto) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.generateTokenForEmployee(authentication);

			return token;
		} catch (BadCredentialsException e) {
			throw new UserApiException(HttpStatus.NOT_FOUND, "Username or Password is incorrect");
		}
	}
	
	@Override
	public String organizationLogin(LoginDto loginDto) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.generateTokenForOrganization(authentication);

			return token;
		} catch (BadCredentialsException e) {
			throw new UserApiException(HttpStatus.NOT_FOUND, "Username or Password is incorrect");
		}
	}
	
	@Override
	public String adminLogin(LoginDto loginDto) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.generateTokenForAdmin(authentication);

			return token;
		} catch (BadCredentialsException e) {
			throw new UserApiException(HttpStatus.NOT_FOUND, "Username or Password is incorrect");
		}
	}
}
