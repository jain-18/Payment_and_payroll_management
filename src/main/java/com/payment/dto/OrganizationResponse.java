package com.payment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class OrganizationResponse {
    private Long organizationId;
    private String organizationName;
    private String organizationEmail;
    private AddressCreateRequest address;
    private boolean isActive;
}
