package com.payment.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.entities.SalaryStructure;

@Repository
public interface SalaryStructureRepo extends JpaRepository<SalaryStructure, Long> {

    Optional<SalaryStructure> findByEmployee_EmployeeIdAndPeriodMonthAndPeriodYear(Long employeeId, Integer month, Integer year);
}
