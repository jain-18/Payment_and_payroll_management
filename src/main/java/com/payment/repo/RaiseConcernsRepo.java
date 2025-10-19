package com.payment.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.RaiseConcerns;

public interface RaiseConcernsRepo extends JpaRepository<RaiseConcerns, Long> {
    boolean existsByEmployeeEmployeeIdAndOrganizationOrganizationIdAndSalaryStructureSlipId(
            Long employeeId, Long organizationId, Long slipId);

    Page<RaiseConcerns> findByOrganizationOrganizationIdAndEmployeeEmployeeId(
            Long organizationId,
            Long employeeId,
            Pageable pageable);

    Page<RaiseConcerns> findByOrganizationOrganizationId(Long orgId, Pageable pageable);
    
    Page<RaiseConcerns> findByOrganizationOrganizationIdAndIsSolved(Long orgId, boolean isSolved, Pageable pageable);


}
