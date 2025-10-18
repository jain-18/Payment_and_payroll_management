package com.payment.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.Account;

public interface AccountRepo extends JpaRepository<Account, Long>{
	
	boolean existsByAccountNumber(String accountNumber);
	Optional<Account> findByAccountNumber(String accountNumber);
}
