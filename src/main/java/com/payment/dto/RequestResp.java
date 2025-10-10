package com.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RequestResp {
    private Long requestId;
    private String requestType;
    private String requestStatus;
    private LocalDate requestDate;
    private BigDecimal totalAmount;
    private BigDecimal balance;
    private String createdBy;
    private String rejectReason;

}
