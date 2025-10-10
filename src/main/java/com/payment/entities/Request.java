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
import jakarta.persistence.PrePersist;
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
@Table(name="requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @NotBlank(message = "Request type cannot be blank")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]*$", 
             message = "Request type must start with a letter and may contain letters, digits, and spaces")
    @Column(name = "request_type", length = 50)
    private String requestType;

    @NotBlank(message = "Request status cannot be blank")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]*$", 
             message = "Request status must start with a letter and may contain letters, digits, and spaces")
    @Column(name = "request_status", length = 50)
    private String requestStatus;

    @Pattern(regexp = "^[A-Za-z0-9 .,]*$", 
             message = "Description may contain letters, digits, spaces, dots, and commas")
    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "request_date", nullable = false)
    @PastOrPresent(message = "Request date should be present or past")
    private LocalDate requestDate;

    @Column(name = "action_date", nullable = true)
    @PastOrPresent(message = "Action date should be present or past")
    private LocalDate actionDate;

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @NotBlank(message = "CreatedBy cannot be blank")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]*$", 
             message = "CreatedBy must start with a letter and may include letters, digits, and spaces")
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]*$", 
             message = "RejectedBy must start with a letter and may include letters, digits, and spaces")
    @Column(name = "rejected_by", length = 50)
    private String rejectedBy;

    @Pattern(regexp = "^[A-Za-z0-9 .,]*$", 
             message = "Reject reason may contain letters, digits, spaces, dots, and commas")
    @Column(name = "reject_reason", length = 100)
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(name = "period_month", nullable = false)
    private Integer periodMonth;

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @PrePersist
    public void prePersist() {
        if (periodMonth == null) {
            periodMonth = LocalDate.now().getMonthValue();
        }
        if (periodYear == null) {
            periodYear = LocalDate.now().getYear();
        }
        if (requestDate == null) {
            requestDate = LocalDate.now();
        }
    }
    
    @OneToMany(mappedBy="request")
	private List<SalaryStructure> salaryStructure;
}
