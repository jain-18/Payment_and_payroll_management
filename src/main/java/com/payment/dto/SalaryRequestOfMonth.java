package com.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SalaryRequestOfMonth {
    private Long slipId;
    private Long employeeId;
    private BigDecimal salary;
    private String Employee;
    private String status;
    private Integer periodMonth;
    private Integer periodYear;

}
