package com.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AllRequest {
    private Long requestId;
    private String requestStatus;
    private String requestType;
    private String createdBy;
    private String to;
    private LocalDate requestDate;
    private BigDecimal totalAmount;
}
