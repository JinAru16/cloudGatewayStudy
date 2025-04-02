package com.mi.gateway.security.jwt;

import com.mi.gateway.common.exception.CustomTokenException;
import com.mi.gateway.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtTokenProvider tokenProvider;

    @Qualifier("blacklistRedisTemplate")
    private final RedisTemplate<String, Object> authRedisTemplate;


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        if (!tokenProvider.validateToken(token) || authRedisTemplate.hasKey(token)) {
            return Mono.error(new CustomTokenException("토큰이 유효하지않습니다."));
        }

        String username = tokenProvider.getUsername(token);
        String role = tokenProvider.getRole(token);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        return Mono.just(new UsernamePasswordAuthenticationToken(username, null, authorities));
    }
}