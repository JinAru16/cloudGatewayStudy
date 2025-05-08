package com.mi.gateway;

import com.mi.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;


@Component
public class TestTokenProvider {

    private final JwtConfig jwtConfig;

    public TestTokenProvider (JwtConfig jwtConfig){
        this.jwtConfig = jwtConfig;
    };

    public String generateTestAuthToken(String testUsername) {

        byte[] keyBytes = jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8);
        Key key = new SecretKeySpec(keyBytes, "HmacSHA256");  // 또는 Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(testUsername)
                .claim("role", "test_role")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationTime()))
                .signWith(key, SignatureAlgorithm.HS256)  // ✅ 새 방식
                .compact();
    }
}
