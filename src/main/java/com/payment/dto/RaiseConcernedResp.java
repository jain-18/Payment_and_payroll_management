package com.payment.dto;

import com.payment.entities.Employee;
import com.payment.entities.SalaryStructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RaiseConcernedResp {
    private Long concernId;
    private Long employeeId;
    private Long slipId;
    private String organizationName;
    private String raiseAt;
    private boolean isSolved;
}
