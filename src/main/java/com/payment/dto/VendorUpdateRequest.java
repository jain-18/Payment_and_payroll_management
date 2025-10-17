package com.payment.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorUpdateRequest {

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Vendor name must contain only alphabets and spaces")
    private String vendorName;

	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "Email should be appropiate")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;
    
    @Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must be 9-18 digits")
    private String accountNumber;

    private String ifsc;

    private AddressCreateRequest address;
}
