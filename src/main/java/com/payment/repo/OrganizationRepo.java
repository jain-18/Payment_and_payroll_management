package com.payment.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.Organization;

public interface OrganizationRepo extends JpaRepository<Organization, Long>{

}
