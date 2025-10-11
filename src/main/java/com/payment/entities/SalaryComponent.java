package com.payment.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name="salary_components")
public class SalaryComponent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "salary_component_id")
	private Long salaryComponentId;
	
	@NotNull(message = "Basic Salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Basic Salary must be greater than 0")
    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;
	
	@NotNull(message = "HRA cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "HRA must be greater than 0")
    @Column(name = "hra", nullable = false, precision = 12, scale = 2)
    private BigDecimal hra;
	
	@NotNull(message = "DA cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "DA must be greater than 0")
    @Column(name = "da", nullable = false, precision = 12, scale = 2)
    private BigDecimal da;
	
	@NotNull(message = "PF cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "PF must be greater than 0")
    @Column(name = "pf", nullable = false, precision = 12, scale = 2)
    private BigDecimal pf;
	
	@NotNull(message = "Other Allowances cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Other Allowances must be greater than 0")
    @Column(name = "other_allowances", nullable = false, precision = 12, scale = 2)
    private BigDecimal otherAllowances;
	
	@NotNull(message = "Net Salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Net Salary must be greater than 0")
    @Column(name = "net_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary;
}
