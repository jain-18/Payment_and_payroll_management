package com.payment.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.payment.entities.Role;
import com.payment.entities.User;
import com.payment.repo.UserRepo;



// Spring Security calls this method (loadUserByUsername) automatically 
// during the authentication process when a user tries to log in.

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	// It fetches user details and roles from the DB and prepares them 
	// for Spring Security’s login authentication.
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));

		Role role = user.getRole();

		//Spring Security needs a list of GrantedAuthority objects to know 
		//what access/permissions a user has.
		
		//A HashSet is created to hold authorities.
		//A SimpleGrantedAuthority is created using the user's role name.
		//Then it is added to the set.
		
		Set<GrantedAuthority> authorities = new HashSet<>();
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName());
		authorities.add(authority);

		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
				authorities);

	}

}
