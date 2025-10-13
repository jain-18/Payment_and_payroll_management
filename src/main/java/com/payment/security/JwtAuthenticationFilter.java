package com.payment.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private JwtTokenProvider jwtTokenProvider;

	private UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
		super();
		this.jwtTokenProvider = jwtTokenProvider;
		this.userDetailsService = userDetailsService;
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
	    String path = request.getServletPath();
	    return path.startsWith("/portal/register") || path.startsWith("/portal/verify-otp") || path.startsWith("/login/employee") || path.startsWith("/login/organization") || path.startsWith("/login/admin");
	}

	
	//It runs for every HTTP request coming into your application.
	//Its job is to check for a JWT token in the request, validate it, 
	//and if valid, authenticate the user.
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// get JWT token from http request
		// It calls a helper method getTokenFromRequest(request) 
		// (usually extracts token from the Authorization header: Bearer <token>).
		String token = getTokenFromRequest(request);
		
		// validate token (Only proceed if a valid JWT token is present)
		if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

			//If valid, extracts the username (subject claim) from the token.
			String username = jwtTokenProvider.getUsername(token);

			// load the user associated with token
			//This includes the username, hashed password, and roles/authorities.
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			
			//Create a Spring Security authentication token for the user.
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());

			//Adds request-specific details to the authentication token.
			//Then places the authentication object into the SecurityContext, 
			//which tells Spring Security:"“This user is now authenticated for this request.”
						
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		}

		filterChain.doFilter(request, response);

	}

	//Its job is to extract the JWT token from the request's Authorization header.
	private String getTokenFromRequest(HttpServletRequest request) {

		String bearerToken = request.getHeader("Authorization");

		System.out.println("------------------> " + bearerToken);
		System.out.println("Filtering path: " + request.getServletPath());

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			
			return bearerToken.split(" ")[1].trim();

			// substring(7, bearerToken.length());
		}

		return null;
	}

}
