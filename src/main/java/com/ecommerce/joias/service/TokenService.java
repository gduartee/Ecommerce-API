package com.ecommerce.joias.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ecommerce.joias.entity.Employee;
import com.ecommerce.joias.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateTokenEmployee(Employee employee) {
       return createToken(employee.getEmail(), "auth-employee", employee.getEmployeeId().toString(), employee.getRole(), employee.getName());
    }

    public String generateTokenUser(User user){
        return createToken(user.getEmail(), "auth-user", user.getUserId().toString(), "USER", user.getName());
    }

    public String createToken(String subject, String issuer, String id, String role, String name){
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(subject)
                    .withClaim("id", id)
                    .withClaim("role", role)
                    .withClaim("name", name)
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public DecodedJWT validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);

            return JWT.require(algorithm)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception){
            return null;
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.of("-03:00"));
    }
}
