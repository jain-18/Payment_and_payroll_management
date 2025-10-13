package com.payment.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "raiseConcerns")
public class RaiseConcerns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concernId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empId")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizationId")
    private Organization organization;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slipId")
    private SalaryStructure salaryStructure;

    private LocalDate raiseAt;

    @Column(name = "solved", nullable = false)
    private boolean isSolved = false;
}
