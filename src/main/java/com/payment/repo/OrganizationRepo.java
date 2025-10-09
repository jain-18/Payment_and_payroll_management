package com.payment.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.Organization;
import java.util.List;


public interface OrganizationRepo extends JpaRepository<Organization, Long>{
    Page<Organization> findByIsActive(boolean isActive, Pageable pageable);

}
