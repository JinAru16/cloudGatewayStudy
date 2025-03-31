package com.mi.gateway.filter;

import com.mi.gateway.common.WhiteListPath;
import com.mi.gateway.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
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

           /*
            * 게이트웨이서  이미 분기가 된 이후기때문에 Predicate가 제거된 whiteListPath를 사용.
            * ex /users/api/auth/login -> /api/auth/login
            * */
        if (Arrays.stream(whiteListPath.getStrippedWhiteList()).anyMatch(path::matches)) {
            return chain.filter(exchange);
        }

        HttpCookie httpCookie = extractToken(exchange);

        // 토큰이 유효하면 SecurityContext의 인증정보를 주입.  -> 인증된 사용자 처리.
        if(httpCookie != null &&  tokenProvider.validateToken(httpCookie.getValue())){
            String username = tokenProvider.getUsername(httpCookie.getValue());
            String role = tokenProvider.getRole(httpCookie.getValue());

            tokenProvider.getUsername(httpCookie.getValue());
            exchange.getRequest().mutate()
                    .header("X-Auth-ID", username)
                    .header("X-Auth-Role", role)
                    .build();

            return chain.filter(exchange);

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
