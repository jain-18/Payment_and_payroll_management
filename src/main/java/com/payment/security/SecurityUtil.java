package com.payment.security;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Long getCurrentOrganizationId() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof Jwt jwt) {
//            return jwt.getClaim("organizationId"); // Ensure this claim exists in JWT
//        }
//        throw new IllegalStateException("Organization ID not found in token");
        return 1L;
    }
}

//@Component
//@RequiredArgsConstructor
//public class SecurityUtil {
//    private final JwtService jwtService;
//
//    public Long getCurrentOrganizationId() {
//        String username = jwtService.extractUsernameFromToken();
//        Organization org = organizationRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
//        return org.getOrganizationId();
//    }
//}

//@Component
//@RequiredArgsConstructor
//public class SecurityUtil {
//
//    private final JwtService jwtService;
//    private final OrganizationRepository organizationRepository;
//
//    public Long getCurrentOrganizationId() {
//        String username = jwtService.extractUsernameFromToken();
//
//        Organization org = organizationRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
//
//        return org.getOrganizationId();
//    }
//}