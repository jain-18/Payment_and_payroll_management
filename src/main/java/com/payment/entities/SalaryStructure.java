package com.payment.entities;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name="salary_structures")
public class SalaryStructure {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "slip_id")
	private Long slipId;
	
	@NotBlank(message = "Status cannot be blank")
	@Pattern(regexp = "^[A-Za-z ]+$", message = "Status must contain only alphabets and spaces")
	@Column(name = "status", length = 50)
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", nullable=false)
	private Organization organization;
	
	@OneToOne(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
	@JoinColumn(name="salary_component_id", nullable=false)
	private SalaryComponent salaryComponent;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id", nullable=true)
	private Request request;
	
	@Column(name = "created_at", nullable = false)
	@PastOrPresent(message = "Created At Date should be present or past")
	private LocalDate createdAt;
	
	@Column(name = "period_month", nullable = false)
    private Integer periodMonth;

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;
    
    @PrePersist
    public void prePersist() {
        if (periodMonth == null) {
            periodMonth = LocalDate.now().getMonthValue(); // e.g. 10 for October
        }
        if (periodYear == null) {
            periodYear = LocalDate.now().getYear(); // e.g. 2025
        }
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
    }
}
