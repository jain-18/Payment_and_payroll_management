package com.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryStructureResponse {

    private Long slipId;
    private String employeeName;
    private String department;
    private Integer periodMonth;
    private Integer periodYear;
    private LocalDate createdAt;
    private String status;

    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal da;
    private BigDecimal pf;
    private BigDecimal otherAllowances;
    private BigDecimal netSalary;
}
