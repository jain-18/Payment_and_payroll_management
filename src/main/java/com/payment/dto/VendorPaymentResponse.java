package com.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class VendorPaymentResponse {
    private Long vpId;
    private BigDecimal amount;
    private Long vendorId;
    private String vendorName;
    private String status;
}