package com.mi.gateway.filter;

import com.mi.gateway.common.WhiteListPath;
import com.mi.gateway.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter {
    private final JwtTokenProvider tokenProvider;
    private final WhiteListPath whiteListPath;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        //화이트 리스트에 있는 주소는 검증 생략
        if(path.matches("/users")) {
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
