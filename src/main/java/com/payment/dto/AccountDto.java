package com.payment.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class AccountDto {

    @NotBlank(message = "account no cannot be blank")
	@Pattern(regexp = "^[0-9]{10,20}$", message = "Account number must be 10-20 digits")
	private String accountNumber;
	
	@NotBlank(message = "Account type should not be null")
	private String accountType;
	
	@NotBlank(message = "IFSC code cannot be null")
	private String ifsc;
	
	@Min(value = 0)
	private BigDecimal balance;
}
