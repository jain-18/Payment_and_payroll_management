package com.payment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RegistrationResponse {
    private Long organizationId;
    private String userName;
    private String organizationName;
    private String organizationEmail;
    private AddressCreateRequest address;
    private String accountNo;
    private String ifsc;
}
