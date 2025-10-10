package com.payment.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class OrganizationUpdateRequest {

    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9]*$", message = "organization name must start with alphabet and may include digits")
    private String organizationName;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email should be appropriate")
    private String organizationEmail;

    @Valid
    private AddressCreateRequest address;

    @Valid
    private AccountUpdate account;

    private MultipartFile pancard;
    private MultipartFile cancelledCheque;
    private MultipartFile companyRegistrationCertificate;

}
