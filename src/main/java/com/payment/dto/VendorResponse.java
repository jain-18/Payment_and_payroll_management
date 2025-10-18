package com.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorResponse {

	@NotBlank(message = "Vendor Id cannot be blank")
    private Long vendorId;
	
	@NotBlank(message = "Vendor name cannot be blank")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Vendor name must contain only alphabets and spaces")
    private String vendorName;
	
	@NotBlank(message = "Email cannot be blank")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "Email should be appropiate")
    private String email;
	
	@NotBlank(message = "Phone number cannot be blank")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;
	
	@NotBlank(message = "Account number cannot be blank")
    @Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must be 9-18 digits")
    private String accountNumber;
	
	@NotBlank(message = "IFSC code cannot be blank")
    private String ifsc;
	
	@NotNull(message = "Organization ID cannot be null")
    private Long organizationId;
	
	@NotNull(message = "Address cannot be null")
    private AddressCreateRequest address;
	
	@NotBlank(message = "Vendor Status cannot be blank")
    private boolean isActive;
}