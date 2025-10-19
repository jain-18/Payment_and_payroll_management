package com.payment.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.entities.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);

    Page<Employee> findByOrganization_OrganizationId(Long organizationId, Pageable pageable);

    Page<Employee> findByOrganization_OrganizationIdAndEmployeeNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            Long orgId, String keyword, String keyword2, PageRequest pageable);

    List<Employee> findByOrganizationOrganizationId(Long organizationId);
    
    Page<Employee> findByEmployeeNameContainingIgnoreCaseAndOrganization_OrganizationId(
            String employeeName, Long orgId, Pageable pageable);

}
