package com.mi.gateway.filter;

import com.mi.gateway.common.WhiteListPath;
import com.mi.gateway.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
/*
* jwt 종류, 사용자의 권한에 따라 endPoint 접근제한.
* */
@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter {
    private final JwtTokenProvider tokenProvider;
    private final WhiteListPath whiteListPath;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getRawPath();

        // 화이트리스트 패스
        if (Arrays.stream(whiteListPath.getWhiteList()).anyMatch(path::matches)) {
            return chain.filter(exchange);
        }

        HttpCookie httpCookie = extractToken(exchange);

        if(httpCookie != null &&  tokenProvider.validateToken(httpCookie.getValue())){
            String username = tokenProvider.getUsername(httpCookie.getValue());
            String role = tokenProvider.getRole(httpCookie.getValue());

            // 인증 객체 생성
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority(role))
            );

            tokenProvider.getUsername(httpCookie.getValue());
            exchange.getRequest().mutate()
                    .header("X-Auth-ID", username)
                    .build();

            // SecurityContext 설정
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        } else{
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private HttpCookie extractToken(ServerWebExchange exchange) {
        return exchange.getRequest()
                .getCookies()
                .getFirst("jwt"); // ← "jwt"는 쿠키 이름
    }
}
