package com.mi.gateway.config;

import com.mi.gateway.common.WhiteListPath;
import com.mi.gateway.security.CustomAuthenticationEntryPoint;
import com.mi.gateway.security.jwt.JwtAuthenticationManager;
import com.mi.gateway.security.jwt.JwtSecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final WhiteListPath whiteListPath;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint; // 인증 실패
    private final JwtAuthenticationManager authenticationManager;
    private final JwtSecurityContextRepository securityContextRepository;

    @Qualifier("blacklistRedisTemplate")
    private final RedisTemplate<String, Object> blacklistRedisTemplate;



    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(whiteListPath.getWhiteList()).permitAll() // 화이트리스트 경로는 허용
                        .anyExchange().authenticated() // 그 외는 인증 필요
                )
                .securityContextRepository(securityContextRepository)
                .authenticationManager(authenticationManager)
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec.authenticationEntryPoint(authenticationEntryPoint)
                )
                .build();
    }
}

