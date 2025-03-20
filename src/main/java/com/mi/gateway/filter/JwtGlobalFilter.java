package com.mi.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtGlobalFilter implements GlobalFilter {
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
        if(httpCookie != null){
            //검증로직 시작
        }

        return null;
    }
}
