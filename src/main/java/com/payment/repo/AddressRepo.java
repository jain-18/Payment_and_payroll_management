package com.payment.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.Address;

public interface AddressRepo extends JpaRepository<Address, Long> {

}
