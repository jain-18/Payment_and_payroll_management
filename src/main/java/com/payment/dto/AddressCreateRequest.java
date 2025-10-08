package com.payment.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AddressCreateRequest {
	
	@Size(min = 2)
	@NotBlank(message = "City should not be blank")
	private String city;
	
	@Size(min = 2)
	@NotBlank(message = "State should not be blank")
	private String state;
	
	@NotBlank(message = "Pincode should not be blank")
	@Column(name = "pin_code")
	@Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be exactly 6 digits")
	private String pinCode;

}
