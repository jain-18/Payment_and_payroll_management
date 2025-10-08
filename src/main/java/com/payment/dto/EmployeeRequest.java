package com.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequest {

    @NotBlank(message = "Employee name cannot be blank")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Employee name must contain only alphabets and spaces")
    private String employeeName;

    @NotBlank(message = "Employee role cannot be blank")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Employee role must contain only alphabets and spaces")
    private String employeeRole;

    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "email should be appropiate")
    private String email;

    @NotBlank(message = "Department cannot be blank")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Department must contain only alphabets and spaces")
    private String department;

    @NotNull(message = "Salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private BigDecimal salary;

    @NotNull(message = "Joined Date cannot be null")
    @PastOrPresent(message = "Join Date should be present or past")
    private LocalDate joinedDate;

    @NotBlank(message = "Account number cannot be blank")
    @Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must be 10–20 digits")
    private String accountNumber;

    @NotBlank(message = "IFSC code cannot be blank")
    private String ifsc;
}
