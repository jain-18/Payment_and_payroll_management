package com.payment.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name="employees")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "employee_id")
	private Long employeeId;
	
	@NotBlank(message = "Employee name cannot be blank")
	@Pattern(regexp = "^[A-Za-z ]+$", message = "Employee name must contain only alphabets and spaces")
	@Column(name = "employee_name", length = 50)
	private String employeeName;
	
	@NotBlank(message = "Employee role cannot be blank")
	@Pattern(regexp = "^[A-Za-z ]+$", message = "Employee role must contain only alphabets and spaces")
	@Column(name = "employee_role", length = 50)
	private String employeeRole;
	
	@NotBlank(message = "email cannot be blank")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "email should be appropiate")
	@Column(name = "employee_email",unique = true)
	private String email;
	
	@NotBlank(message = "Department cannot be blank")
	@Column(name = "department")
	@Pattern(regexp = "^[A-Za-z ]+$", message = "Department must contain only alphabets and spaces")
	private String department;
	
	@NotNull(message = "Salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    @Column(name = "salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal salary;
	
	@Column(name = "joined_date", nullable = false)
	@PastOrPresent(message = "Join Date should be present or past")
	private LocalDate joinedDate;
	
	@Column(name = "isActive", nullable = false)
	private boolean isActive;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	private Organization organization;
	
	@OneToOne(cascade = {CascadeType.ALL},fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	private Account account;
	
	@OneToMany(mappedBy="employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SalaryStructure> salaryStructure;
}
