package com.payment.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.entities.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);

    Page<Employee> findByOrganization_OrganizationId(Long organizationId, Pageable pageable);
}
