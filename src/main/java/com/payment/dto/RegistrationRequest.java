package com.payment.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class RegistrationRequest {

	@NotBlank(message = "username should not be Blank")
	private String userName;

	@NotBlank(message = "password Should not be blank")
	private String password;

	@NotBlank(message = "organization name cannot be blank")
	@Size(max = 50)
	@Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]*$", message = "Organization name must start with a letter and may include digits and spaces")
	private String organizationName;

	@NotBlank(message = "email cannot be blank")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email should be appropiate")
	private String organizationEmail;

	@Valid
	private AddressCreateRequest address;

	@NotBlank(message = "account no cannot be blank")
	@Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must be 9-18 digits")
	private String accountNo;

	@Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",message = "Invalid IFSC code format")
	@NotBlank(message = "ifsc should not be blank")
	private String ifsc;

	@NotNull(message = "PAN card document is required")
	private MultipartFile pancard;

	@NotNull(message = "Cancelled cheque document is required")
	private MultipartFile cancelledCheque;

	@NotNull(message = "Company registration certificate is required")
	private MultipartFile companyRegistrationCertificate;

}
