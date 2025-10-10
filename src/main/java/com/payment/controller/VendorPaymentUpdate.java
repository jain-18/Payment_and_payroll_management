package com.payment.controller;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class VendorPaymentUpdate {
    private Long id;
    private BigDecimal amount;
}
