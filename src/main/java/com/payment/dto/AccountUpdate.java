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
	@Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must be 9-18 digits")
	private String accountNumber;
	
	@NotBlank(message = "IFSC code cannot be null")
	private String ifsc;
	
}
