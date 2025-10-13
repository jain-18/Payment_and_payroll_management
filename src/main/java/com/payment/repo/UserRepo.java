package com.payment.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entities.User;

public interface UserRepo extends JpaRepository<User, Long>{

	Optional<User> findByUserName(String userName);
}
