package com.payment.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.User;

public interface UserRepo extends JpaRepository<User, Long>{

}
