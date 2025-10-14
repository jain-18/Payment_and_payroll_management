package com.payment.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.payment.security.JwtAuthenticationEntryPoint;
import com.payment.security.JwtAuthenticationFilter;


//This SecurityConfig class:Configures Spring Security to work with 
//JWT-based authentication.

//Sets:
//Password encoding using BCrypt.
//AuthenticationManager for user login.
//Stateless session policy (no server session, JWT used for every request).
//CORS enabled and CSRF disabled.
//Custom JWT filter to validate tokens on every request.
//Custom entry point to handle unauthorized errors.
//Defines which endpoints are public and which are protected.

//In short: This class is the security backbone of your app.
//It ensures only authenticated users with valid JWT tokens can access secured APIs, while allowing open access to login and register endpoints.


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter authenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

   
    public SecurityConfig(UserDetailsService userDetailsService,
                          JwtAuthenticationFilter authenticationFilter,
                          JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.authenticationFilter = authenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    //During login, Spring Security uses passwordEncoder.matches(rawPassword, storedEncodedPassword)
    //behind the scenes to validate credentials.
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //responsible for handling authentication requests 
    //(like verifying username/password during login).
    
    //This is a parameter automatically provided by Spring.
    //It contains the security configuration context, including how authentication should be done 
    //(e.g., using a UserDetailsService, PasswordEncoder, etc.).
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // allow register & login without authentication
                .requestMatchers("/portal/register", "/login/**", "/portal/verify-otp").permitAll()

                // secure student endpoints (require authentication)
                .requestMatchers("/api/**", "/portal/**").authenticated()

                // everything else also requires authentication
                .anyRequest().authenticated()
            );

        // Add JWT filter
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
