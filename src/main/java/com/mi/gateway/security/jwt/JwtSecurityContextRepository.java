package com.mi.gateway.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationManager authenticationManager;

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String token = extractToken(exchange);
        if (token == null) return Mono.empty();

        Authentication auth = new UsernamePasswordAuthenticationToken(null, token);
        return authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty(); // Stateless 처리
    }

    private String extractToken(ServerWebExchange exchange) {
        return Optional.ofNullable(
                exchange.getRequest().getCookies().getFirst("jwt")
        ).map(HttpCookie::getValue).orElse(null);
    }
}