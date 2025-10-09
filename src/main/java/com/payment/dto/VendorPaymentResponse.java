package com.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorPaymentResponse {
    private Long vpId;
    private BigDecimal amount;
    private Long vendorId;
    private String vendorName;
}