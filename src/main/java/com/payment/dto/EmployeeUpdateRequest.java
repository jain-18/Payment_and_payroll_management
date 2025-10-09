package com.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeUpdateRequest {

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Employee name must contain only alphabets and spaces")
    private String employeeName;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Employee role must contain only alphabets and spaces")
    private String employeeRole;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "email should be appropiate")
    private String email;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Department must contain only alphabets and spaces")
    private String department;

    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private BigDecimal salary;

    @PastOrPresent(message = "Join Date should be present or past")
    private LocalDate joinedDate;

    @Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must be 10–20 digits")
    private String accountNumber;

    private String ifsc;
}
