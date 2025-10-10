package com.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PendingVendorRes {
    private Long requestId;
    private String requestStatus;
    private LocalDate requestDate;
    private String createdBy;
    private String to;
    private BigDecimal totalAmount;

}
