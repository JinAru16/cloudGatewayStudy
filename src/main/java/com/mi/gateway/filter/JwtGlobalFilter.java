package com.mi.gateway.filter;

import com.mi.gateway.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter {
    private final JwtTokenProvider tokenProvider;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        //로그인에 대해서는 검증생략.
        if(path.equals("/api/auth/login")) {
            return chain.filter(exchange);
        }

        HttpCookie httpCookie = exchange.getRequest()
                .getCookies()
                .getFirst("jwt");  // ← 클라이언트가 보내는 쿠키명


        if(httpCookie != null &&  tokenProvider.validateToken(httpCookie.getValue())){
            String username = tokenProvider.getUsername(httpCookie.getValue());
            tokenProvider.getUsername(httpCookie.getValue());
            exchange.getRequest().mutate()
                    .header("X-Auth-ID", username)
                    .build();

            return chain.filter(exchange);
        } else{
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
