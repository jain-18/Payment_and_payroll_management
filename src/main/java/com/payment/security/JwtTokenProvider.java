package com.payment.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.payment.entities.User;
import com.payment.exception.UserApiException;
import com.payment.repo.UserRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${app.jwt-secret}")
	private String jwtSecret;

	@Value("${app-jwt-expiration-milliseconds}")
	private long jwtExpirationDate;
	
	@Autowired UserRepo userRepo;

	// This code builds and signs a JWT token containing the username, issue time,
	// expiry time, and user roles, then returns it as a compact string.
	
	public String generateTokenForEmployee(Authentication authentication) {

	    String username = authentication.getName();

	    User user = userRepo.findByUserName(username)
	            .orElseThrow(() -> new IllegalStateException("User not found"));

	    Date currentDate = new Date();
	    Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

	    // Extract roles as a list or string
	    List<String> roles = authentication.getAuthorities().stream()
	            .map(auth -> auth.getAuthority())
	            .collect(Collectors.toList());

	    // Prepare claims map
	    Map<String, Object> claims = new HashMap<>();
	    claims.put("employeeId", user.getEmployee().getEmployeeId());
	    claims.put("organizationId", user.getOrganization().getOrganizationId());
	    claims.put("role", roles);

	    // Build token (modern JJWT fluent API)
	    String token = Jwts.builder()
	            .claims(claims)
	            .subject(username)
	            .issuedAt(currentDate)
	            .expiration(expireDate)
	            .signWith(key())
	            .compact();

	    return token;
	}
	
	public String generateTokenForOrganization(Authentication authentication) {

	    String username = authentication.getName();

	    User user = userRepo.findByUserName(username)
	            .orElseThrow(() -> new IllegalStateException("User not found"));

	    Date currentDate = new Date();
	    Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

	    // Extract roles as a list or string
	    List<String> roles = authentication.getAuthorities().stream()
	            .map(auth -> auth.getAuthority())
	            .collect(Collectors.toList());

	    // Prepare claims map
	    Map<String, Object> claims = new HashMap<>();
	    claims.put("organizationId", user.getOrganization().getOrganizationId());
	    claims.put("role", roles);

	    // Build token (modern JJWT fluent API)
	    String token = Jwts.builder()
	            .claims(claims)
	            .subject(username)
	            .issuedAt(currentDate)
	            .expiration(expireDate)
	            .signWith(key())
	            .compact();

	    return token;
	}
	
	public String generateTokenForAdmin(Authentication authentication) {

		String username = authentication.getName();

		Date currentDate = new Date();

		Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

		String token = Jwts.builder().claims().

				subject(username).issuedAt(new Date(System.currentTimeMillis()))
				.expiration(expireDate).and()
				.signWith(key()).claim("role", authentication.getAuthorities())
				.compact();

		return token;
	}

	private SecretKey key() {

		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	
	// method that accepts a JWT token (as String) and returns the username that was 
	// embedded inside it during token creation.
	public String getUsername(String token) {
		Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();

		String username = claims.getSubject();

		return username;
	}
	
	private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())     // same key used for signing
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
	
	public Long extractEmployeeId(String token) {
        Object value = extractAllClaims(token).get("employeeId");
        return value != null ? Long.parseLong(value.toString()) : null;
    }

    // ✅ Extract organizationId
    public Long extractOrganizationId(String token) {
        Object value = extractAllClaims(token).get("organizationId");
        return value != null ? Long.parseLong(value.toString()) : null;
    }

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key()).build().parse(token);
			return true;
		} catch (MalformedJwtException ex) {
			throw new UserApiException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			throw new UserApiException(HttpStatus.BAD_REQUEST, "Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			throw new UserApiException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			throw new UserApiException(HttpStatus.BAD_REQUEST, "JWT claims string is empty.");
		} catch (Exception e) {
			throw new UserApiException(HttpStatus.BAD_REQUEST, "Invalid Credentials");
		}
	}

}
