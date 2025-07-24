package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

// 1. @Component Annotation
//    - Marks this class as a Spring component for dependency injection.
@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    // Secret key loaded from application properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // 2. Constructor Injection for Dependencies
    //    - Injects repositories for admin, doctor, and patient validation.
    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // 3. getSigningKey Method
    //    - Retrieves the HMAC SHA SecretKey from the configured secret string.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 4. generateToken Method
    //    - Generates a JWT token with subject=email, issuedAt, expiration (7 days), and signs it.
    public String generateToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + TimeUnit.DAYS.toMillis(7));
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 5. extractEmail Method
    //    - Parses the token to extract the subject (email) after validation.
    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 6. validateToken Method
    //    - Validates token for a given user role by checking repository for extracted email.
    public boolean validateToken(String role, String token) {
        try {
            String email = extractEmail(token);
            Optional<?> userOpt;
            switch (role.toLowerCase()) {
                case "admin":
                    userOpt = adminRepository.findByUsername(email);
                    break;
                case "doctor":
                    userOpt = doctorRepository.findByEmail(email);
                    break;
                case "patient":
                    userOpt = patientRepository.findByEmail(email);
                    break;
                default:
                    return false;
            }
            return userOpt.isPresent();
        } catch (Exception e) {
            // invalid token or parsing error
            return false;
        }
    }
}
