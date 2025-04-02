package com.mi.gateway.util;

import com.mi.gateway.common.exception.CustomTokenException;
import com.mi.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtConfig jwtConfig;

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }
    public Date getExpirationTime(String token) {
        return getClaims(token).getExpiration();
    }

    public boolean validateToken(String token) {
        try {
            byte[] keyBytes = jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8);
            Jwts.parserBuilder()
                    .setSigningKey(new SecretKeySpec(keyBytes, "HmacSHA256"))  // 서명 검증을 위한 키 설정
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ JWT에서 Claims(페이로드) 추출하는 메서드
    private Claims getClaims(String token) {

        byte[] keyBytes = jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8);

        return Jwts.parserBuilder()
                .setSigningKey(new SecretKeySpec(keyBytes, "HmacSHA256"))  // 서명 검증을 위한 키 설정
                .build()
                .parseClaimsJws(token)  // JWT 파싱 및 검증
                .getBody();  // Claims (페이로드) 반환
    }

    public Authentication getAuthentication(String token) {
        byte[] keyBytes = jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(new SecretKeySpec(keyBytes, "HmacSHA256"))  // 서명 검증을 위한 키 설정
                .build()
                .parseClaimsJws(token).getBody();
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

// 🔽 role 문자열을 권한 리스트로 변환
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        // Board 서버에서는 UserDetailsService 없이 직접 UserDetails 생성
        return new UsernamePasswordAuthenticationToken(username, "", authorities);
    }
}