package com.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AccountUpdate {

    @NotBlank(message = "account no cannot be blank")
	@Pattern(regexp = "^[0-9]{10,20}$", message = "Account number must be 10-20 digits")
	private String accountNumber;
	
	@NotBlank(message = "IFSC code cannot be null")
	private String ifsc;
	
}
