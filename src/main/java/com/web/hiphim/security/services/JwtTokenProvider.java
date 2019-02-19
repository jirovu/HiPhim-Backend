package com.web.hiphim.security.services;

import com.web.hiphim.repositories.IUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenProvider {
    @Value("${spring.token.secret}")
    private String secret;
    @Value("${spring.token.issuer}")
    private String issuer;
    @Value("${spring.token.expirationTime}")
    private int expirationTime;
    @Autowired
    private IUserRepository userRepository;

    public String generateToken(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        var userExist = userRepository.findByEmail(email);
        return Jwts.builder()
                .setSubject(email)
                .setIssuer(issuer)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}