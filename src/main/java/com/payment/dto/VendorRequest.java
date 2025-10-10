package com.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorRequest {

	@NotBlank(message = "Vendor name cannot be blank")
	@Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]*$", message = "Organization name must start with a letter and may include digits and spaces")
    private String vendorName;

	@NotBlank(message = "Email cannot be blank")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "Email should be appropiate")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;
    
    @NotBlank(message = "Account number cannot be blank")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Account number must be 10-20 digits")
    private String accountNumber;

    @NotBlank(message = "IFSC code cannot be blank")
    private String ifsc;

    @NotNull(message = "Address cannot be null")
    private AddressCreateRequest address;
}
