package com.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class AccountDto {
    private String accountNumber;
	private String accountType;
	private String ifsc;
	private BigDecimal balance;
}
