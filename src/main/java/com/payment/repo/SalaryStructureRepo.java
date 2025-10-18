package com.payment.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.entities.Request;
import com.payment.entities.SalaryStructure;

@Repository
public interface SalaryStructureRepo extends JpaRepository<SalaryStructure, Long> {

        List<SalaryStructure> findAllByOrganizationOrganizationId(Long organizationId);

        Optional<SalaryStructure> findByEmployee_EmployeeIdAndPeriodMonthAndPeriodYear(Long employeeId, Integer month,
                        Integer year);

        List<SalaryStructure> findByRequest(Request request);

        Page<SalaryStructure> findByOrganizationOrganizationId(Long orgId, Pageable pageable);

        Page<SalaryStructure> findByOrganizationOrganizationIdAndStatusIgnoreCase(Long orgId, String status,
                        Pageable pageable);

        Optional<SalaryStructure> findByOrganizationOrganizationIdAndEmployeeEmployeeIdAndPeriodMonthAndPeriodYear(
                        Long organizationId,
                        Long employeeId,
                        Integer periodMonth,
                        Integer periodYear);

        Optional<SalaryStructure> findByOrganizationOrganizationIdAndEmployeeEmployeeIdAndPeriodMonthAndPeriodYear(
                        Long orgId, Long empId, int month, int year);

        List<SalaryStructure> findByOrganizationOrganizationIdAndEmployeeEmployeeId(
                        Long orgId, Long empId);

        Page<SalaryStructure> findByOrganizationOrganizationIdAndEmployeeEmployeeId(
                        Long orgId, Long empId, Pageable pageable);

        List<SalaryStructure> findByOrganizationOrganizationIdAndEmployeeEmployeeIdAndPeriodYear(
                        Long orgId, Long empId, int year);

        Page<SalaryStructure> findByOrganizationOrganizationIdAndEmployeeEmployeeIdAndPeriodYear(
                        Long orgId, Long empId, int year, Pageable pageable);

}
