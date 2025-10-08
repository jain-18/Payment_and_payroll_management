package com.payment.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.Account;

public interface AccountRepo extends JpaRepository<Account, Long>{

}
