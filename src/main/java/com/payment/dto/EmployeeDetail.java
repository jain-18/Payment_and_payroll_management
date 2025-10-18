package com.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class EmployeeDetail {
    private Long employeeId;
    private String employeeName;
    private String email;
    private String employeeRole;
    private String department;
    private BigDecimal salary;
    private LocalDate joinedDate;
    private String accountNumber;
    private String ifsc;
    private String organizationName;
    private boolean isActive;
}
