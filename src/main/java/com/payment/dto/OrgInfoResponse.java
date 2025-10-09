package com.payment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class OrgInfoResponse {
    private Long organizationId;
    private String organizationName;
    private String organizationEmail;
    private AddressCreateRequest address;
    private AccountDto account;
    private DocumentDto document;
    private boolean isActive;
}
