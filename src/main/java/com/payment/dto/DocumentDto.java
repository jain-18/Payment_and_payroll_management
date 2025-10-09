package com.payment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class DocumentDto {

    private String panUrl;
	private String cancelledCheque;
	private String companyRegistrationCertificate;
}
