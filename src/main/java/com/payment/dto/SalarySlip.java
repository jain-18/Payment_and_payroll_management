package com.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class SalarySlip {
    private Long slipId;
    private String employeeName;
    private String organizationName;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal da;
    private BigDecimal pf;
    private BigDecimal otherAllowances;
    private BigDecimal netSalary;
    private Integer periodMonth;
    private Integer periodYear;
}
