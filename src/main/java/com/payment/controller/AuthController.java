package com.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.JwtAuthResponse;
import com.payment.dto.LoginDto;
import com.payment.service.AuthService;

@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/login")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@PostMapping("/employee")
	public ResponseEntity<JwtAuthResponse> employeeLogin(@RequestBody LoginDto loginDto)
	{
		
		String token=authService.employeeLogin(loginDto);
		JwtAuthResponse jwtAuthResponse=new JwtAuthResponse();
		jwtAuthResponse.setAccessToken(token);
		
		return ResponseEntity.ok(jwtAuthResponse);
	}
	
	@PostMapping("/organization")
	public ResponseEntity<JwtAuthResponse> organizationLogin(@RequestBody LoginDto loginDto)
	{
		
		String token=authService.organizationLogin(loginDto);
		JwtAuthResponse jwtAuthResponse=new JwtAuthResponse();
		jwtAuthResponse.setAccessToken(token);
		
		return ResponseEntity.ok(jwtAuthResponse);
	}
	
	@PostMapping("/admin")
	public ResponseEntity<JwtAuthResponse> adminLogin(@RequestBody LoginDto loginDto)
	{
		
		String token=authService.adminLogin(loginDto);
		JwtAuthResponse jwtAuthResponse=new JwtAuthResponse();
		jwtAuthResponse.setAccessToken(token);
		
		return ResponseEntity.ok(jwtAuthResponse);
	}
}
